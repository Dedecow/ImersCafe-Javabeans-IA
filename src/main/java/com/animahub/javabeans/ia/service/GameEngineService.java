package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.dto.AcaoJogadorDTO;
import com.animahub.javabeans.ia.dto.AnaliseIADTO;
import com.animahub.javabeans.ia.dto.ResultadoTurnoDTO;
import com.animahub.javabeans.ia.model.Jogador;
import com.animahub.javabeans.ia.model.Sessao;
import com.animahub.javabeans.ia.repository.JogadorRepository;
import com.animahub.javabeans.ia.repository.SessaoRepository;
import com.animahub.javabeans.ia.service.ai.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Maestro central do simulador JavaBeans-IA.
 *
 * O GameEngineService é a ÚNICA autoridade sobre decisões de progressão, cálculo de XP
 * e fluxo narrativo. O AIService é consumido de forma estritamente agnóstica:
 * recebe apenas os dados textuais necessários e retorna exclusivamente a análise
 * de competência comunicativa — sem influência sobre regras de negócio.
 *
 * Princípio de Responsabilidade Única: Orquestra os serviços de domínio e o AIService
 * para processar cada turno do jogador de forma atômica e auditável.
 *
 * Conformidade:
 * - Matthew et al. (2024): LLM como tutor personalizado agnóstico
 * - Chen et al. (2025): Sistemas gamificados com progressão estruturada
 * - Qin et al. (2025): Competência comunicativa avaliada pela IA
 * - Gupta et al. (2025): Resiliência em arquiteturas com IA
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GameEngineService {

    private final JogadorRepository jogadorRepository;
    private final SessaoRepository sessaoRepository;
    private final JogadorService jogadorService;
    private final SessaoService sessaoService;
    private final AIService aiService;

    // Threshold mínimo de competência comunicativa para considerar a ação bem-sucedida
    private static final double THRESHOLD_COMPETENCIA = 60.0;

    // Parâmetros de cálculo de XP (autoridade exclusiva do GameEngine)
    private static final int XP_BASE = 10;
    private static final double BONUS_COMPETENCIA_FATOR = 0.5;
    private static final int BONUS_NIVEL_POR_NIVEL = 2;
    private static final int BONUS_DIFICULDADE_BASE = 5;

    /**
     * Processa a ação de um jogador em seu turno atual de forma atômica.
     *
     * Isolamento SERIALIZABLE: Garante isolamento total durante a avaliação completa
     * do turno, incluindo leitura, cálculo de XP e persistência dos resultados.
     *
     * Etapas de processamento:
     * 1. Recuperar sessão ativa com eager loading (isolamento de contexto)
     * 2. Chamar AIService de forma agnóstica (apenas dados textuais)
     * 3. Validar resposta baseada em threshold de competência (>= 60%)
     * 4. Calcular XP via GameEngine (NÃO delegado à IA)
     * 5. Atualizar estado atomicamente (jogador + sessão)
     * 6. Determinar próximo cenário (decisão exclusiva do GameEngine)
     * 7. Retornar ResultadoTurnoDTO com decisão, feedback e XP
     *
     * @param jogadorId   UUID do jogador
     * @param acaoJogador DTO com a resposta textual do jogador
     * @return ResultadoTurnoDTO com decisão, feedback, XP e próximo cenário
     * @throws IllegalArgumentException se o jogador não possuir sessão ativa
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResultadoTurnoDTO processarAcaoJogador(UUID jogadorId, AcaoJogadorDTO acaoJogador) {
        log.info("[ETAPA-1] Recuperando sessão ativa com eager loading. jogadorId={}", jogadorId);

        // Etapa 1: Recuperar sessão ativa com eager loading (LEFT JOIN FETCH)
        Sessao sessao = sessaoRepository.findSessaoAtivaByJogadorIdComJogador(jogadorId)
                .orElseThrow(() -> {
                    log.warn("Nenhuma sessão ativa encontrada para jogadorId={}", jogadorId);
                    return new IllegalArgumentException(
                            "Nenhuma sessão ativa encontrada para o jogador: " + jogadorId);
                });

        Jogador jogador = sessao.getJogador();
        String cenarioAtual = sessao.getCenarioAtual();

        log.info("[ETAPA-1] Sessão ativa encontrada: sessaoId={}, cenario={}, xpSessao={}",
                sessao.getId(), cenarioAtual, sessao.getXpSessao());

        // Etapa 2: Chamar AIService de forma estritamente agnóstica às regras de negócio
        // Passamos apenas: resposta textual, cenário atual e contexto mínimo necessário
        log.info("[ETAPA-2] Chamando AIService para avaliação de competência comunicativa. sessaoId={}",
                sessao.getId());

        String contexto = String.format("Nível do jogador: %d, XP da sessão: %d",
                jogador.getNivelGlobal(), sessao.getXpSessao());

        AnaliseIADTO analise = aiService.avaliarCompetenciaComunicativa(
                acaoJogador.getResposta(),
                cenarioAtual,
                contexto
        );

        log.info("[ETAPA-2] Análise recebida: competencia={}%, feedback='{}'",
                analise.competenciaComunicativa(), analise.feedback());

        // Etapa 3: Validar resposta baseada em threshold de competência (>= 60%)
        boolean sucesso = analise.competenciaComunicativa() >= THRESHOLD_COMPETENCIA;
        log.info("[ETAPA-3] Validação de competência: competencia={}%, threshold={}%, sucesso={}",
                analise.competenciaComunicativa(), THRESHOLD_COMPETENCIA, sucesso);

        // Etapa 4: Calcular XP via GameEngine — NÃO delegado à IA
        // Fórmula: base + bônus(competência) + bônus(nível) + bônus(dificuldade cenário)
        int xpGanho = calcularXP(analise.competenciaComunicativa(), jogador.getNivelGlobal(), sucesso);
        log.info("[ETAPA-4] XP calculado pelo GameEngine: xpGanho={}, jogadorNivel={}, competencia={}%",
                xpGanho, jogador.getNivelGlobal(), analise.competenciaComunicativa());

        // Etapa 5: Atualizar estado atomicamente
        log.info("[ETAPA-5] Atualizando estado atomicamente. sessaoId={}, jogadorId={}, xpGanho={}",
                sessao.getId(), jogadorId, xpGanho);

        // 5a: Adicionar XP ao jogador (recalcula nível global automaticamente)
        jogadorService.adicionarXP(jogadorId, xpGanho);

        // 5b: Adicionar XP à sessão local
        sessao.adicionarXPSessao(xpGanho);

        // 5c: Persistir sessão atualizada
        sessaoRepository.save(sessao);

        // Recarregar jogador para obter valores atualizados após adicionarXP
        Jogador jogadorAtualizado = jogadorRepository.findById(jogadorId)
                .orElseThrow(() -> new IllegalArgumentException("Jogador não encontrado após atualização: " + jogadorId));

        log.info("[ETAPA-5] Estado atualizado: xpTotalJogador={}, nivelGlobal={}, xpSessao={}",
                jogadorAtualizado.getXpTotal(), jogadorAtualizado.getNivelGlobal(), sessao.getXpSessao());

        // Etapa 6: Determinar próximo cenário (decisão exclusiva do GameEngine)
        String proximoCenario = determinarProximoCenario(cenarioAtual, sucesso, jogadorAtualizado.getNivelGlobal());
        log.info("[ETAPA-6] Próximo cenário determinado pelo GameEngine: '{}' (sucesso={})",
                proximoCenario, sucesso);

        // Etapa 7: Retornar ResultadoTurnoDTO com decisão, feedback e XP
        ResultadoTurnoDTO resultado = new ResultadoTurnoDTO(
                sessao.getId(),
                jogadorId,
                sucesso,
                proximoCenario,
                analise.feedback(),
                analise.competenciaComunicativa(),
                xpGanho,
                jogadorAtualizado.getXpTotal(),
                jogadorAtualizado.getNivelGlobal(),
                sessao.getXpSessao()
        );

        log.info("[ETAPA-7] Resultado do turno: sessaoId={}, sucesso={}, proximoCenario={}, xpGanho={}, nivelGlobal={}",
                sessao.getId(), sucesso, proximoCenario, xpGanho, jogadorAtualizado.getNivelGlobal());

        return resultado;
    }

    /**
     * Calcula o XP ganho no turno com base em métricas gamificadas.
     *
     * Esta função é a ÚNICA autoridade sobre cálculo de XP — nunca delegada à IA.
     *
     * Fórmula: base + bônus(competência) + bônus(nível) + bônus(dificuldade cenário)
     *
     * @param competencia Percentual de competência comunicativa (0-100)
     * @param nivel       Nível atual do jogador
     * @param sucesso     Se a ação foi bem-sucedida (competência >= threshold)
     * @return XP calculado (sempre >= 0)
     */
    private int calcularXP(double competencia, int nivel, boolean sucesso) {
        if (!sucesso) {
            // XP mínimo por participação mesmo em caso de falha
            return Math.max(1, (int) (competencia * 0.1));
        }

        int bonusCompetencia = (int) (competencia * BONUS_COMPETENCIA_FATOR);
        int bonusNivel = nivel * BONUS_NIVEL_POR_NIVEL;
        int bonusDificuldade = BONUS_DIFICULDADE_BASE;

        return XP_BASE + bonusCompetencia + bonusNivel + bonusDificuldade;
    }

    /**
     * Determina o próximo cenário baseado no cenário atual, resultado e nível do jogador.
     *
     * Esta função é a ÚNICA autoridade sobre progressão narrativa — nunca delegada à IA.
     *
     * @param cenarioAtual  Cenário atual da sessão
     * @param sucesso       Se a ação foi bem-sucedida
     * @param nivelJogador  Nível atual do jogador
     * @return Identificador do próximo cenário
     */
    private String determinarProximoCenario(String cenarioAtual, boolean sucesso, int nivelJogador) {
        if (!sucesso) {
            // Repetir o cenário atual até o jogador ter sucesso
            return cenarioAtual;
        }

        // Progressão linear baseada no nível do jogador
        return switch (cenarioAtual) {
            case "CEN-001" -> nivelJogador >= 3 ? "CEN-003" : "CEN-002";
            case "CEN-002" -> nivelJogador >= 5 ? "CEN-004" : "CEN-003";
            case "CEN-003" -> "CEN-004";
            case "CEN-004" -> "CEN-005";
            default -> "CEN-001";
        };
    }
}
