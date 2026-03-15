package com.animahub.javabeans.ia.service.ai;

import com.animahub.javabeans.ia.dto.AnaliseIADTO;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Integração com o modelo de linguagem DeepSeek para análise pedagógica.
 * Fase 1: implementação placeholder com análise heurística básica.
 * Fase 2: integração completa com a API DeepSeek via WebClient reativo.
 */
@Component
public class DeepSeekIntegration {

    private static final int WORD_SCORE_MULTIPLIER = 3;
    private static final int MAX_BASE_SCORE = 60;
    private static final int EMPATHY_BONUS = 10;
    private static final int APOLOGY_BONUS = 10;
    private static final int SOLUTION_BONUS = 20;
    private static final int MAX_SCORE = 100;
    private static final int EXCELLENT_THRESHOLD = 80;
    private static final int ACCEPTABLE_THRESHOLD = 50;

    /**
     * Envia a resposta do discente e o contexto do cenário para análise pelo DeepSeek.
     * Na Fase 1, retorna análise baseada em heurísticas simples de comprimento e clareza.
     *
     * @param resposta texto da resposta fornecida pelo discente
     * @param contexto contexto narrativo do cenário pedagógico atual
     * @return AnaliseIADTO com métricas de competência comunicativa e sugestões
     */
    public AnaliseIADTO analisar(String resposta, String contexto) {
        // Fase 1: análise heurística placeholder
        // Fase 2: chamada à API DeepSeek via WebClient reativo
        return analisarHeuristicamente(resposta);
    }

    /**
     * Análise heurística baseada em indicadores linguísticos da resposta.
     * Avalia comprimento, clareza e uso de palavras de polidez.
     *
     * @param resposta texto da resposta do discente
     * @return AnaliseIADTO com pontuação calculada heuristicamente
     */
    private AnaliseIADTO analisarHeuristicamente(String resposta) {
        if (resposta == null || resposta.isBlank()) {
            return new AnaliseIADTO(
                    0,
                    "Resposta vazia. Nenhum conteúdo para analisar.",
                    List.of("Forneça uma resposta descritiva ao cliente", "Não deixe o cliente sem retorno")
            );
        }

        String[] palavras = resposta.trim().split("\\s+");
        int numeroPalavras = palavras.length;

        // Heurística de comprimento (máximo MAX_BASE_SCORE pontos)
        int pontuacaoBase = Math.min(MAX_BASE_SCORE, numeroPalavras * WORD_SCORE_MULTIPLIER);

        // Bônus por uso de palavras de polidez e empatia
        String respostaLower = resposta.toLowerCase();
        if (respostaLower.contains("entendo") || respostaLower.contains("compreendo")) pontuacaoBase += EMPATHY_BONUS;
        if (respostaLower.contains("desculpe") || respostaLower.contains("lamento")) pontuacaoBase += APOLOGY_BONUS;
        if (respostaLower.contains("solução") || respostaLower.contains("posso ajudar")) pontuacaoBase += SOLUTION_BONUS;

        int pontuacaoFinal = Math.min(MAX_SCORE, pontuacaoBase);

        String analise = String.format(
                "Resposta com %d palavras. %s",
                numeroPalavras,
                pontuacaoFinal >= EXCELLENT_THRESHOLD
                        ? "Boa competência comunicativa demonstrada."
                        : "Resposta pode ser aprimorada com mais detalhes e empatia."
        );

        List<String> sugestoes = gerarSugestoes(pontuacaoFinal, respostaLower);

        return new AnaliseIADTO(pontuacaoFinal, analise, sugestoes);
    }

    private List<String> gerarSugestoes(int pontuacao, String respostaLower) {
        if (pontuacao >= EXCELLENT_THRESHOLD) {
            return List.of(
                    "Excelente abordagem! Continue demonstrando empatia",
                    "Considere oferecer alternativas adicionais ao cliente"
            );
        } else if (pontuacao >= ACCEPTABLE_THRESHOLD) {
            return List.of(
                    "Adicione mais detalhes sobre como resolverá o problema",
                    "Demonstre mais empatia reconhecendo o inconveniente do cliente",
                    "Proponha um prazo ou alternativa concreta"
            );
        } else {
            return List.of(
                    "Elabore mais sua resposta com pelo menos 3 frases completas",
                    "Inicie reconhecendo a situação do cliente",
                    "Apresente uma solução clara e objetiva",
                    "Finalize com uma pergunta de confirmação ao cliente"
            );
        }
    }
}
