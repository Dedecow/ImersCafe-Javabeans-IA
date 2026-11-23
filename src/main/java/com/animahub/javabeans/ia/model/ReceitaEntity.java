package com.animahub.javabeans.ia.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Receita persistida (tabela "receita"). O record {@link Receita} continua sendo
 * o formato enviado ao frontend, para não alterar o contrato da API.
 */
@Entity
@Table(name = "receita")
public class ReceitaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nomeBebida;

    @Column(nullable = false)
    private int pontosBase;

    // Posição do cliente na sequência do jogo (1, 2, 3...)
    @Column(nullable = false, unique = true)
    private int ordem;

    // Ingredientes esperados (tabela "receita_ingrediente")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "receita_ingrediente", joinColumns = @JoinColumn(name = "receita_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "ingrediente", nullable = false, length = 30)
    private Set<Ingrediente> ingredientes = new HashSet<>();

    protected ReceitaEntity() {}

    public ReceitaEntity(String nomeBebida, int pontosBase, int ordem, Set<Ingrediente> ingredientes) {
        this.nomeBebida = nomeBebida;
        this.pontosBase = pontosBase;
        this.ordem = ordem;
        this.ingredientes = new HashSet<>(ingredientes);
    }

    public Receita paraReceita() {
        List<Ingrediente> ordenados = ingredientes.stream()
                .sorted(Comparator.comparingInt(Enum::ordinal))
                .toList();
        return new Receita(nomeBebida, ordenados, pontosBase);
    }

    public Long getId() { return id; }
    public String getNomeBebida() { return nomeBebida; }
    public int getPontosBase() { return pontosBase; }
    public int getOrdem() { return ordem; }
    public Set<Ingrediente> getIngredientes() { return ingredientes; }
}
