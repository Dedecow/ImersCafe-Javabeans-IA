package com.animahub.javabeans.ia.engine;

import com.animahub.javabeans.ia.dto.AvaliacaoDTO;
import com.animahub.javabeans.ia.dto.CenarioDTO;
import com.animahub.javabeans.ia.service.AIService;
import com.animahub.javabeans.ia.service.AvaliacaoIAParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class GameEngine {

    private static final Logger logger = LoggerFactory.getLogger(GameEngine.class);

    private final SessionEngine sessionEngine;
    private final AIService aiService;
    private final AvaliacaoIAParser avaliacaoIAParser;

    public GameEngine(
            SessionEngine sessionEngine,
            AIService aiService,
            AvaliacaoIAParser avaliacaoIAParser) {
        this.sessionEngine = sessionEngine;
        this.aiService = aiService;
        this.avaliacaoIAParser = avaliacaoIAParser;
    }

    public CenarioDTO carregarCenarioInicial() {
        return new CenarioDTO(
                "CEN-001",
                "O Cliente Apressado",
                "Um cliente entra na cafeteria pedindo um espresso para viagem, mas o sistema de pagamentos acabou de cair.",
                "Comunicação Clara e Resolução de Problemas sob Pressão");
    }

    public AvaliacaoDTO processarTurno(
            String sessionId,
            String respostaAluno,
            String nomeJogador) {
        if (!sessionEngine.isSessaoValida(sessionId)) {
            throw new IllegalArgumentException("Sessão inválida ou expirada.");
        }

        try {
            TipoCliente cliente = sortearTipoCliente();

            String respostaRawIA = aiService.avaliarResposta(
                    respostaAluno,
                    nomeJogador);

            AvaliacaoDTO avaliacao = avaliacaoIAParser.parse(respostaRawIA);

            aplicarBonusCliente(avaliacao, cliente);

            return avaliacao;

        } catch (Exception e) {
            logger.error("Erro ao processar turno do jogo: {}", e.getMessage(), e);

            return new AvaliacaoDTO(
                    0,
                    "Erro de comunicação com a IA. Tente novamente.",
                    false,
                    false);
        }
    }

    private void aplicarBonusCliente(
            AvaliacaoDTO avaliacao,
            TipoCliente cliente) {
        avaliacao.setTipoCliente(cliente);

        int bonus = avaliacao.isCenarioConcluido()
                ? cliente.getPontos()
                : cliente.getPontos() / 2;

        avaliacao.setXpGanho(avaliacao.getXpGanho() + bonus);
    }

    public TipoCliente sortearTipoCliente() {
        TipoCliente[] valores = TipoCliente.values();

        int indice = ThreadLocalRandom.current().nextInt(valores.length);

        return valores[indice];
    }
}