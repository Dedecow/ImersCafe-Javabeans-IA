package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.config.DeepseekConfig;
import com.animahub.javabeans.ia.model.Ingrediente;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class AIService {

    private static final Logger log = LoggerFactory.getLogger(AIService.class);

    static final String FEEDBACK_INDISPONIVEL =
            "Feedback da IA indisponível no momento.";

    // Limite de tokens gerados: o tempo de resposta de um LLM cresce com o tamanho da resposta
    private static final int MAX_TOKENS_AVALIACAO = 400; // JSON com feedback breve
    private static final int MAX_TOKENS_FEEDBACK = 250;  // reação de 2 a 4 frases
    private static final int TIMEOUT_CONEXAO_MS = 5000;

    private final WebClient webClient;
    private final Duration timeout;
    private final PromptFactory promptFactory;
    private final MeterRegistry meterRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public AIService(DeepseekConfig config, WebClient.Builder builder, PromptFactory promptFactory,
                     MeterRegistry meterRegistry) {
        this.timeout = config.getTimeout();
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT_CONEXAO_MS)
                .responseTimeout(timeout);
        // Se o builder já tiver uma exchangeFunction (testes), ela tem precedência sobre o conector
        this.webClient = builder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(config.getEndpoint())
                .defaultHeader("Authorization", "Bearer " + config.getApiKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.promptFactory = promptFactory;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Usado nos testes, sem registro de métricas do Spring.
     */
    public AIService(DeepseekConfig config, WebClient.Builder builder, PromptFactory promptFactory) {
        this(config, builder, promptFactory, new SimpleMeterRegistry());
    }

    public String avaliarResposta(String promptJogador, String nomeJogador) {
        String systemPrompt = promptFactory.getSystemPromptAvaliacaoSoftSkills(nomeJogador);

        Map<String, Object> body = Map.of(
                "model", "deepseek-chat",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", promptJogador)
                ),
                "response_format", Map.of("type", "json_object"),
                "max_tokens", MAX_TOKENS_AVALIACAO
        );

        return chamarDeepseek("avaliacao", body);
    }

    public String gerarFeedbackBarista(String bebida, List<Ingrediente> escolhido, String nomeJogador, String falaBarista) {
        String promptUsuario = promptFactory.createFeedbackBarista(bebida, escolhido, nomeJogador, falaBarista);

        Map<String, Object> body = Map.of(
                "model", "deepseek-chat",
                "messages", List.of(
                        Map.of("role", "system", "content", "Você é um cliente reagindo ao atendimento e ao pedido."),
                        Map.of("role", "user", "content", promptUsuario)
                ),
                "max_tokens", MAX_TOKENS_FEEDBACK
        );

        String rawResponse;
        try {
            rawResponse = chamarDeepseek("feedback", body);
        } catch (WebClientException | DeepseekTimeoutException e) {
            return FEEDBACK_INDISPONIVEL;
        }
        return extrairConteudo(rawResponse);
    }

    /**
     * Chama a API com timeout e registra a duração no log e na métrica "javabeans.ia.chamadas"
     * (consultável em /actuator/metrics/javabeans.ia.chamadas).
     */
    private String chamarDeepseek(String operacao, Map<String, Object> body) {
        long inicio = System.nanoTime();
        String resultado = "erro";
        try {
            String resposta = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(timeout)
                    .onErrorMap(TimeoutException.class, DeepseekTimeoutException::new)
                    .block();
            resultado = "sucesso";
            log.info("IA ({}) respondeu em {} ms", operacao, milissegundos(inicio));
            return resposta;
        } catch (RuntimeException e) {
            log.warn("IA ({}) falhou após {} ms: {}", operacao, milissegundos(inicio), e.getMessage());
            throw e;
        } finally {
            Timer.builder("javabeans.ia.chamadas")
                    .description("Duração das chamadas à API de IA")
                    .tag("operacao", operacao)
                    .tag("resultado", resultado)
                    .register(meterRegistry)
                    .record(System.nanoTime() - inicio, TimeUnit.NANOSECONDS);
        }
    }

    private static long milissegundos(long inicioNanos) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - inicioNanos);
    }

    private static final class DeepseekTimeoutException extends RuntimeException {
        private DeepseekTimeoutException(TimeoutException cause) {
            super("Tempo limite da chamada à DeepSeek excedido.", cause);
        }
    }

    private String extrairConteudo(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            JsonNode choices = root.path("choices");

            if (choices.isArray() && !choices.isEmpty()) {
                JsonNode content = choices.get(0).path("message").path("content");
                if (content.isTextual() && !content.textValue().isBlank()) {
                    return content.textValue();
                }
            }

            return FEEDBACK_INDISPONIVEL;
        } catch (Exception e) {
            return FEEDBACK_INDISPONIVEL;
        }
    }
}
