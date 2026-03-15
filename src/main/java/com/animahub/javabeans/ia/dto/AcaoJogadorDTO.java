package com.animahub.javabeans.ia.dto;

import java.util.Map;

public record AcaoJogadorDTO(
        String acao,
        Map<String, Object> parametros,
        String resposta
) {}

