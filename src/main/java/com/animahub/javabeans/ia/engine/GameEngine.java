package com.animahub.javabeans.ia.engine;

import com.animahub.javabeans.ia.dto.CenarioDTO;
import com.animahub.javabeans.ia.dto.AvaliacaoDTO;
import com.animahub.javabeans.ia.service.AIService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.stereotype.Service;
import java.util.Random;

import java.math.MathContext;
import java.util.Random;

@Service
public class GameEngine {

    private final SessionEngine sessionEngine;
    private final AIService aiService;
    private final ObjectMapper objectMapper;

    // Injetamos as dependências automaticamente
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

    public AvaliacaoDTO processarTurno(String sessionId, String respostaAluno) {
        if (!sessionEngine.isSessaoValida(sessionId)) {
            throw new IllegalArgumentException("Sessão inválida ou expirada.");
        }

        try {
            TipoCliente cliente = sortearTipoCliente();
            int pontos = cliente.getPontos();

            // 1. Chama a IA repassando a resposta do aluno
            String respostaRawIA = aiService.avaliarResposta(respostaAluno);

            // 2. Navega no JSON de retorno da API para pegar o conteúdo real gerado pela IA
            JsonNode rootNode = objectMapper.readTree(respostaRawIA);
            String conteudoIA = rootNode.path("choices").get(0).path("message").path("content").asText();

            // 3. Converte o texto JSON gerado pela IA para o nosso AvaliacaoDTO
            AvaliacaoDTO avaliacao = objectMapper.readValue(conteudoIA, AvaliacaoDTO.class);

            avaliacao.setTipoCliente(cliente)

            if (avaliacao.isCenarioConcluido()){
                avaliacao.setXpGanho(avaliacao.getXpGanho() + pontos);
            } else {
                avaliacao.setXpGanho(avaliacao.getXpGanho() + pontos / 2);
            }
            return avaliacao;

        } catch (Exception e) {
            System.err.println("Erro ao processar a IA: " + e.getMessage());
            // Fallback seguro para o jogo não quebrar caso a IA falhe ou a chave esteja errada
            return new AvaliacaoDTO(0, "Erro de comunicação com a IA. Tente novamente.", false, false);
        }

    }
         public TipoCliente sortearTipoCliente() {
             TipoCliente[] valores = TipoCliente.values();

             Random random = new Random();
             int indice = random.nextInt(valores.length);
             return valores[indice];

         }
}
