package tiny_url.app.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.BloomFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tiny_url.app.backend.component.SnowflakeIdGenerator;
import tiny_url.app.backend.entity.UrlEntity;
import tiny_url.app.backend.repository.UrlRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final BloomFilter<String> shortUrlBloomFilter;
    private final SnowflakeIdGenerator idGenerator;
    private final String TOPIC = "url-clicks";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String PREFIX_REDIS = "tinyurl:";
    // Bộ ký tự để encode Base62
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public UrlService (UrlRepository urlRepository, BloomFilter<String> shortUrlBloomFilter, SnowflakeIdGenerator idGenerator) {
        this.urlRepository = urlRepository;
        this.shortUrlBloomFilter = shortUrlBloomFilter;
        //this.idGenerator = new SnowflakeIdGenerator(1L); // tạo 1 instance bằng trực tiếp thay vì để spring inject một bean.
        this.idGenerator = idGenerator; // Spring Inject bean vào - Cần tạo bean configuration để Spring quản lý SnowflakeIdGenerator
    }

    // Func shortenUrl
    public String shortenUrl(String longUrl) {
        // kiểm tra longUrl
        Optional<UrlEntity> optionalUrl = urlRepository.findByLongUrl(longUrl);

        if (!optionalUrl.isPresent()) {
            //Long id = Instant.now().toEpochMilli();
            long id;
            String shortUrl = "";
            do {
                id = idGenerator.nextId(); // Dùn SnowFlake tạo id
                shortUrl = encodeBase62(id);

                // kiểm tra shortURL sau khi tạo đã tồn tại chưa, nếu ko tồn tại -> pass -> lưu vào db & cập nhật Bloom Filter.
                if (!shortUrlBloomFilter.mightContain(shortUrl)) {
                    break;
                }
                // nếu tồn tại => tạo lại (dùng Snow Flake thì khó mà trung được, trùng xảy ra khi ta lấy một phần của shortUrl - đương nhiên là trong điều kiện lượng người dùng, yêu cầu lớn)
            }
            while (true);

            //lưu vào db
            UrlEntity urlEntity = new UrlEntity(id, shortUrl, longUrl);
            urlRepository.save(urlEntity);
            decodeSnowflakeId(id);
            System.out.println("Generated ID: " + id);
            System.out.println("Binary ID: " + Long.toBinaryString(id));

            // cập nhật Bloom Filter
            shortUrlBloomFilter.put(shortUrl);
            System.out.println("Bloom Filter: " + shortUrlBloomFilter);

            // cache: lưu vào redis ngay khi tạo thành công
            redisTemplate.opsForValue().set(PREFIX_REDIS + shortUrl, longUrl, 24, TimeUnit.HOURS);

            return shortUrl;
        } else {
            // cache: lưu vào redis
            redisTemplate.opsForValue().set(PREFIX_REDIS + optionalUrl.get().getShortUrl(), longUrl, 24, TimeUnit.HOURS);

            return optionalUrl.get().getShortUrl();
        }
    }

    // Encode Base62
    private String encodeBase62(Long id) {
        // chia id cho 62 tìm cơ số của lũy thừa x -> tim ký tự vị trí x tương ứng trong BASE62_ALPHABET
        StringBuilder shortUrl = new StringBuilder();
        while(id > 0) {
            shortUrl.append(BASE62_ALPHABET.charAt((int) (id % 62)));

            id /=62;
        }

        return shortUrl.reverse().toString();
    }

    // Decode Base62
    public void decodeSnowflakeId(long id) {
        long timestamp = (id >> 22) + 1672531200000L; // lấy 41 bits đầu
        long datacenterId = (id >> 17) & 0x1F; // lấy 5 bits tiếp theo
        long machineId = (id >> 12) & 0x1F; // lấy 5 bits tiếp theo (vị trí lấy) & so_bit_lay
        long sequence = id & 0xFFF; // 12 bits cuối

        System.out.println("Timestamp: " + timestamp + " (" + Instant.ofEpochMilli(timestamp) + ") ");
        System.out.println("Datacenter ID: " + datacenterId);
        System.out.println("Machine ID: " + machineId);
        System.out.println("Sequence: " + sequence);
    }

    // Get original URL
    public String getLongUrl(String shortUrl) {
        String cachedUrl = (String) redisTemplate.opsForValue().get(PREFIX_REDIS + shortUrl);
        if (cachedUrl != null) {
            System.out.println("Cached {ShortURL : LongURL} 💨🚀🚁🚀");

            return cachedUrl;
        } else {
            String longUrl = urlRepository.findByShortUrl(shortUrl).map(UrlEntity::getLongUrl).orElseThrow(() -> new NoSuchElementException("Không tìm thấy long URL tương ứng với Short URL."));//orElse("Không tìm thấy Long URL tương ứng với Short URL đã chọn.");
            // cache: lưu vào redis
            redisTemplate.opsForValue().set(PREFIX_REDIS + shortUrl, longUrl, 24, TimeUnit.HOURS);

            return longUrl;
        }

//        UrlEntity urlEntity = urlRepository.findByShortUrl(shortUrl).get();
//        if(urlEntity != null) {
//            String longUrl = urlEntity.getLongUrl();
//
//            // cached
//            redisTemplate.opsForValue().set(PREFIX_REDIS + shortUrl, longUrl, 24, TimeUnit.HOURS);
//            return longUrl;
//        }
//
//        return null;

    }



    // Func log click
    public void logClick(String shortUrl, HttpServletRequest request) {
        try {
            Map<String, Object> logData = new HashMap<>();
            logData.put("shortUrl", shortUrl);
            logData.put("timestamp", Instant.now().toString());
            logData.put("ip", request.getRemoteAddr());
            logData.put("userAgent", request.getHeader("User-Agent"));

            String logJson = objectMapper.writeValueAsString(logData);

            kafkaTemplate.send(TOPIC, logJson);
            System.out.println("📤 Log sent to Kafka: " + logJson);
        } catch (Exception e) {
            System.err.println("❌ JSON Serialization Error: " + e.getMessage());
        }
    }

}































