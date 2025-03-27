package tiny_url.app.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tiny_url.app.backend.component.SnowflakeIdGenerator;

@Configuration //định nghĩa một bean cho SnowflakeIdGenerator trong cấu hình của ứng dụng
public class SnowflakeConfig {
    @Value("${snowflake.machine-id}")
    private long machineId;

    @Value("${snowflake.datacenter-id}")
    private long datacenterId;

    @Bean// tạo bean configuration để Spring quản lý SnowflakeIdGenerator và Inject nó vào UrlService
    public SnowflakeIdGenerator snowflakeIdGenerator() {
        System.out.println("🚀 Datacenter ID: " + datacenterId);
        System.out.println("🚀 Machine ID: " + machineId);
        return new SnowflakeIdGenerator(datacenterId, machineId);
    }
}
