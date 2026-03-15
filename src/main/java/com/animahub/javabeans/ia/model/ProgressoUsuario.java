package com.animahub.javabeans.ia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade JPA que rastreia o progresso de gamificação do usuário.
 * Registra XP acumulado, nível atual e a última atualização para auditoria.
 */
@Entity
public class ProgressoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** UUID da sessão vinculada a este progresso */
    private String sessionId;

    private int xpAtual;
    private int nivelAtual;

    /** Timestamp da última atualização de progresso */
    private LocalDateTime ultimaAtualizacao;

    public ProgressoUsuario() {}

    public ProgressoUsuario(String sessionId, int xpAtual, int nivelAtual) {
        this.sessionId = sessionId;
        this.xpAtual = xpAtual;
        this.nivelAtual = nivelAtual;
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public int getXpAtual() { return xpAtual; }
    public void setXpAtual(int xpAtual) { this.xpAtual = xpAtual; }

    public int getNivelAtual() { return nivelAtual; }
    public void setNivelAtual(int nivelAtual) { this.nivelAtual = nivelAtual; }

    public LocalDateTime getUltimaAtualizacao() { return ultimaAtualizacao; }
    public void setUltimaAtualizacao(LocalDateTime ultimaAtualizacao) { this.ultimaAtualizacao = ultimaAtualizacao; }
}
