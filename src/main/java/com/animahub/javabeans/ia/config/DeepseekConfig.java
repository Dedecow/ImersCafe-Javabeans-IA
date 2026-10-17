package com.animahub.javabeans.ia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class DeepseekConfig {

    private final String apiKey;
    private final String endpoint;
    private final Duration timeout;

    public DeepseekConfig(
            @Value("${deepseek.key}") String apiKey,
            @Value("${deepseek.endpoint}") String endpoint,
            @Value("${deepseek.timeout}") String timeout) {
        this.apiKey = apiKey;
        this.endpoint = endpoint;
        this.timeout = DurationStyle.detectAndParse(timeout);
        if (this.timeout.isZero() || this.timeout.isNegative()) {
            throw new IllegalArgumentException("deepseek.timeout deve ser positivo.");
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Duration getTimeout() {
        return timeout;
    }
}
