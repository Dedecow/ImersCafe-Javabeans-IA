package com.animahub.javabeans.ia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidade que representa um Jogador no ecossistema JavaBeans-IA.
 *
 * Responsabilidades:
 * - Persistência de identidade única (UUID)
 * - Rastreabilidade de progressão global (xpTotal, nivelGlobal)
 * - Auditoria automática (dataCriacao, dataAtualizacao)
 *
 * Conformidade: Matthew et al. (2024), Chen et al. (2025), Qin et al. (2025)
 */
@Entity
@Table(
        name = "jogadores",
        indexes = {
                @Index(name = "idx_jogador_email", columnList = "email", unique = true),
                @Index(name = "idx_jogador_criacao", columnList = "data_criacao")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "sessoes")
public class Jogador {

    /**
     * Identificador único universal (UUID).
     * Gerado automaticamente via UUID strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID", nullable = false)
    private UUID id;

    /**
     * Email do jogador - Campo único e obrigatório.
     * Utilizado para autenticação e recuperação de conta.
     */
    @NotBlank(message = "Email do jogador não pode estar vazio")
    @Email(message = "Email deve ser válido")
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    /**
     * Nome completo do jogador.
     */
    @NotBlank(message = "Nome do jogador não pode estar vazio")
    @Size(min = 2, max = 255, message = "Nome deve ter entre 2 e 255 caracteres")
    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    /**
     * Nome do personagem escolhido pelo jogador.
     */
    @Size(max = 255, message = "Nome do personagem não pode exceder 255 caracteres")
    @Column(name = "nome_personagem", length = 255)
    private String nomePersonagem;

    /**
     * Experiência total acumulada globalmente.
     * Inicializado com 0 e incrementado conforme conclusão de cenários.
     */
    @PositiveOrZero(message = "XP total não pode ser negativo")
    @Column(name = "xp_total", nullable = false)
    @Builder.Default
    private Integer xpTotal = 0;

    /**
     * Nível global do jogador.
     * Calculado dinamicamente baseado em xpTotal (1 nível a cada 1000 XP).
     */
    @Positive(message = "Nível global deve ser positivo")
    @Column(name = "nivel_global", nullable = false)
    @Builder.Default
    private Integer nivelGlobal = 1;

    /**
     * Flag de ativação do jogador (soft delete).
     */
    @NotNull(message = "Status de ativação não pode ser nulo")
    @Column(name = "ativo", nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    /**
     * Timestamp de criação automático.
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    /**
     * Timestamp de atualização automático.
     */
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    /**
     * Relacionamento 1:N com Sessao.
     * Lazy loading para otimização de queries.
     */
    @OneToMany(
            mappedBy = "jogador",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY,
            orphanRemoval = false
    )
    @Builder.Default
    private List<Sessao> sessoes = new ArrayList<>();

    /**
     * Calcula o nível baseado no XP total.
     * Fórmula: nivel = (xpTotal / 1000) + 1
     *
     * @return Nível calculado (sempre >= 1)
     */
    public Integer calcularNivel() {
        return (this.xpTotal / 1000) + 1;
    }

    /**
     * Incrementa o XP total do jogador e recalcula o nível automaticamente.
     *
     * Conformidade: Chen et al. (2025) - Acúmulo de experiência em sistemas gamificados
     *
     * @param pontos Quantidade de XP a adicionar
     * @throws IllegalArgumentException se pontos < 0
     */
    public void adicionarXP(Integer pontos) {
        if (pontos < 0) {
            throw new IllegalArgumentException("Pontos não podem ser negativos");
        }
        this.xpTotal += pontos;
        this.nivelGlobal = calcularNivel();
    }

    /**
     * Obtém a sessão ativa atual (se existir).
     *
     * @return Primeira sessão ativa encontrada, ou null se nenhuma está ativa
     */
    public Sessao obterSessaoAtiva() {
        return this.sessoes.stream()
                .filter(Sessao::getAtiva)
                .findFirst()
                .orElse(null);
    }

    /**
     * Verifica se o jogador tem uma sessão ativa.
     *
     * @return true se houver sessão ativa, false caso contrário
     */
    public boolean temSessaoAtiva() {
        return this.sessoes.stream().anyMatch(Sessao::getAtiva);
    }
}
