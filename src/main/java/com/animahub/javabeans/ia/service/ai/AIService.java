package com.animahub.javabeans.ia.service.ai;

import com.animahub.javabeans.ia.dto.AnaliseIADTO;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Serviço de análise de inteligência artificial para avaliação pedagógica.
 * Na Fase 1, retorna análises placeholder. A integração DeepSeek será implementada na Fase 2.
 *
 * @see DeepSeekIntegration para a integração futura com o modelo de linguagem
 */
@Service
public class AIService {

    private static final int FALLBACK_WORD_MULTIPLIER = 5;
    private static final int MAX_SCORE = 100;

    private final DeepSeekIntegration deepSeekIntegration;

    public AIService(DeepSeekIntegration deepSeekIntegration) {
        this.deepSeekIntegration = deepSeekIntegration;
    }

    /**
     * Analisa semanticamente a resposta do discente e retorna métricas pedagógicas.
     *
     * @param resposta    texto da resposta fornecida pelo discente
     * @param contexto    contexto narrativo do cenário atual
     * @return AnaliseIADTO com pontuação de competência comunicativa, análise semântica e sugestões
     */
    public AnaliseIADTO analisarResposta(String resposta, String contexto) {
        try {
            return deepSeekIntegration.analisar(resposta, contexto);
        } catch (Exception e) {
            // Fallback com análise básica em caso de falha na integração
            return gerarAnaliseFallback(resposta);
        }
    }

    /**
     * Gera uma análise de fallback quando a integração com IA não está disponível.
     * Utiliza heurísticas simples de comprimento e palavras-chave.
     *
     * @param resposta texto da resposta do discente
     * @return AnaliseIADTO com pontuação básica baseada em heurísticas
     */
    private AnaliseIADTO gerarAnaliseFallback(String resposta) {
        int tamanho = resposta != null ? resposta.trim().split("\\s+").length : 0;
        int pontuacao = Math.min(MAX_SCORE, tamanho * FALLBACK_WORD_MULTIPLIER);

        return new AnaliseIADTO(
                pontuacao,
                "Análise indisponível no momento. Serviço de IA em manutenção.",
                List.of(
                        "Procure ser mais específico na sua resposta",
                        "Demonstre empatia com o cliente",
                        "Proponha soluções concretas para o problema"
                )
        );
    }
}
