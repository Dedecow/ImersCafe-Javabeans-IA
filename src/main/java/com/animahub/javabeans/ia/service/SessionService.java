package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.dto.SessaoInicioDTO;
import com.animahub.javabeans.ia.dto.CenarioDTO;
import com.animahub.javabeans.ia.engine.GameEngine;
import com.animahub.javabeans.ia.engine.SessionEngine;
import com.animahub.javabeans.ia.repository.SessaoRepository;
import org.springframework.stereotype.Service;

/**
 * Serviço legado de sessão baseado em HttpSession.
 *
 * @deprecated Substituído por {@link SessaoService} na Fase 2.
 *             Este serviço mantém compatibilidade com o fluxo HttpSession do GameController.
 */
@Deprecated
@Service
public class SessionService {

    private final SessaoRepository sessaoRepository;
    private final SessionEngine sessionEngine;
    private final GameEngine gameEngine;

    public SessionService(SessaoRepository sessaoRepository,
                          SessionEngine sessionEngine,
                          GameEngine gameEngine) {
        this.sessaoRepository = sessaoRepository;
        this.sessionEngine = sessionEngine;
        this.gameEngine = gameEngine;
    }

    /**
     * Inicia uma nova sessão via SessionEngine e retorna o cenário inicial.
     * Para sessões persistidas com Jogador, utilize {@link SessaoService#iniciarSessao}.
     *
     * @return SessaoInicioDTO com sessionId e cenário inicial
     */
    public SessaoInicioDTO iniciarNovaSessao() {
        String sessionId = sessionEngine.iniciarNovaSessao();
        CenarioDTO cenario = gameEngine.carregarCenarioInicial();
        return new SessaoInicioDTO(sessionId, cenario);
    }
}

