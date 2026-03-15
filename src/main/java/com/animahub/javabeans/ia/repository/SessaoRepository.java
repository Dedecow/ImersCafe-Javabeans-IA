package com.animahub.javabeans.ia.repository;

import com.animahub.javabeans.ia.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório de acesso a dados para a entidade Sessao.
 *
 * Utiliza LEFT JOIN FETCH (eager loading) para evitar lazy loading issues
 * e garantir que o Jogador associado esteja instanciado atomicamente.
 *
 * Conformidade: Gupta et al. (2025) - Resiliência em arquiteturas com IA
 */
@Repository
public interface SessaoRepository extends JpaRepository<Sessao, UUID> {

    /**
     * Busca a sessão ativa de um jogador pelo seu ID.
     *
     * @param jogadorId UUID do jogador
     * @return Optional contendo a sessão ativa, ou vazio se não houver
     */
    Optional<Sessao> findByJogadorIdAndAtivaTrue(UUID jogadorId);

    /**
     * Recupera a sessão ativa com o Jogador instanciado via LEFT JOIN FETCH,
     * evitando lazy loading issues e garantindo eager loading atômico.
     *
     * @param sessaoId UUID da sessão
     * @return Optional contendo a sessão ativa com jogador carregado
     */
    @Query("SELECT s FROM Sessao s LEFT JOIN FETCH s.jogador WHERE s.id = :sessaoId AND s.ativa = true")
    Optional<Sessao> findSessaoAtivaComJogador(@Param("sessaoId") UUID sessaoId);

    /**
     * Recupera a sessão ativa de um jogador com o Jogador instanciado,
     * mitigando N+1 ao carregar contexto completo em uma única query.
     *
     * @param jogadorId UUID do jogador
     * @return Optional contendo a sessão ativa com jogador carregado
     */
    @Query("SELECT s FROM Sessao s LEFT JOIN FETCH s.jogador WHERE s.jogador.id = :jogadorId AND s.ativa = true")
    Optional<Sessao> findSessaoAtivaByJogadorIdComJogador(@Param("jogadorId") UUID jogadorId);
}

