package com.animahub.javabeans.ia.model;

import com.animahub.javabeans.ia.engine.TipoCliente;
import jakarta.persistence.Column;
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

/**
 * Avaliação da fala do aluno feita pela IA (domínio interpretativo / soft skill).
 * Fica separada de {@link Rodada}, conforme as Diretrizes Arquiteturais.
 */
@Entity
@Table(name = "avaliacao_atendimento", indexes = @Index(name = "idx_avaliacao_sessao", columnList = "sessao_id"))
public class AvaliacaoAtendimento {

    private static final int TAMANHO_TEXTO = 4000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sessao_id", nullable = false)
    private Sessao sessao;

    @Column(nullable = false, length = TAMANHO_TEXTO)
    private String respostaAluno;

    // Pode ser nulo quando a chamada à IA falha
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoCliente tipoCliente;

    @Column(nullable = false)
    private int xpGanho;

    @Column(length = TAMANHO_TEXTO)
    private String feedbackEducativo;

    @Column(nullable = false)
    private boolean personaQuebrada;

    @Column(nullable = false)
    private boolean cenarioConcluido;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    protected AvaliacaoAtendimento() {}

    public AvaliacaoAtendimento(Sessao sessao, String respostaAluno, TipoCliente tipoCliente, int xpGanho,
                                String feedbackEducativo, boolean personaQuebrada, boolean cenarioConcluido) {
        this.sessao = sessao;
        this.respostaAluno = limitar(respostaAluno == null ? "" : respostaAluno);
        this.tipoCliente = tipoCliente;
        this.xpGanho = xpGanho;
        this.feedbackEducativo = limitar(feedbackEducativo);
        this.personaQuebrada = personaQuebrada;
        this.cenarioConcluido = cenarioConcluido;
        this.criadoEm = LocalDateTime.now();
    }

    private static String limitar(String texto) {
        return texto != null && texto.length() > TAMANHO_TEXTO ? texto.substring(0, TAMANHO_TEXTO) : texto;
    }

    public Long getId() { return id; }
    public Sessao getSessao() { return sessao; }
    public String getRespostaAluno() { return respostaAluno; }
    public TipoCliente getTipoCliente() { return tipoCliente; }
    public int getXpGanho() { return xpGanho; }
    public String getFeedbackEducativo() { return feedbackEducativo; }
    public boolean isPersonaQuebrada() { return personaQuebrada; }
    public boolean isCenarioConcluido() { return cenarioConcluido; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
