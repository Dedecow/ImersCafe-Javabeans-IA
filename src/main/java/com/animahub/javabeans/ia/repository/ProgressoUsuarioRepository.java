package com.animahub.javabeans.ia.repository;

import com.animahub.javabeans.ia.model.ProgressoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositório Spring Data JPA para acesso ao progresso de gamificação do usuário.
 */
public interface ProgressoUsuarioRepository extends JpaRepository<ProgressoUsuario, Long> {

    /** Busca o progresso associado a uma sessão pelo UUID */
    Optional<ProgressoUsuario> findBySessionId(String sessionId);
}
