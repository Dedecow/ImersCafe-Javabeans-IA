package com.animahub.javabeans.ia.dto;

/**
 * DTO de request para o endpoint de avaliação de resposta do discente.
 */
public class AvaliacaoRequest {

    private String sessionId;
    private String cenarioId;
    private String resposta;

    public AvaliacaoRequest() {}

    public AvaliacaoRequest(String sessionId, String cenarioId, String resposta) {
        this.sessionId = sessionId;
        this.cenarioId = cenarioId;
        this.resposta = resposta;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getCenarioId() { return cenarioId; }
    public void setCenarioId(String cenarioId) { this.cenarioId = cenarioId; }

    public String getResposta() { return resposta; }
    public void setResposta(String resposta) { this.resposta = resposta; }
}
