package com.animahub.javabeans.ia.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object para a entidade Jogador.
 * Expõe os dados do jogador sem expor a entidade JPA diretamente.
 */
public record JogadorDTO(
        UUID id,
        String email,
        String nome,
        String nomePersonagem,
        Integer xpTotal,
        Integer nivelGlobal,
        Boolean ativo,
        LocalDateTime dataCriacao
) {}
