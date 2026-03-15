package com.animahub.javabeans.ia.repository;

import com.animahub.javabeans.ia.model.Cenario;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório Spring Data JPA para acesso à entidade Cenario.
 */
public interface CenarioRepository extends JpaRepository<Cenario, String> {
}
