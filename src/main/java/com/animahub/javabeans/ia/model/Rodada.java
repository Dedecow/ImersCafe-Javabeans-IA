package com.animahub.javabeans.ia.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Uma jogada de preparo de café (domínio determinístico / hard skill).
 * "acertou" e "pontos" são sempre calculados pelo backend, nunca pela IA.
 */
@Entity
@Table(name = "rodada", indexes = @Index(name = "idx_rodada_sessao", columnList = "sessao_id"))
public class Rodada {

    private static final int TAMANHO_TEXTO = 4000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sessao_id", nullable = false)
    private Sessao sessao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "receita_id", nullable = false)
    private ReceitaEntity receita;

    // Ingredientes escolhidos pelo aluno (tabela "rodada_ingrediente")
    @ElementCollection
    @CollectionTable(name = "rodada_ingrediente", joinColumns = @JoinColumn(name = "rodada_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "ingrediente", nullable = false, length = 30)
    private Set<Ingrediente> ingredientesEscolhidos = new HashSet<>();

    @Column(nullable = false)
    private boolean acertou;

    @Column(nullable = false)
    private int pontos;

    @Column(length = TAMANHO_TEXTO)
    private String falaBarista;

    // Comentário narrativo da IA; não influencia acerto nem pontuação
    @Column(length = TAMANHO_TEXTO)
    private String feedbackIa;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    protected Rodada() {}

    public Rodada(Sessao sessao, ReceitaEntity receita, Collection<Ingrediente> ingredientesEscolhidos,
                  boolean acertou, int pontos, String falaBarista, String feedbackIa) {
        this.sessao = sessao;
        this.receita = receita;
        this.ingredientesEscolhidos = new HashSet<>(ingredientesEscolhidos);
        this.acertou = acertou;
        this.pontos = pontos;
        this.falaBarista = limitar(falaBarista);
        this.feedbackIa = limitar(feedbackIa);
        this.criadoEm = LocalDateTime.now();
    }

    private static String limitar(String texto) {
        return texto != null && texto.length() > TAMANHO_TEXTO ? texto.substring(0, TAMANHO_TEXTO) : texto;
    }

    public Long getId() { return id; }
    public Sessao getSessao() { return sessao; }
    public ReceitaEntity getReceita() { return receita; }
    public Set<Ingrediente> getIngredientesEscolhidos() { return ingredientesEscolhidos; }
    public boolean isAcertou() { return acertou; }
    public int getPontos() { return pontos; }
    public String getFalaBarista() { return falaBarista; }
    public String getFeedbackIa() { return feedbackIa; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
