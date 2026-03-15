package com.animahub.javabeans.ia.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record JogadorDTO(
        UUID id,
        String nome,
        String nomePersonagem,
        Integer xpTotal,
        Integer nivelGlobal,
        LocalDateTime dataCriacao
) {}
