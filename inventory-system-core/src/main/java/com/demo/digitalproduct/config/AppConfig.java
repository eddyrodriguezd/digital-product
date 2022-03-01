package com.demo.digitalproduct.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "digital-product.config")
public class AppConfig {

    private String productExternalInfoEndpoint;
}
