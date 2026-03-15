package com.animahub.javabeans.ia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Entidade que representa uma Sessão de Jogo no simulador JavaBeans-IA.
 *
 * Responsabilidades:
 * - Rastreamento de progressão por sessão individual
 * - Vínculo entre Jogador e cenários completados
 * - Registro histórico de jogo (dataCriacao, dataEncerramento)
 * - Sincronização com GameEngineService (cenarioAtual, xpSessao)
 *
 * Conformidade: Matthew et al. (2024), Chen et al. (2025), Qin et al. (2025)
 */
@Entity
@Table(
        name = "sessoes",
        indexes = {
                @Index(name = "idx_sessao_jogador", columnList = "jogador_id"),
                @Index(name = "idx_sessao_ativa", columnList = "ativa"),
                @Index(name = "idx_sessao_criacao", columnList = "data_criacao")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "jogador")
public class Sessao {

    /**
     * Identificador único universal (UUID) da sessão.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID", nullable = false)
    private UUID id;

    /**
     * Referência ao Jogador que iniciou esta sessão.
     * Relacionamento N:1 obrigatório.
     */
    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(
            name = "jogador_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_sessao_jogador")
    )
    @NotNull(message = "Jogador não pode ser nulo")
    private Jogador jogador;

    /**
     * Identificador do cenário atual na sessão.
     */
    @NotBlank(message = "Cenário atual não pode estar vazio")
    @Size(min = 1, max = 255, message = "Cenário deve ter entre 1 e 255 caracteres")
    @Column(name = "cenario_atual", nullable = false, length = 255)
    private String cenarioAtual;

    /**
     * Experiência acumulada nesta sessão específica.
     * Conformidade: Chen et al. (2025) - Métrica de gamificação por sessão
     */
    @PositiveOrZero(message = "XP da sessão não pode ser negativo")
    @Column(name = "xp_sessao", nullable = false)
    @Builder.Default
    private Integer xpSessao = 0;

    /**
     * Flag indicando se a sessão está ativa ou concluída.
     */
    @NotNull(message = "Status de sessão não pode ser nulo")
    @Column(name = "ativa", nullable = false)
    @Builder.Default
    private Boolean ativa = true;

    /**
     * Timestamp de criação automático.
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    /**
     * Timestamp de encerramento da sessão.
     * Invariante: Se ativa == false, dataEncerramento != null
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_encerramento")
    private LocalDateTime dataEncerramento;

    /**
     * Calcula a duração da sessão em minutos.
     *
     * @return Duração em minutos, ou null se sessão ainda está ativa
     */
    public Long calcularDuracaoMinutos() {
        if (this.dataEncerramento == null) {
            return null;
        }
        return ChronoUnit.MINUTES.between(this.dataCriacao, this.dataEncerramento);
    }

    /**
     * Calcula a duração da sessão em segundos.
     *
     * @return Duração em segundos, ou null se sessão ainda está ativa
     */
    public Long calcularDuracaoSegundos() {
        if (this.dataEncerramento == null) {
            return null;
        }
        return ChronoUnit.SECONDS.between(this.dataCriacao, this.dataEncerramento);
    }

    /**
     * Encerra a sessão de jogo.
     * Define ativa = false e preenche dataEncerramento com o timestamp atual.
     */
    public void encerrar() {
        this.ativa = false;
        this.dataEncerramento = LocalDateTime.now();
    }

    /**
     * Encerra a sessão com um timestamp específico (útil para testes).
     *
     * @param dataEncerramento Timestamp de encerramento customizado
     */
    public void encerrarComTimestamp(LocalDateTime dataEncerramento) {
        if (dataEncerramento == null) {
            throw new IllegalArgumentException("Data de encerramento não pode ser nula");
        }
        this.ativa = false;
        this.dataEncerramento = dataEncerramento;
    }

    /**
     * Incrementa o XP da sessão.
     * Conformidade: Chen et al. (2025) - Acúmulo de experiência em contexto pedagógico
     *
     * @param pontos Quantidade de XP a adicionar
     * @throws IllegalArgumentException se pontos < 0
     * @throws IllegalStateException se sessão encerrada
     */
    public void adicionarXPSessao(Integer pontos) {
        if (pontos < 0) {
            throw new IllegalArgumentException("Pontos não podem ser negativos");
        }
        if (!this.ativa) {
            throw new IllegalStateException("Não é possível adicionar XP a uma sessão encerrada");
        }
        this.xpSessao += pontos;
    }

    /**
     * Avança para o próximo cenário.
     *
     * @param novoCenario ID/Nome do novo cenário
     * @throws IllegalArgumentException se cenário é inválido
     * @throws IllegalStateException se sessão não está ativa
     */
    public void avancarCenario(String novoCenario) {
        if (!this.ativa) {
            throw new IllegalStateException("Não é possível avançar em uma sessão encerrada");
        }
        if (novoCenario == null || novoCenario.isBlank()) {
            throw new IllegalArgumentException("Cenário não pode ser vazio");
        }
        this.cenarioAtual = novoCenario;
    }

    /**
     * Valida a integridade da sessão.
     *
     * @return true se sessão está em estado consistente
     */
    public boolean validarIntegridade() {
        if (!this.ativa && this.dataEncerramento == null) {
            return false;
        }
        if (this.ativa && this.dataEncerramento != null) {
            return false;
        }
        return this.dataCriacao != null;
    }
}

