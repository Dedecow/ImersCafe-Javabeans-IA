package com.animahub.javabeans.ia.controller;

import com.animahub.javabeans.ia.dto.SessaoCreateRequest;
import com.animahub.javabeans.ia.dto.SessaoDTO;
import com.animahub.javabeans.ia.service.SessaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para gestão do ciclo de vida das sessões de jogo.
 * Endpoint: POST /api/v1/sessao
 */
@RestController
@RequestMapping("/api/v1/sessao")
public class SessaoController {

    private final SessaoService sessaoService;

    public SessaoController(SessaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    /**
     * Inicializa uma nova sessão de usuário com UUID único para rastreabilidade.
     *
     * @param request contém nomeUsuario e nomePersonagem
     * @return SessaoDTO com sessionId, dados do jogador e estado inicial (HTTP 201)
     */
    @PostMapping
    public ResponseEntity<SessaoDTO> criarSessao(@RequestBody SessaoCreateRequest request) {
        SessaoDTO sessao = sessaoService.criarSessao(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessao);
    }
}
