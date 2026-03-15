package com.animahub.javabeans.ia.dto;

import com.animahub.javabeans.ia.model.enums.NivelDificuldade;
import java.util.List;

/**
 * DTO para representação de cenários pedagógicos.
 * Suporta tanto o contrato legado do /game quanto o novo contrato /api/v1/jogo/cenario.
 */
public class CenarioDTO {
    // Campos legados (endpoint /game)
    private String id;
    private String titulo;
    private String descricaoContexto;
    private String objetivoSoftSkill;

    // Campos do novo contrato de API /api/v1/jogo/cenario
    private String cenarioId;
    private String descricao;
    private String contextoNarrativo;
    private List<String> objetivosPedagogicos;
    private NivelDificuldade dificuldade;
    private int tempoEstimado;
    private int xpRecompensa;

    // Novos campos para o fluxo de preparo de bebida
    private String nomeBebida;
    private List<String> ingredientesEsperados;

    public CenarioDTO() {}

    public CenarioDTO(String id, String titulo, String descricaoContexto, String objetivoSoftSkill) {
        this.id = id;
        this.titulo = titulo;
        this.descricaoContexto = descricaoContexto;
        this.objetivoSoftSkill = objetivoSoftSkill;
    }

    public CenarioDTO(String id, String titulo, String descricaoContexto, String objetivoSoftSkill,
                      String nomeBebida, List<String> ingredientesEsperados) {
        this(id, titulo, descricaoContexto, objetivoSoftSkill);
        this.nomeBebida = nomeBebida;
        this.ingredientesEsperados = ingredientesEsperados;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricaoContexto() { return descricaoContexto; }
    public void setDescricaoContexto(String descricaoContexto) { this.descricaoContexto = descricaoContexto; }
    public String getObjetivoSoftSkill() { return objetivoSoftSkill; }
    public void setObjetivoSoftSkill(String objetivoSoftSkill) { this.objetivoSoftSkill = objetivoSoftSkill; }
    public String getNomeBebida() { return nomeBebida; }
    public void setNomeBebida(String nomeBebida) { this.nomeBebida = nomeBebida; }
    public List<String> getIngredientesEsperados() { return ingredientesEsperados; }
    public void setIngredientesEsperados(List<String> ingredientesEsperados) { this.ingredientesEsperados = ingredientesEsperados; }

    public String getCenarioId() { return cenarioId; }
    public void setCenarioId(String cenarioId) { this.cenarioId = cenarioId; }
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
