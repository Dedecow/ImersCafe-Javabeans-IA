package com.animahub.javabeans.ia.service.engine;

import com.animahub.javabeans.ia.dto.AnaliseIADTO;
import com.animahub.javabeans.ia.dto.AvaliacaoRequest;
import com.animahub.javabeans.ia.dto.AvaliacaoResponse;
import com.animahub.javabeans.ia.dto.ProgressaoJogoDTO;
import com.animahub.javabeans.ia.exception.SessaoNaoEncontradaException;
import com.animahub.javabeans.ia.model.Avaliacao;
import com.animahub.javabeans.ia.model.Sessao;
import com.animahub.javabeans.ia.repository.AvaliacaoRepository;
import com.animahub.javabeans.ia.repository.SessaoRepository;
import com.animahub.javabeans.ia.service.CenarioService;
import com.animahub.javabeans.ia.service.ai.AIService;
import com.animahub.javabeans.ia.dto.CenarioDTO;
import org.springframework.stereotype.Service;

/**
 * Motor de jogo responsável pela orquestração do fluxo de avaliação de respostas.
 * Coordena a análise de IA, o cálculo de gamificação e a persistência do progresso.
 */
@Service
public class GameEngineService {

    private final AIService aiService;
    private final GamificationService gamificationService;
    private final ProgressionEngine progressionEngine;
    private final SessaoRepository sessaoRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final CenarioService cenarioService;

    public GameEngineService(AIService aiService,
                             GamificationService gamificationService,
                             ProgressionEngine progressionEngine,
                             SessaoRepository sessaoRepository,
                             AvaliacaoRepository avaliacaoRepository,
                             CenarioService cenarioService) {
        this.aiService = aiService;
        this.gamificationService = gamificationService;
        this.progressionEngine = progressionEngine;
        this.sessaoRepository = sessaoRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.cenarioService = cenarioService;
    }

    /**
     * Processa a resposta textual do discente, realiza análise de IA e atualiza o progresso.
     *
     * @param request AvaliacaoRequest com sessionId, cenarioId e resposta do discente
     * @return AvaliacaoResponse com resultado, feedback, XP ganho, análise de IA e progressão
     * @throws SessaoNaoEncontradaException  se a sessão não existir
     */
    public AvaliacaoResponse avaliar(AvaliacaoRequest request) {
        Sessao sessao = sessaoRepository.findBySessionId(request.getSessionId())
                .orElseThrow(() -> new SessaoNaoEncontradaException(request.getSessionId()));

        CenarioDTO cenario = cenarioService.getCenarioPorId(request.getCenarioId());

        // Análise semântica via serviço de IA
        AnaliseIADTO analiseIA = aiService.analisarResposta(
                request.getResposta(),
                cenario.getContextoNarrativo()
        );

        // Lógica de gamificação
        boolean correto = gamificationService.avaliarCorreto(analiseIA.getCompetenciaComunicativa());
        int xpGanho = gamificationService.calcularXpGanho(correto, analiseIA.getCompetenciaComunicativa(), cenario.getXpRecompensa());

        // Atualiza progresso da sessão
        int novoXp = sessao.getXpTotal() + xpGanho;
        int novoNivel = progressionEngine.calcularNivel(novoXp);
        sessao.setXpTotal(novoXp);
        sessao.setNivelAtual(novoNivel);
        sessaoRepository.save(sessao);

        // Persiste registro de avaliação para auditoria
        Avaliacao avaliacao = new Avaliacao(
                request.getSessionId(),
                request.getCenarioId(),
                request.getResposta(),
                correto,
                xpGanho
        );
        avaliacaoRepository.save(avaliacao);

        String feedback = correto
                ? "Excelente! Sua comunicação foi eficaz e o cliente ficou satisfeito."
                : "Sua resposta precisa de aprimoramento. Confira as sugestões da análise de IA.";

        return new AvaliacaoResponse(
                correto,
                feedback,
                xpGanho,
                analiseIA,
                new ProgressaoJogoDTO(novoXp, novoNivel)
        );
    }
}
