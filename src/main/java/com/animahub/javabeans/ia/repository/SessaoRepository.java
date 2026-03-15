package com.animahub.javabeans.ia.repository;

import com.animahub.javabeans.ia.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, UUID> {

    Optional<Sessao> findByJogadorIdAndAtivaTrue(UUID jogadorId);

    @Query("SELECT s FROM Sessao s LEFT JOIN FETCH s.jogador j WHERE j.id = :jogadorId AND s.ativa = true")
    Optional<Sessao> findAtivaSessaoWithJogador(@Param("jogadorId") UUID jogadorId);

    List<Sessao> findByJogadorId(UUID jogadorId);
}
