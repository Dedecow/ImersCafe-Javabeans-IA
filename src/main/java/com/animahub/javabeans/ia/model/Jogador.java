package com.animahub.javabeans.ia.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "jogadores")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Jogador {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String nomePersonagem;

    @Column(nullable = false)
    private Integer xpTotal;

    @Column(nullable = false)
    private Integer nivelGlobal;

    @Column(nullable = false)
    private Boolean ativo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    @OneToMany(mappedBy = "jogador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Sessao> sessoes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    @PostLoad
    protected void onLoad() {
        if (sessoes == null) {
            sessoes = new ArrayList<>();
        }
    }

    /**
     * Adiciona XP ao jogador e recalcula o nível global automaticamente.
     * Fórmula de nível: nível = (xpTotal / 100) + 1
     */
    public void adicionarXP(Integer pontos) {
        this.xpTotal += pontos;
        this.nivelGlobal = (this.xpTotal / 100) + 1;
    }
}
