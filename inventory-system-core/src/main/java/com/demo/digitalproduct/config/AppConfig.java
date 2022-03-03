package com.demo.digitalproduct.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "digital-product.config")
public class AppConfig {

    private String productExternalInfoEndpoint;

    private Cache cache;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cache {
        private String host;
        private int port;
        private String password;
        private int duration;
    }
}
