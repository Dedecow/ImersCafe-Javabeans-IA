package com.animahub.javabeans.ia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "sessoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jogador_id", nullable = false)
    private Jogador jogador;

    @Column(nullable = false)
    private String cenarioAtual;

    @Column(nullable = false)
    private Integer xpSessao;

    @Column(nullable = false)
    private Boolean ativa;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime dataEncerramento;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    /**
     * Encerra a sessão registrando o timestamp de encerramento.
     */
    public void encerrar() {
        this.ativa = false;
        this.dataEncerramento = LocalDateTime.now();
    }

    /**
     * Adiciona XP à sessão atual.
     */
    public void adicionarXPSessao(Integer pontos) {
        this.xpSessao += pontos;
    }

    /**
     * Calcula a duração da sessão em minutos.
     */
    public Long calcularDuracaoMinutos() {
        LocalDateTime fim = dataEncerramento != null ? dataEncerramento : LocalDateTime.now();
        return ChronoUnit.MINUTES.between(dataCriacao, fim);
    }

    /**
     * Avança o cenário atual para o próximo.
     */
    public void avancarCenario(String novoCenario) {
        this.cenarioAtual = novoCenario;
    }
}

