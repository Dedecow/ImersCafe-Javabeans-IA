package com.animahub.javabeans.ia.dto;

/**
 * Resultado da análise de competência comunicativa realizada pelo AIService.
 * Retornado pelo método avaliarCompetenciaComunicativa().
 *
 * Conformidade: Qin et al. (2025) - Competência comunicativa avaliada pela IA
 */
public record AnaliseIADTO(
        Double competenciaComunicativa,
        String feedback
) {}
