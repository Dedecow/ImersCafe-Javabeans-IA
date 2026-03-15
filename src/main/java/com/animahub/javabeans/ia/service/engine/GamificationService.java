package com.animahub.javabeans.ia.service.engine;

import org.springframework.stereotype.Service;

/**
 * Serviço de gamificação responsável pelo cálculo de recompensas de XP.
 * Aplica multiplicadores baseados na qualidade da resposta e no nível de dificuldade do cenário.
 */
@Service
public class GamificationService {

    private static final int XP_RESPOSTA_PARCIAL_BASE = 20;
    private static final int COMPETENCIA_MINIMA_CORRETA = 70;
    private static final int COMPETENCIA_MINIMA_PARCIAL = 50;
    private static final int COMPETENCIA_MAX = 100;

    /**
     * Calcula o XP ganho com base na pontuação de competência comunicativa e acerto da resposta.
     *
     * @param correto                  indica se a resposta foi considerada correta
     * @param competenciaComunicativa  pontuação de 0 a 100 da análise de IA
     * @param xpRecompensaCenario      XP base do cenário atual
     * @return quantidade de XP a ser concedida
     */
    public int calcularXpGanho(boolean correto, int competenciaComunicativa, int xpRecompensaCenario) {
        int competencia = Math.max(0, Math.min(COMPETENCIA_MAX, competenciaComunicativa));
        if (correto) {
            // XP cheio proporcional à competência comunicativa
            double multiplicador = competencia / (double) COMPETENCIA_MAX;
            return (int) (xpRecompensaCenario * multiplicador);
        } else if (competencia >= COMPETENCIA_MINIMA_PARCIAL) {
            // XP parcial para respostas com boa comunicação mas incorretas
            return XP_RESPOSTA_PARCIAL_BASE;
        }
        return 0;
    }

    /**
     * Determina se a resposta do discente é considerada correta com base na análise de IA.
     * Uma resposta é correta quando a competência comunicativa supera o threshold mínimo.
     *
     * @param competenciaComunicativa pontuação de 0 a 100 da análise de IA
     * @return true se a resposta for considerada correta
     */
    public boolean avaliarCorreto(int competenciaComunicativa) {
        return competenciaComunicativa >= COMPETENCIA_MINIMA_CORRETA;
    }
}
