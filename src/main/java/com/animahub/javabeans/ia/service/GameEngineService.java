package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.dto.AcaoJogadorDTO;
import com.animahub.javabeans.ia.dto.AnaliseIADTO;
import com.animahub.javabeans.ia.dto.ResultadoTurnoDTO;
import com.animahub.javabeans.ia.model.Jogador;
import com.animahub.javabeans.ia.model.Sessao;
import com.animahub.javabeans.ia.repository.JogadorRepository;
import com.animahub.javabeans.ia.repository.SessaoRepository;
import com.animahub.javabeans.ia.service.ai.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Slf4j
public class GameEngineService {

    private final JogadorRepository jogadorRepository;
    private final SessaoRepository sessaoRepository;
    private final JogadorService jogadorService;
    private final SessaoService sessaoService;
    private final AIService aiService;

    public GameEngineService(
            JogadorRepository jogadorRepository,
            SessaoRepository sessaoRepository,
            JogadorService jogadorService,
            SessaoService sessaoService,
            AIService aiService
    ) {
        this.jogadorRepository = jogadorRepository;
        this.sessaoRepository = sessaoRepository;
        this.jogadorService = jogadorService;
        this.sessaoService = sessaoService;
        this.aiService = aiService;
    }

    /**
     * Método mestre de processamento de ação do jogador.
     * Orquestra o fluxo completo de gamificação: recuperação de contexto,
     * delegação agnóstica à IA, cálculo de XP e persistência atômica.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResultadoTurnoDTO processarAcaoJogador(UUID jogadorId, AcaoJogadorDTO acao) {
        log.info("Processando ação do jogador: jogadorId={}, ação={}", jogadorId, acao.acao());

        // ETAPA 1: RECUPERAÇÃO DE CONTEXTO
        Sessao sessao = sessaoRepository.findAtivaSessaoWithJogador(jogadorId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Nenhuma sessão ativa para o jogador: " + jogadorId));

        Jogador jogador = sessao.getJogador();
        String cenarioAtual = sessao.getCenarioAtual();

        log.debug("Contexto recuperado: jogador={}, cenário={}, xpGlobal={}, xpSessão={}",
                jogador.getNome(), cenarioAtual, jogador.getXpTotal(), sessao.getXpSessao());

        // ETAPA 2: DELEGAÇÃO À IA (AGNÓSTICA)
        // AIService recebe apenas o texto do usuário e o contexto narrativo.
        // Nenhuma regra de gamificação é delegada ao modelo de linguagem natural.
        AnaliseIADTO analiseIA = aiService.avaliarCompetenciaComunicativa(
                acao.resposta(),
                cenarioAtual,
                jogador.getNomePersonagem()
        );

        log.debug("IA retornou análise: competência={}%, feedback={}",
                analiseIA.competenciaComunicativa(),
                analiseIA.analiseSemantica() != null
                        ? analiseIA.analiseSemantica().substring(
                                0, Math.min(50, analiseIA.analiseSemantica().length()))
                        : "");

        // ETAPA 3: VALIDAÇÃO (DECISÃO GAMEENGINE)
        // Threshold de competência comunicativa: 60%
        boolean respostaCorreta = analiseIA.competenciaComunicativa() >= 60;

        log.info("Resposta validada: {}", respostaCorreta ? "CORRETA" : "INCORRETA");

        // ETAPA 4: CÁLCULO DE XP (AUTORIDADE GAMEENGINE)
        // Fórmula gamificada: base + bônus competência + bônus nível
        Integer xpGanho = respostaCorreta
                ? calcularXP(analiseIA, jogador.getNivelGlobal())
                : 0;

        log.info("XP calculado: {} pontos", xpGanho);

        // ETAPA 5: PERSISTÊNCIA ATÔMICA
        if (respostaCorreta) {
            sessao.adicionarXPSessao(xpGanho);
            sessaoRepository.save(sessao);

            jogadorService.adicionarXP(jogadorId, xpGanho);

            log.info("Estado persistido: xpSessão={}", sessao.getXpSessao());
        }

        // ETAPA 6: DETERMINAÇÃO DO PRÓXIMO CENÁRIO
        String proximoCenario = determinarProximoCenario(cenarioAtual, respostaCorreta);

        if (!proximoCenario.equals(cenarioAtual)) {
            sessao.avancarCenario(proximoCenario);
            sessaoRepository.save(sessao);
            log.info("Cenário avançado: {} -> {}", cenarioAtual, proximoCenario);
        }

        // ETAPA 7: RETORNO ESTRUTURADO
        ResultadoTurnoDTO resultado = new ResultadoTurnoDTO(
                respostaCorreta,
                analiseIA.analiseSemantica(),
                xpGanho,
                analiseIA
        );

        log.info("Processamento concluído: sucesso={}, xp={}", respostaCorreta, xpGanho);
        return resultado;
    }

    private Integer calcularXP(AnaliseIADTO analiseIA, Integer nivelAtual) {
        Integer base = 100;
        Integer bonusCompetencia = analiseIA.competenciaComunicativa() / 2;
        Integer bonusNivel = nivelAtual * 10;
        Integer total = base + bonusCompetencia + bonusNivel;

        log.debug("XP: base={}, competência={}, nível={}, total={}",
                base, bonusCompetencia, bonusNivel, total);

        return total;
    }

    private String determinarProximoCenario(String cenarioAtual, boolean acertou) {
        if (!acertou || cenarioAtual == null) return cenarioAtual;

        return switch (cenarioAtual.toLowerCase()) {
            case "cappuccino" -> "latte";
            case "latte" -> "espresso";
            case "espresso" -> "cappuccino";
            default -> cenarioAtual;
        };
    }
}
