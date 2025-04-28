package tiny_url.app.backend.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

//@Component
public class KafkaConsumerService {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "url-clicks", groupId = "tracking-group")
    public void consume(String logData) {
        try {
            // Chuyển json -> map
            Map<String, Object> logMap = objectMapper.readValue(logData, new TypeReference<Map<String, Object>>(){});
            Document document = Document.from(logMap);

            // Lưu vào Elasticsearch (index = "click_logs)
            // elasticsearchOperations.save(document, IndexCoordinates.of("click_logs"));
            // System.out.println("✅ Log saved to Elasticsearch: " + logMap);
        } catch (Exception e) {
            System.err.println("❌ Error saving log to Elasticsearch: " + e.getMessage());
        }
    }
}
