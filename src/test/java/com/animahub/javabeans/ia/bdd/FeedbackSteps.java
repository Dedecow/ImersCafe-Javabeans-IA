
package com.animahub.javabeans.ia.bdd;

import com.animahub.javabeans.ia.config.DeepseekConfig;
import com.animahub.javabeans.ia.model.Ingrediente;
import com.animahub.javabeans.ia.service.AIService;
import com.animahub.javabeans.ia.service.PromptFactory;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FeedbackSteps {

    private static final String FEEDBACK_INDISPONIVEL =
            "Feedback da IA indisponível no momento.";

    private AIService aiService;
    private String feedback;

    @Dado("que o jogador finalizou o atendimento")
    public void jogadorFinalizouAtendimento() {
        ExchangeFunction exchange = request ->
                Mono.error(new WebClientRequestException(
                        new IOException("Falha de conexão simulada"),
                        request.method(),
                        request.url(),
                        request.headers()
                ));

        aiService = new AIService(
                new DeepseekConfig("15s"),
                WebClient.builder().exchangeFunction(exchange),
                new PromptFactory()
        );
    }

    @Quando("a integração com a DeepSeek estiver indisponível")
    public void integracaoDeepseekIndisponivel() {
        feedback = aiService.gerarFeedbackBarista(
                "Cappuccino",
                List.of(
                        Ingrediente.CAFE,
                        Ingrediente.LEITE,
                        Ingrediente.ESPUMA
                ),
                "Ana",
                "Aqui está seu café."
        );
    }

    @Então("o sistema deve retornar uma mensagem de indisponibilidade")
    public void sistemaRetornaMensagemIndisponibilidade() {
        assertEquals(FEEDBACK_INDISPONIVEL, feedback);
    }
}
