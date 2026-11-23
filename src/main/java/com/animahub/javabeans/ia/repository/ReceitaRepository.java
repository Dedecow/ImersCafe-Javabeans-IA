package com.animahub.javabeans.ia.repository;

import com.animahub.javabeans.ia.model.ReceitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReceitaRepository extends JpaRepository<ReceitaEntity, Long> {
    // Primeiro cliente da sequência
    Optional<ReceitaEntity> findFirstByOrderByOrdemAsc();

    // Próximo cliente depois da ordem informada
    Optional<ReceitaEntity> findFirstByOrdemGreaterThanOrderByOrdemAsc(int ordem);
}
