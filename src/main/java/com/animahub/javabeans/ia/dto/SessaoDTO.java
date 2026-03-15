package com.animahub.javabeans.ia.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object para a entidade Sessao.
 * Expõe os dados da sessão sem expor a entidade JPA diretamente.
 */
public record SessaoDTO(
        UUID id,
        UUID jogadorId,
        String cenarioAtual,
        Integer xpSessao,
        Boolean ativa,
        LocalDateTime dataCriacao,
        LocalDateTime dataEncerramento
) {}
