package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.dto.AvaliacaoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class AvaliacaoIAParser {

    private final ObjectMapper objectMapper;

    public AvaliacaoIAParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AvaliacaoDTO parse(String respostaRawIA) throws JsonProcessingException {
        if (respostaRawIA == null || respostaRawIA.isBlank()) {
            throw new IllegalArgumentException("Resposta da IA inválida.");
        }

        JsonNode rootNode = objectMapper.readTree(respostaRawIA);

        if (rootNode == null || !rootNode.isObject()) {
            throw new IllegalArgumentException("Resposta da IA inválida.");
        }

        JsonNode choices = rootNode.path("choices");

        if (!choices.isArray() || choices.isEmpty() || !choices.get(0).isObject()) {
            throw new IllegalArgumentException("Resposta da IA inválida.");
        }

        JsonNode message = choices.get(0).path("message");

        if (!message.isObject()) {
            throw new IllegalArgumentException("Resposta da IA inválida.");
        }

        JsonNode content = message.path("content");

        if (!content.isTextual() || content.textValue().isBlank()) {
            throw new IllegalArgumentException("Resposta da IA inválida.");
        }

        JsonNode avaliacaoNode = objectMapper.readTree(content.textValue());

        validarAvaliacao(avaliacaoNode);

        return objectMapper.treeToValue(avaliacaoNode, AvaliacaoDTO.class);
    }

    private void validarAvaliacao(JsonNode avaliacaoNode) {
        if (avaliacaoNode == null || !avaliacaoNode.isObject()) {
            throw new IllegalArgumentException("Avaliação da IA inválida.");
        }

        JsonNode xpGanho = avaliacaoNode.path("xpGanho");
        JsonNode cenarioConcluido = avaliacaoNode.path("cenarioConcluido");
        JsonNode feedbackEducativo = avaliacaoNode.path("feedbackEducativo");
        JsonNode personaQuebrada = avaliacaoNode.path("personaQuebrada");

        if (!xpGanho.isIntegralNumber() || !xpGanho.canConvertToInt()
                || !cenarioConcluido.isBoolean()
                || !feedbackEducativo.isTextual() || feedbackEducativo.textValue().isBlank()
                || !personaQuebrada.isBoolean()) {
            throw new IllegalArgumentException("Avaliação da IA inválida.");
        }
    }
}