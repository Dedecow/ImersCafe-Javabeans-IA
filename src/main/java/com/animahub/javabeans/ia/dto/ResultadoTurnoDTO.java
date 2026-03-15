package com.animahub.javabeans.ia.dto;

import java.util.UUID;

/**
 * Resultado de um turno processado pelo GameEngineService.
 * Contém a decisão de progressão, feedback educativo e XP calculado.
 *
 * Conformidade: Chen et al. (2025) - Sistemas gamificados com progressão estruturada
 */
public record ResultadoTurnoDTO(
        UUID sessaoId,
        UUID jogadorId,
        boolean sucesso,
        String proximoCenario,
        String feedbackIA,
        Double competenciaComunicativa,
        Integer xpGanho,
        Integer xpTotalJogador,
        Integer nivelGlobal,
        Integer xpSessao
) {}
