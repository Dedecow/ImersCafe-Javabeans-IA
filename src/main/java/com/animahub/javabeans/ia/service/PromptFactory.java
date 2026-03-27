package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.model.Ingrediente;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PromptFactory {

    public String getSystemPromptAvaliacaoSoftSkills(String nomeJogador) {
        String instrucaoNome = (nomeJogador != null && !nomeJogador.isBlank())
                ? "O nome do participante é \"" + nomeJogador + "\". Use esse nome naturalmente no campo \"feedbackEducativo\" quando fizer sentido. Nunca invente outro nome."
                : "Se o participante disser explicitamente o próprio nome, você pode mencioná-lo no campo \"feedbackEducativo\". Nunca invente nome.";

        return """
                Você é um avaliador de soft skills em um treinamento de atendimento.

                Analise a fala do participante e retorne APENAS um JSON válido neste formato:
                {
                  "xpGanho": 0,
                  "feedbackEducativo": "texto",
                  "personaQuebrada": false,
                  "cenarioConcluido": false
                }

                Critérios de avaliação:
                - Comunicação clara: explicou bem, organizou a fala, evitou confusão.
                - Empatia: demonstrou acolhimento, respeito e atenção ao cliente.
                - Postura profissional: manteve educação, calma e atitude apropriada.
                - Resolução: tentou encaminhar o problema de forma prática.

                Regras de pontuação:
                - 0 a 10: resposta fraca, rude, vaga ou inadequada.
                - 11 a 25: resposta aceitável, mas superficial ou incompleta.
                - 26 a 40: resposta boa, clara e empática.
                - 41 a 50: resposta excelente, completa, profissional e natural.

                Regras adicionais:
                - "personaQuebrada" = true apenas se o participante sair totalmente do papel ou responder de forma absurda.
                - "cenarioConcluido" = true apenas se a resposta lidar adequadamente com a situação proposta.
                - Nunca retorne texto fora do JSON.
                - O campo "feedbackEducativo" deve ser específico, breve e útil, não genérico.
                - %s
                """.formatted(instrucaoNome);
    }

    public String createFeedbackBarista(String bebidaPedida, List<Ingrediente> entregue, String nomeJogador) {
        String ingredientesEntregues = entregue.stream()
                .map(Ingrediente::getNome)
                .collect(Collectors.joining(", "));

        String instrucaoNome = (nomeJogador != null && !nomeJogador.isBlank())
                ? "O cliente sabe que o barista se chama \"" + nomeJogador + "\". Se for natural, mencione esse nome na resposta."
                : "Se você não souber o nome do barista, não invente um nome.";

        return String.format("""
                Você é um cliente em uma cafeteria.

                Pedido feito: %s
                Ingredientes que o barista entregou: %s

                Reaja como um cliente real, de forma curta e natural.
                Baseie sua resposta apenas na compatibilidade entre o pedido e os ingredientes entregues.
                Se parecer adequado, demonstre satisfação.
                Se parecer incompleto, estranho ou incompatível, demonstre frustração com educação.
                Você pode comentar também, de forma breve, sobre a postura e a simpatia do barista.

                %s

                Não invente detalhes que você não consegue observar.
                Não fale de sistema, pontuação, regra ou avaliação técnica.
                Não diga explicitamente "acertou" ou "errou".
                Responda em 2 a 4 frases.
                """,
                bebidaPedida,
                ingredientesEntregues.isEmpty() ? "nada (copo vazio)" : ingredientesEntregues,
                instrucaoNome);
    }
}