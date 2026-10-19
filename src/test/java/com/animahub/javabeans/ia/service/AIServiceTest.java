package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.config.DeepseekConfig;
import com.animahub.javabeans.ia.model.Ingrediente;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class AIServiceTest {

    private static final String FALLBACK =
            "Feedback da IA indisponível no momento.";

    @Test
    void feedbackValidoRetornaTextoOriginal() {
        AIService service = comResposta(HttpStatus.OK, """
                {"choices":[{"message":{"content":"  Café muito bom!  "}}]}
                """);

        assertEquals("  Café muito bom!  ", gerarFeedback(service));
    }

    @Test
    void http500RetornaFallback() {
        AIService service = comResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno simulado"
        );

        assertEquals(FALLBACK, gerarFeedback(service));
    }

    @Test
    void falhaDeRedeRetornaFallback() {
        AIService service = criarService(request ->
                Mono.error(new WebClientRequestException(
                        new IOException("Falha de rede simulada"),
                        request.method(),
                        request.url(),
                        request.headers()
                )), "15s");

        assertEquals(FALLBACK, gerarFeedback(service));
    }

    @Test
    void timeoutRetornaFallback() {
        AIService service = criarService(request -> Mono.never(), "100ms");

        assertTimeoutPreemptively(Duration.ofSeconds(5), () ->
                assertEquals(FALLBACK, gerarFeedback(service))
        );
    }

    @Test
    void corpoVazioRetornaFallback() {
        AIService service = criarService(request ->
                Mono.just(ClientResponse.create(HttpStatus.OK).build()), "15s");

        assertEquals(FALLBACK, gerarFeedback(service));
    }

    @Test
    void jsonExternoInvalidoRetornaFallback() {
        AIService service = comResposta(HttpStatus.OK, "{");

        assertEquals(FALLBACK, gerarFeedback(service));
    }

    @Test
    void choicesVazioRetornaFallback() {
        AIService service = comResposta(HttpStatus.OK, """
                {"choices":[]}
                """);

        assertEquals(FALLBACK, gerarFeedback(service));
    }

    @Test
    void contentVazioRetornaFallback() {
        AIService service = comResposta(HttpStatus.OK, """
                {"choices":[{"message":{"content":""}}]}
                """);

        assertEquals(FALLBACK, gerarFeedback(service));
    }

    private AIService comResposta(HttpStatus status, String body) {
        return criarService(request ->
                Mono.just(ClientResponse.create(status)
                        .header("Content-Type", "application/json")
                        .body(body)
                        .build()), "15s");
    }

    private AIService criarService(ExchangeFunction exchange, String timeout) {
        return new AIService(
                new DeepseekConfig(
                        "chave-ficticia",
                        "https://deepseek.invalid",
                        timeout
                ),
                WebClient.builder().exchangeFunction(exchange),
                new PromptFactory()
        );
    }

    private String gerarFeedback(AIService service) {
        return service.gerarFeedbackBarista(
                "Cappuccino",
                List.of(Ingrediente.CAFE, Ingrediente.LEITE, Ingrediente.ESPUMA),
                "Ana",
                "Aqui está seu café."
        );
    }
}
