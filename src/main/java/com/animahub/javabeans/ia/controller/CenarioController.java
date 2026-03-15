package com.animahub.javabeans.ia.controller;

import com.animahub.javabeans.ia.dto.CenarioDTO;
import com.animahub.javabeans.ia.service.CenarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para fornecimento de metadados dos cenários pedagógicos.
 * Endpoint: GET /api/v1/jogo/cenario
 */
@RestController
@RequestMapping("/api/v1/jogo")
public class CenarioController {

    private final CenarioService cenarioService;

    public CenarioController(CenarioService cenarioService) {
        this.cenarioService = cenarioService;
    }

    /**
     * Retorna metadados do cenário atual para a sessão informada.
     * Inclui contexto narrativo, objetivos pedagógicos, dificuldade e recompensa de XP.
     *
     * @param sessionId UUID da sessão do jogador
     * @return CenarioDTO com todos os metadados do cenário (HTTP 200)
     */
    @GetMapping("/cenario")
    public ResponseEntity<CenarioDTO> getCenario(@RequestParam String sessionId) {
        CenarioDTO cenario = cenarioService.getCenario(sessionId);
        return ResponseEntity.ok(cenario);
    }
}
