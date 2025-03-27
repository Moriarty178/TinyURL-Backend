package tiny_url.app.backend.config;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class BloomFilterConfig {

    @Bean
    public BloomFilter<String> shortUrlBloomFilter() {
        return BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8),
                1_000_000, // số lượng bạn ghi ước tính (expected)
                0.01); // xs lỗi
    }
}
