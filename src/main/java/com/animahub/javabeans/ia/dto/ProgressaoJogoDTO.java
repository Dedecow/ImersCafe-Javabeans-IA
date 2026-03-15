package com.animahub.javabeans.ia.dto;

/**
 * DTO que representa o estado atual de progressão de gamificação do jogador.
 */
public class ProgressaoJogoDTO {

    private int xpAtual;
    private int nivelAtual;

    public ProgressaoJogoDTO() {}

    public ProgressaoJogoDTO(int xpAtual, int nivelAtual) {
        this.xpAtual = xpAtual;
        this.nivelAtual = nivelAtual;
    }

    public int getXpAtual() { return xpAtual; }
    public void setXpAtual(int xpAtual) { this.xpAtual = xpAtual; }

    public int getNivelAtual() { return nivelAtual; }
    public void setNivelAtual(int nivelAtual) { this.nivelAtual = nivelAtual; }
}
