package cn.edu.zjou.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "crawler")
@Data
public class CrawlerConfig {
    private  String startUrl;
}
