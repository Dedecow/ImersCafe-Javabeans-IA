package com.animahub.javabeans.ia.repository;

import com.animahub.javabeans.ia.model.Jogador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório de acesso a dados para a entidade Jogador.
 *
 * Mitiga o problema N+1 via LEFT JOIN FETCH para recuperar o jogador
 * e suas sessões associadas em uma única consulta ao banco de dados.
 *
 * Conformidade: Gupta et al. (2025) - Resiliência em arquiteturas com IA
 */
@Repository
public interface JogadorRepository extends JpaRepository<Jogador, UUID> {

    /**
     * Busca um jogador pelo email.
     *
     * @param email Email do jogador
     * @return Optional contendo o jogador, ou vazio se não encontrado
     */
    Optional<Jogador> findByEmail(String email);

    /**
     * Verifica se um jogador com o email informado já existe.
     *
     * @param email Email a verificar
     * @return true se o email já está cadastrado, false caso contrário
     */
    boolean existsByEmail(String email);

    /**
     * Recupera o jogador e suas sessões associadas via LEFT JOIN FETCH,
     * mitigando o problema de consultas N+1 no histórico de sessões.
     *
     * @param id UUID do jogador
     * @return Optional contendo o jogador com sessões carregadas
     */
    @Query("SELECT j FROM Jogador j LEFT JOIN FETCH j.sessoes WHERE j.id = :id")
    Optional<Jogador> findByIdWithSessoes(@Param("id") UUID id);
}
