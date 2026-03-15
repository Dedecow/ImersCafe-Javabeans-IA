package com.animahub.javabeans.ia.controller;

import com.animahub.javabeans.ia.dto.AvaliacaoRequest;
import com.animahub.javabeans.ia.dto.AvaliacaoResponse;
import com.animahub.javabeans.ia.service.engine.GameEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para processamento e avaliação de respostas do discente.
 * Endpoint: POST /api/v1/jogo/avaliar
 */
@RestController
@RequestMapping("/api/v1/jogo")
public class AvaliacaoController {

    private final GameEngineService gameEngineService;

    public AvaliacaoController(GameEngineService gameEngineService) {
        this.gameEngineService = gameEngineService;
    }

    /**
     * Processa a entrada textual do discente, valida estado do jogo,
     * integra com IA para análise semântica e retorna feedback pedagógico.
     *
     * @param request AvaliacaoRequest com sessionId, cenarioId e resposta do discente
     * @return AvaliacaoResponse com resultado, feedback, XP ganho, análise de IA e progressão (HTTP 200)
     */
    @PostMapping("/avaliar")
    public ResponseEntity<AvaliacaoResponse> avaliar(@RequestBody AvaliacaoRequest request) {
        AvaliacaoResponse response = gameEngineService.avaliar(request);
        return ResponseEntity.ok(response);
    }
}
