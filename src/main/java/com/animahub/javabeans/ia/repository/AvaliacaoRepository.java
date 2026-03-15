package com.animahub.javabeans.ia.repository;

import com.animahub.javabeans.ia.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositório Spring Data JPA para acesso ao histórico de avaliações.
 */
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    /** Retorna todas as avaliações realizadas em uma sessão específica */
    List<Avaliacao> findBySessionId(String sessionId);
}
