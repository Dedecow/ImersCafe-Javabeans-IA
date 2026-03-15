package com.animahub.javabeans.ia.dto;

import java.time.LocalDateTime;

/**
 * DTO de resposta para criação e consulta de sessão.
 * Contém o identificador único (UUID), dados do jogador e estado de progressão inicial.
 */
public class SessaoDTO {

    private String sessionId;
    private String nomeUsuario;
    private String nomePersonagem;
    private int xpAtual;
    private int nivelAtual;
    private LocalDateTime dataInicio;

    public SessaoDTO() {}

    public SessaoDTO(String sessionId, String nomeUsuario, String nomePersonagem,
                     int xpAtual, int nivelAtual, LocalDateTime dataInicio) {
        this.sessionId = sessionId;
        this.nomeUsuario = nomeUsuario;
        this.nomePersonagem = nomePersonagem;
        this.xpAtual = xpAtual;
        this.nivelAtual = nivelAtual;
        this.dataInicio = dataInicio;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getNomePersonagem() { return nomePersonagem; }
    public void setNomePersonagem(String nomePersonagem) { this.nomePersonagem = nomePersonagem; }

    public int getXpAtual() { return xpAtual; }
    public void setXpAtual(int xpAtual) { this.xpAtual = xpAtual; }

    public int getNivelAtual() { return nivelAtual; }
    public void setNivelAtual(int nivelAtual) { this.nivelAtual = nivelAtual; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
}
