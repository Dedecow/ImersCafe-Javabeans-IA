package com.animahub.javabeans.ia.service.engine;

import org.springframework.stereotype.Component;

/**
 * Motor de progressão de nível baseado em XP acumulado.
 * Implementa a fórmula de cálculo de nível com thresholds configuráveis.
 */
@Component
public class ProgressionEngine {

    private static final int XP_POR_NIVEL = 100;

    /**
     * Calcula o nível atual com base no XP total acumulado.
     * Fórmula: nível = (xpTotal / XP_POR_NIVEL) + 1
     *
     * @param xpTotal XP total acumulado pelo jogador (deve ser não-negativo)
     * @return nível calculado (mínimo 1)
     */
    public int calcularNivel(int xpTotal) {
        int xp = Math.max(0, xpTotal);
        return (xp / XP_POR_NIVEL) + 1;
    }

    /**
     * Calcula o XP necessário para atingir o próximo nível.
     *
     * @param xpAtual XP atual do jogador
     * @return XP restante para o próximo nível
     */
    public int xpParaProximoNivel(int xpAtual) {
        int xp = Math.max(0, xpAtual);
        int nivelAtual = calcularNivel(xp);
        int xpDoProximoNivel = nivelAtual * XP_POR_NIVEL;
        return xpDoProximoNivel - xp;
    }
}
