package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.config.DeepseekConfig;
import com.animahub.javabeans.ia.model.Ingrediente;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.List;
import java.util.Map;

@Service
public class AIService {

    private static final String FEEDBACK_INDISPONIVEL =
            "Feedback da IA indisponível no momento.";

    private final WebClient webClient;
    private final PromptFactory promptFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIService(DeepseekConfig config, WebClient.Builder builder, PromptFactory promptFactory) {
        this.webClient = builder
                .baseUrl(config.getEndpoint())
                .defaultHeader("Authorization", "Bearer " + config.getApiKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.promptFactory = promptFactory;
    }

    public String avaliarResposta(String promptJogador, String nomeJogador) {
        String systemPrompt = promptFactory.getSystemPromptAvaliacaoSoftSkills(nomeJogador);

        Map<String, Object> body = Map.of(
                "model", "deepseek-chat",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", promptJogador)
                ),
                "response_format", Map.of("type", "json_object")
        );

        return chamarDeepseek(body);
    }

        public String gerarFeedbackBarista(String bebida, List<Ingrediente> escolhido, String nomeJogador, String falaBarista) {
        String promptUsuario = promptFactory.createFeedbackBarista(bebida, escolhido, nomeJogador, falaBarista);

        Map<String, Object> body = Map.of(
            "model", "deepseek-chat",
            "messages", List.of(
                Map.of("role", "system", "content", "Você é um cliente reagindo ao atendimento e ao pedido."),
                Map.of("role", "user", "content", promptUsuario)
            )
        );

        String rawResponse;
        try {
            rawResponse = chamarDeepseek(body);
        } catch (WebClientException e) {
            return FEEDBACK_INDISPONIVEL;
        }
        return extrairConteudo(rawResponse);
        }

    private String chamarDeepseek(Map<String, Object> body) {
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private String extrairConteudo(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            JsonNode choices = root.path("choices");

            if (choices.isArray() && !choices.isEmpty()) {
                return choices.get(0).path("message").path("content").asText();
            }

            return FEEDBACK_INDISPONIVEL;
        } catch (Exception e) {
            return FEEDBACK_INDISPONIVEL;
        }
    }
}