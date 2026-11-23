package com.animahub.javabeans.ia.repository;

import com.animahub.javabeans.ia.model.Sessao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface SessaoRepository extends JpaRepository<Sessao, Long> {
    // Busca automática de uma sessão baseada no UUID do jogador
    Optional<Sessao> findBySessionId(String sessionId);

    // SELECT ... FOR UPDATE: trava a linha da partida até o fim da transação,
    // para que gravações simultâneas na mesma partida não percam XP
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Sessao s where s.id = :id")
    Optional<Sessao> buscarParaAtualizar(@Param("id") Long id);

    // Soma do XP de todas as sessões (usado no /game/health)
    @Query("select coalesce(sum(s.xpTotal), 0) from Sessao s")
    long somarXpTotal();
}
