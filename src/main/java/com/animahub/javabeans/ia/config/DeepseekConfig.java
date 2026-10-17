package com.animahub.javabeans.ia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeepseekConfig {

    private final String apiKey;
    private final String endpoint;

    public DeepseekConfig(
            @Value("${deepseek.key}") String apiKey,
            @Value("${deepseek.endpoint}") String endpoint) {
        this.apiKey = apiKey;
        this.endpoint = endpoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getEndpoint() {
        return endpoint;
    }
}