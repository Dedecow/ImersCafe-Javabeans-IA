package com.animahub.javabeans.ia.dto;

import java.util.List;

public record AnaliseIADTO(
        Integer competenciaComunicativa,
        String analiseSemantica,
        List<String> sugestoes
) {}
