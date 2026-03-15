package com.animahub.javabeans.ia.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessaoDTO(
        UUID id,
        UUID jogadorId,
        String cenarioAtual,
        Integer xpSessao,
        Boolean ativa,
        LocalDateTime dataCriacao
) {}
