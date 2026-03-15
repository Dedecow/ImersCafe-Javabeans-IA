package com.animahub.javabeans.ia.service.ai;

import com.animahub.javabeans.ia.dto.AnaliseIADTO;

/**
 * Contrato de integração com o serviço de Inteligência Artificial.
 * Esta interface é consumida de forma agnóstica pelo GameEngineService,
 * que não conhece detalhes de implementação do modelo de linguagem natural.
 * A implementação concreta será provida na Fase 3.
 */
public interface AIService {

    /**
     * Avalia a competência comunicativa do jogador com base na resposta textual fornecida.
     *
     * @param respostaJogador  Texto bruto da resposta do jogador
     * @param cenarioAtual     Contexto narrativo atual para calibrar a análise
     * @param nomePersonagem   Nome do personagem do jogador para personalização
     * @return AnaliseIADTO contendo a pontuação de competência comunicativa (0-100),
     *         análise semântica e sugestões de melhoria
     */
    AnaliseIADTO avaliarCompetenciaComunicativa(
            String respostaJogador,
            String cenarioAtual,
            String nomePersonagem
    );
}
