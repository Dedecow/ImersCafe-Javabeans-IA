package com.animahub.javabeans.ia.engine;

import com.animahub.javabeans.ia.dto.AvaliacaoDTO;
import com.animahub.javabeans.ia.dto.CenarioDTO;
import com.animahub.javabeans.ia.service.AIService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class GameEngine {

    private final SessionEngine sessionEngine;
    private final AIService aiService;
    private final ObjectMapper objectMapper;

    public GameEngine(SessionEngine sessionEngine, AIService aiService, ObjectMapper objectMapper) {
        this.sessionEngine = sessionEngine;
        this.aiService = aiService;
        this.objectMapper = objectMapper;
    }

    public CenarioDTO carregarCenarioInicial() {
        return new CenarioDTO(
                "CEN-001",
                "O Cliente Apressado",
                "Um cliente entra na cafeteria pedindo um espresso para viagem, mas o sistema de pagamentos acabou de cair.",
                "Comunicação Clara e Resolução de Problemas sob Pressão"
        );
    }

    public AvaliacaoDTO processarTurno(String sessionId, String respostaAluno, String nomeJogador) {
        if (!sessionEngine.isSessaoValida(sessionId)) {
            throw new IllegalArgumentException("Sessão inválida ou expirada.");
        }

        try {
            TipoCliente cliente = sortearTipoCliente();
            int pontosBonusCliente = cliente.getPontos();

            String respostaRawIA = aiService.avaliarResposta(respostaAluno, nomeJogador);

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

            AvaliacaoDTO avaliacao = objectMapper.treeToValue(avaliacaoNode, AvaliacaoDTO.class);

            avaliacao.setTipoCliente(cliente);

            if (avaliacao.isCenarioConcluido()) {
                avaliacao.setXpGanho(avaliacao.getXpGanho() + pontosBonusCliente);
            } else {
                avaliacao.setXpGanho(avaliacao.getXpGanho() + pontosBonusCliente / 2);
            }

            return avaliacao;

        } catch (Exception e) {
            System.err.println("Erro ao processar a IA: " + e.getMessage());
            return new AvaliacaoDTO(
                    0,
                    "Erro de comunicação com a IA. Tente novamente.",
                    false,
                    false
            );
        }
    }

    public TipoCliente sortearTipoCliente() {
        TipoCliente[] valores = TipoCliente.values();
        Random random = new Random();
        int indice = random.nextInt(valores.length);
        return valores[indice];
    }
}
