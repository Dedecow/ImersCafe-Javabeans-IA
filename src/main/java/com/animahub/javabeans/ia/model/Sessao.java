package com.animahub.javabeans.ia.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;

/**
 * Entidade JPA que representa uma sessão de jogo do usuário.
 * Armazena estado de progressão, identificação e timestamps para auditoria.
 */
@Entity
public class Sessao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** UUID único gerado para rastreabilidade da sessão */
    private String sessionId;

    private String nomeUsuario;
    private String nomePersonagem;

    /** XP total acumulado na sessão */
    private int xpTotal;

    /** Nível atual do jogador calculado com base no XP */
    private int nivelAtual;

    /** Timestamp de criação da sessão para auditoria */
    private LocalDateTime dataInicio;

    public Sessao() {}

    public Sessao(String sessionId, int xpTotal) {
        this.sessionId = sessionId;
        this.xpTotal = xpTotal;
        this.nivelAtual = 1;
        this.dataInicio = LocalDateTime.now();
    }

    public Sessao(String sessionId, String nomeUsuario, String nomePersonagem) {
        this.sessionId = sessionId;
        this.nomeUsuario = nomeUsuario;
        this.nomePersonagem = nomePersonagem;
        this.xpTotal = 0;
        this.nivelAtual = 1;
        this.dataInicio = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getNomePersonagem() { return nomePersonagem; }
    public void setNomePersonagem(String nomePersonagem) { this.nomePersonagem = nomePersonagem; }

    public int getXpTotal() { return xpTotal; }
    public void setXpTotal(int xpTotal) { this.xpTotal = xpTotal; }

    public int getNivelAtual() { return nivelAtual; }
    public void setNivelAtual(int nivelAtual) { this.nivelAtual = nivelAtual; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
}
