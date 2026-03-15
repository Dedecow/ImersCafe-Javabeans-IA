package com.animahub.javabeans.ia.dto;

public record ResultadoTurnoDTO(
        Boolean sucesso,
        String feedbackBarista,
        Integer incrementoPontuacao,
        AnaliseIADTO analiseIA
) {}
