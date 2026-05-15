package com.animahub.javabeans.ia.controller;

import com.animahub.javabeans.ia.dto.AvaliacaoDTO;
import com.animahub.javabeans.ia.dto.DiagnosticoDTO;
import com.animahub.javabeans.ia.dto.PrepararCafeRequest;
import com.animahub.javabeans.ia.dto.ResultadoPreparar;
import com.animahub.javabeans.ia.service.AIService;
import com.animahub.javabeans.ia.service.GameService;
import com.animahub.javabeans.ia.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final AIService aiService;
    private final SessionService sessionService; // Adicionado para suportar a persistência na base de dados

    public GameController(GameService gameService, AIService aiService, SessionService sessionService) {
        this.gameService = gameService;
        this.aiService = aiService;
        this.sessionService = sessionService;
    }

    // O método foi movido para dentro do escopo da classe
    @PostMapping("/iniciar")
    public ResponseEntity<Void> iniciarSessao(@RequestBody Map<String, String> payload, HttpSession session) {
        String nomeBarista = payload.get("nomeBarista");
        if (nomeBarista != null && !nomeBarista.trim().isEmpty()) {
            session.setAttribute("nomeJogador", nomeBarista.trim());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/health")
    public ResponseEntity<DiagnosticoDTO> health() {
        return ResponseEntity.ok(new DiagnosticoDTO(
                "HEALTHY", "1.0-MVP", true, "Tudo ok!", false, 0, 0,
                "1.0-MVP", "development", LocalDateTime.now().toString()
        ));
    }

    @GetMapping("/cenario")
    public ResponseEntity<String> getCenario(HttpSession session) {
        return ResponseEntity.ok(gameService.getProximoCliente(session).nomeBebida());
    }

    @PostMapping("/preparar-cafe")
    public ResponseEntity<ResultadoPreparar> prepararCafe(
            HttpSession session,
            @RequestBody PrepararCafeRequest request) {
        ResultadoPreparar resultado = gameService.prepararCafe(
                session,
                request.ingredientesEscolhidos(),
                request.falaBarista()
        );
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/debug-sessao")
    public ResponseEntity<Map<String, Object>> debugSessao(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        map.put("sessionId", session.getId());
        map.put("cenarioAtual", session.getAttribute("cenarioAtual"));
        map.put("pontuacaoTotal", session.getAttribute("pontuacaoTotal"));
        return ResponseEntity.ok(map);
    }

    @PostMapping("/responder")
    public ResponseEntity<AvaliacaoDTO> responder(
            @RequestBody String respostaAluno,
            HttpSession session) {

        AvaliacaoDTO avaliacao = gameService.processarResposta(session, respostaAluno);
        return ResponseEntity.ok(avaliacao);
    }
}