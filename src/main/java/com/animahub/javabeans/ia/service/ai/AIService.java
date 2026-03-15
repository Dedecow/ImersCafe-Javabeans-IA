package com.animahub.javabeans.ia.service.ai;

import com.animahub.javabeans.ia.dto.AnaliseIADTO;

/**
 * Interface do serviço de Inteligência Artificial - contrato agnóstico ao provedor.
 *
 * O AIService atua como um avaliador de competência comunicativa, recebendo a
 * resposta textual do jogador e retornando um diagnóstico estruturado.
 *
 * Princípio de responsabilidade única: Este serviço NÃO toma decisões de fluxo
 * narrativo nem calcula progressão de XP. Essas responsabilidades são exclusivas
 * do GameEngineService.
 *
 * Conformidade:
 * - Matthew et al. (2024): LLM como tutor personalizado (agnóstico ao provedor)
 * - Qin et al. (2025): Competência comunicativa avaliada pela IA
 */
public interface AIService {

    /**
     * Avalia a competência comunicativa do jogador de forma agnóstica às regras de negócio.
     *
     * O resultado retornado contém apenas:
     * - competenciaComunicativa: métrica percentual (0-100) da qualidade comunicativa
     * - feedback: texto educativo para o jogador
     *
     * Nenhuma decisão de progressão, XP ou fluxo narrativo deve ser delegada a este método.
     *
     * @param respostaJogador Texto da resposta fornecida pelo jogador
     * @param cenarioAtual    Identificador ou descrição do cenário atual
     * @param contexto        Contexto adicional necessário para avaliação
     * @return AnaliseIADTO com a competência comunicativa e feedback educativo
     */
    AnaliseIADTO avaliarCompetenciaComunicativa(String respostaJogador, String cenarioAtual, String contexto);
}
