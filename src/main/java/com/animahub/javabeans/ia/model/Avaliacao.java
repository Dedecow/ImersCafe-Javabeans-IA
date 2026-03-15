package com.animahub.javabeans.ia.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade JPA que registra cada avaliação de resposta do discente.
 * Persiste o histórico de interações para fins de auditoria e progressão.
 */
@Entity
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificador da sessão do jogador que realizou a avaliação */
    private String sessionId;

    /** Identificador do cenário avaliado */
    private String cenarioId;

    @Column(columnDefinition = "TEXT")
    private String resposta;

    private boolean correto;

    /** XP concedido nesta avaliação */
    private int xpGanho;

    /** Timestamp de quando a avaliação foi realizada */
    private LocalDateTime dataAvaliacao;

    public Avaliacao() {}

    public Avaliacao(String sessionId, String cenarioId, String resposta, boolean correto, int xpGanho) {
        this.sessionId = sessionId;
        this.cenarioId = cenarioId;
        this.resposta = resposta;
        this.correto = correto;
        this.xpGanho = xpGanho;
        this.dataAvaliacao = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getCenarioId() { return cenarioId; }
    public void setCenarioId(String cenarioId) { this.cenarioId = cenarioId; }

    public String getResposta() { return resposta; }
    public void setResposta(String resposta) { this.resposta = resposta; }

    public boolean isCorreto() { return correto; }
    public void setCorreto(boolean correto) { this.correto = correto; }

    public int getXpGanho() { return xpGanho; }
    public void setXpGanho(int xpGanho) { this.xpGanho = xpGanho; }

    public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDateTime dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }
}
