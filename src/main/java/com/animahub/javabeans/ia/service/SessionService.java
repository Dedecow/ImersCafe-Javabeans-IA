package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.dto.SessaoInicioDTO;
import com.animahub.javabeans.ia.dto.CenarioDTO;
import com.animahub.javabeans.ia.engine.GameEngine;
import com.animahub.javabeans.ia.engine.SessionEngine;
import com.animahub.javabeans.ia.model.Sessao;
import com.animahub.javabeans.ia.repository.SessaoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionService {

    private final SessaoRepository sessaoRepository;
    private final SessionEngine sessionEngine;
    private final GameEngine gameEngine;

    public SessionService(
            SessaoRepository sessaoRepository,
            SessionEngine sessionEngine,
            GameEngine gameEngine
    ) {
        this.sessaoRepository = sessaoRepository;
        this.sessionEngine = sessionEngine;
        this.gameEngine = gameEngine;
    }

    public SessaoInicioDTO iniciarNovaSessao() {
        String sessionId = sessionEngine.iniciarNovaSessao();

        Sessao novaSessao = new Sessao(sessionId, 0);
        sessaoRepository.save(novaSessao);

        CenarioDTO cenario = gameEngine.carregarCenarioInicial();

        return new SessaoInicioDTO(sessionId, cenario);
    }

    public void atualizarProgresso(String sessionId, int xpGanho) {
        sessaoRepository.findBySessionId(sessionId)
                .ifPresent(sessao -> {
                    sessao.setXpTotal(sessao.getXpTotal() + xpGanho);
                    sessaoRepository.save(sessao);
                });
    }

    public Optional<Sessao> buscarSessao(String sessionId) {
        return sessaoRepository.findBySessionId(sessionId);
    }
}
