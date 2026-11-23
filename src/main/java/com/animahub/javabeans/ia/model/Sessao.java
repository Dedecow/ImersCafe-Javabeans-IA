package com.animahub.javabeans.ia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class Sessao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String sessionId;

    @Column(length = 100)
    private String nomeBarista;

    @Column(nullable = false)
    private int xpTotal;

    // Cliente (receita) que o jogador está atendendo agora
    @ManyToOne
    @JoinColumn(name = "receita_atual_id")
    private ReceitaEntity receitaAtual;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    public Sessao() {}

    public Sessao(String sessionId, String nomeBarista, ReceitaEntity receitaAtual) {
        this.sessionId = sessionId;
        this.nomeBarista = nomeBarista;
        this.receitaAtual = receitaAtual;
        this.xpTotal = 0;
        this.criadoEm = LocalDateTime.now();
    }

    public void adicionarXp(int xp) {
        this.xpTotal += xp;
    }

    public Long getId() { return id; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getNomeBarista() { return nomeBarista; }
    public void setNomeBarista(String nomeBarista) { this.nomeBarista = nomeBarista; }
    public int getXpTotal() { return xpTotal; }
    public void setXpTotal(int xpTotal) { this.xpTotal = xpTotal; }
    public ReceitaEntity getReceitaAtual() { return receitaAtual; }
    public void setReceitaAtual(ReceitaEntity receitaAtual) { this.receitaAtual = receitaAtual; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
