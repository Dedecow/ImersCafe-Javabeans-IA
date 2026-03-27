package com.animahub.javabeans.ia.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DeepseekConfig {

    // Insira sua chave manualmente entre as aspas abaixo
    private final String apiKey = "sk-0e03572130ff4bd6bd83286b4fe82702";
    private final String endpoint = "https://api.deepseek.com";

    public String getApiKey() {
        return apiKey;
    }

    public String getEndpoint() {
        return endpoint;
    }
}