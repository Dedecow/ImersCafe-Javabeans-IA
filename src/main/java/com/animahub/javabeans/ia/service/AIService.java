package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.config.DeepseekConfig;
import com.animahub.javabeans.ia.dto.AnaliseIADTO;
import com.animahub.javabeans.ia.dto.AvaliacaoDTO;
import com.animahub.javabeans.ia.model.Ingrediente;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AIService implements com.animahub.javabeans.ia.service.ai.AIService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public AIService(DeepseekConfig config, WebClient.Builder builder, ObjectMapper objectMapper) {
        this.webClient = builder
                .baseUrl(config.getEndpoint())
                .defaultHeader("Authorization", "Bearer " + config.getApiKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.objectMapper = objectMapper;
    }

    public AvaliacaoDTO avaliarResposta(String promptJogador) {
        Map<String, Object> body = Map.of(
                "model", "deepseek-chat",
                "messages", List.of(
                        Map.of("role", "system", "content", "Você é um avaliador de soft skills. RESPONDA SEMPRE APENAS COM UM JSON VÁLIDO NO FORMATO: {\"xpGanho\":<numero>, \"feedbackEducativo\":\"texto\", \"personaQuebrada\":<true|false>, \"cenarioConcluido\":<true|false>}"),
                        Map.of("role", "user", "content", promptJogador)
                ),
                "response_format", Map.of("type", "json_object")
        );

        try {
            String rawResponse = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(rawResponse);
            String conteudoIA = root.path("choices").get(0).path("message").path("content").asText();

            // Limpa possíveis blocos de código Markdown que a IA possa incluir.
            String jsonLimpo = conteudoIA
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            return objectMapper.readValue(jsonLimpo, AvaliacaoDTO.class);

        } catch (Exception e) {
            log.error("Erro ao validar JSON da IA: {}", e.getMessage(), e);
            return new AvaliacaoDTO(0, "Resposta inválida da IA. Tente novamente.", false, false);
        }
    }

    public String gerarFeedbackBarista(String bebida, List<Ingrediente> escolhido, List<Ingrediente> esperado, boolean acertou) {
        String tom = acertou ? "entusiasmado e elogiador" : "construtivo, educado, mas honesto";
        String abertura = acertou ? "Ótimo! O sabor está incrível!" : "Hmm... não foi exatamente isso que eu esperava...";

        String prompt = String.format("""
                Você é um cliente em uma cafeteria, falando diretamente com o barista.
                Tom: %s.
                Comece com: "%s"

                Bebida pedida: %s
                Ingredientes corretos esperados: %s
                O que o barista entregou: %s

                Dê feedback curto (2-4 frases), realista e imersivo.
                Se errou muito, seja sincero mas não agressivo.
                Nunca elogie se estiver claramente errado.
                """, tom, abertura, bebida, esperado, escolhido);

        Map<String, Object> body = Map.of(
                "model", "deepseek-chat",
                "messages", List.of(
                        Map.of("role", "system", "content", "Você é um cliente reagindo ao pedido."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        try {
            String rawResponse = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            // 🔥 O PULO DO GATO: Desempacota o JSON e pega só o texto!
            JsonNode root = objectMapper.readTree(rawResponse);
            return root.path("choices").get(0).path("message").path("content").asText();

        } catch (Exception e) {
            return "Desculpe, erro ao processar a resposta da IA: " + e.getMessage();
        }
    }

    /**
     * Avalia a competência comunicativa do jogador de forma agnóstica às regras de negócio.
     *
     * Conformidade: Qin et al. (2025) - Competência comunicativa avaliada pela IA
     *
     * @param respostaJogador Texto da resposta fornecida pelo jogador
     * @param cenarioAtual    Identificador ou descrição do cenário atual
     * @param contexto        Contexto adicional necessário para avaliação
     * @return AnaliseIADTO com competência comunicativa (0-100) e feedback educativo
     */
    @Override
    public AnaliseIADTO avaliarCompetenciaComunicativa(String respostaJogador, String cenarioAtual, String contexto) {
        String prompt = String.format("""
                Você é um avaliador de competência comunicativa em um simulador educacional.
                Cenário atual: %s
                Contexto: %s
                Resposta do jogador: %s

                RESPONDA APENAS COM UM JSON VÁLIDO NO FORMATO:
                {"competenciaComunicativa":<numero_de_0_a_100>, "feedback":"<texto_educativo>"}
                """, cenarioAtual, contexto, respostaJogador);

        Map<String, Object> body = Map.of(
                "model", "deepseek-chat",
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "Você é um avaliador de competência comunicativa. RESPONDA APENAS COM JSON VÁLIDO."),
                        Map.of("role", "user", "content", prompt)
                ),
                "response_format", Map.of("type", "json_object")
        );

        try {
            String rawResponse = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(rawResponse);
            String conteudoIA = root.path("choices").get(0).path("message").path("content").asText();

            String jsonLimpo = conteudoIA
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            JsonNode resultado = objectMapper.readTree(jsonLimpo);
            double competencia = resultado.path("competenciaComunicativa").asDouble(0.0);
            String feedback = resultado.path("feedback").asText("Sem feedback disponível.");

            return new AnaliseIADTO(competencia, feedback);

        } catch (Exception e) {
            log.error("Erro ao avaliar competência comunicativa: {}", e.getMessage(), e);
            return new AnaliseIADTO(0.0, "Erro ao processar avaliação. Tente novamente.");
        }
    }
}
