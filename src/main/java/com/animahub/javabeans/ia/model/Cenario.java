package com.animahub.javabeans.ia.model;

import com.animahub.javabeans.ia.model.enums.NivelDificuldade;
import jakarta.persistence.*;
import java.util.List;

/**
 * Entidade JPA que representa um cenário pedagógico do simulador.
 * Contém contexto narrativo, objetivos de aprendizagem e metadados de gamificação.
 */
@Entity
public class Cenario {

    /** UUID único do cenário para rastreabilidade */
    @Id
    private String cenarioId;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(columnDefinition = "TEXT")
    private String contextoNarrativo;

    /** Lista de objetivos pedagógicos mapeados a competências do discente */
    @ElementCollection
    private List<String> objetivosPedagogicos;

    @Enumerated(EnumType.STRING)
    private NivelDificuldade dificuldade;

    /** Tempo estimado em segundos para concluir o cenário */
    private int tempoEstimado;

    /** Quantidade de XP concedida ao concluir o cenário com sucesso */
    private int xpRecompensa;

    public Cenario() {}

    public Cenario(String cenarioId, String titulo, String descricao, String contextoNarrativo,
                   List<String> objetivosPedagogicos, NivelDificuldade dificuldade,
                   int tempoEstimado, int xpRecompensa) {
        this.cenarioId = cenarioId;
        this.titulo = titulo;
        this.descricao = descricao;
        this.contextoNarrativo = contextoNarrativo;
        this.objetivosPedagogicos = objetivosPedagogicos;
        this.dificuldade = dificuldade;
        this.tempoEstimado = tempoEstimado;
        this.xpRecompensa = xpRecompensa;
    }

    public String getCenarioId() { return cenarioId; }
    public void setCenarioId(String cenarioId) { this.cenarioId = cenarioId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getContextoNarrativo() { return contextoNarrativo; }
    public void setContextoNarrativo(String contextoNarrativo) { this.contextoNarrativo = contextoNarrativo; }

    public List<String> getObjetivosPedagogicos() { return objetivosPedagogicos; }
    public void setObjetivosPedagogicos(List<String> objetivosPedagogicos) { this.objetivosPedagogicos = objetivosPedagogicos; }

    public NivelDificuldade getDificuldade() { return dificuldade; }
    public void setDificuldade(NivelDificuldade dificuldade) { this.dificuldade = dificuldade; }

    public int getTempoEstimado() { return tempoEstimado; }
    public void setTempoEstimado(int tempoEstimado) { this.tempoEstimado = tempoEstimado; }

    public int getXpRecompensa() { return xpRecompensa; }
    public void setXpRecompensa(int xpRecompensa) { this.xpRecompensa = xpRecompensa; }
}
