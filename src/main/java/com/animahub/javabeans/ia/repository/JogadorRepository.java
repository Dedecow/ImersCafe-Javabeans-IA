package com.animahub.javabeans.ia.repository;

import com.animahub.javabeans.ia.model.Jogador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JogadorRepository extends JpaRepository<Jogador, UUID> {

    Optional<Jogador> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT j FROM Jogador j LEFT JOIN FETCH j.sessoes WHERE j.id = :id")
    Optional<Jogador> findByIdWithSessoes(@Param("id") UUID id);
}
