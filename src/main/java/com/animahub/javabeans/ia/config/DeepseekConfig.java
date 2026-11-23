package com.animahub.javabeans.ia.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class DeepseekConfig {

    private static final String ENDPOINT_PADRAO = "https://api.deepseek.com";

    // Valores vêm do application.yml, que lê as variáveis de ambiente DEEPSEEK_API_KEY,
    // DEEPSEEK_ENDPOINT e DEEPSEEK_TIMEOUT (ex.: 15s)
    private final String apiKey;
    private final String endpoint;
    private final Duration timeout;

    @Autowired
    public DeepseekConfig(@Value("${deepseek.key:}") String apiKey,
                          @Value("${deepseek.endpoint:" + ENDPOINT_PADRAO + "}") String endpoint,
                          @Value("${deepseek.timeout:15s}") String timeout) {
        this.apiKey = apiKey;
        this.endpoint = endpoint;
        this.timeout = DurationStyle.detectAndParse(timeout);
        if (this.timeout.isZero() || this.timeout.isNegative()) {
            throw new IllegalArgumentException("deepseek.timeout deve ser positivo.");
        }
    }

    /**
     * Usado nos testes: só o timeout, sem chave e com o endpoint padrão.
     */
    public DeepseekConfig(String timeout) {
        this("", ENDPOINT_PADRAO, timeout);
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
