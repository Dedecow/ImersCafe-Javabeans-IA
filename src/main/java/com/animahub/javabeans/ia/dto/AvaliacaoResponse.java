package com.animahub.javabeans.ia.dto;

/**
 * DTO de resposta para o endpoint de avaliação.
 * Contém resultado da avaliação, feedback educativo, XP ganho,
 * análise de IA e estado atualizado de progressão do jogador.
 */
public class AvaliacaoResponse {

    private boolean correto;
    private String feedback;
    private int xpGanho;
    private AnaliseIADTO analiseIA;
    private ProgressaoJogoDTO progressaoJogo;

    public AvaliacaoResponse() {}

    public AvaliacaoResponse(boolean correto, String feedback, int xpGanho,
                             AnaliseIADTO analiseIA, ProgressaoJogoDTO progressaoJogo) {
        this.correto = correto;
        this.feedback = feedback;
        this.xpGanho = xpGanho;
        this.analiseIA = analiseIA;
        this.progressaoJogo = progressaoJogo;
    }

    public boolean isCorreto() { return correto; }
    public void setCorreto(boolean correto) { this.correto = correto; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public int getXpGanho() { return xpGanho; }
    public void setXpGanho(int xpGanho) { this.xpGanho = xpGanho; }

    public AnaliseIADTO getAnaliseIA() { return analiseIA; }
    public void setAnaliseIA(AnaliseIADTO analiseIA) { this.analiseIA = analiseIA; }

    public ProgressaoJogoDTO getProgressaoJogo() { return progressaoJogo; }
    public void setProgressaoJogo(ProgressaoJogoDTO progressaoJogo) { this.progressaoJogo = progressaoJogo; }
}
