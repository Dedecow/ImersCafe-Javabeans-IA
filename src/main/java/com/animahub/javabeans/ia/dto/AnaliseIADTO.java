package com.animahub.javabeans.ia.dto;

import java.util.List;

/**
 * DTO que encapsula a análise semântica e comunicativa realizada pela IA.
 * Fornece métricas de competência e sugestões de melhoria para o discente.
 */
public class AnaliseIADTO {

    /** Pontuação de 0 a 100 avaliando a competência comunicativa da resposta */
    private int competenciaComunicativa;

    /** Análise textual sobre o conteúdo semântico da resposta do discente */
    private String analiseSemantica;

    /** Lista de sugestões pedagógicas para aprimorar a resposta */
    private List<String> sugestoes;

    public AnaliseIADTO() {}

    public AnaliseIADTO(int competenciaComunicativa, String analiseSemantica, List<String> sugestoes) {
        this.competenciaComunicativa = competenciaComunicativa;
        this.analiseSemantica = analiseSemantica;
        this.sugestoes = sugestoes;
    }

    public int getCompetenciaComunicativa() { return competenciaComunicativa; }
    public void setCompetenciaComunicativa(int competenciaComunicativa) { this.competenciaComunicativa = competenciaComunicativa; }

    public String getAnaliseSemantica() { return analiseSemantica; }
    public void setAnaliseSemantica(String analiseSemantica) { this.analiseSemantica = analiseSemantica; }

    public List<String> getSugestoes() { return sugestoes; }
    public void setSugestoes(List<String> sugestoes) { this.sugestoes = sugestoes; }
}
