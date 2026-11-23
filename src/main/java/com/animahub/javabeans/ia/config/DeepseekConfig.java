package com.animahub.javabeans.ia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeepseekConfig {

    // Valores vêm do application.yml, que lê as variáveis de ambiente DEEPSEEK_API_KEY, DEEPSEEK_ENDPOINT
    // e DEEPSEEK_TIMEOUT_SEGUNDOS
    private final String apiKey;
    private final String endpoint;
    private final int timeoutSegundos;

    public DeepseekConfig(@Value("${deepseek.key}") String apiKey,
                          @Value("${deepseek.endpoint}") String endpoint,
                          @Value("${deepseek.timeout-segundos:15}") int timeoutSegundos) {
        this.apiKey = apiKey;
        this.endpoint = endpoint;
        this.timeoutSegundos = timeoutSegundos;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public int getTimeoutSegundos() {
        return timeoutSegundos;
    }
}
