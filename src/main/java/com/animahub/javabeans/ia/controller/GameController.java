package com.animahub.javabeans.ia.controller;

import com.animahub.javabeans.ia.dto.AcaoJogadorDTO;
import com.animahub.javabeans.ia.dto.CenarioDTO;
import com.animahub.javabeans.ia.dto.DiagnosticoDTO;
import com.animahub.javabeans.ia.dto.PrepararCafeRequest;
import com.animahub.javabeans.ia.dto.ResultadoPreparar;
import com.animahub.javabeans.ia.model.Receita;
import com.animahub.javabeans.ia.service.AIService;
import com.animahub.javabeans.ia.service.GameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final AIService aiService;

    public GameController(GameService gameService, AIService aiService) {
        this.gameService = gameService;
        this.aiService = aiService;
    }

    @GetMapping("/health")
    public ResponseEntity<DiagnosticoDTO> health() {
        return ResponseEntity.ok(new DiagnosticoDTO(
                "HEALTHY", "1.0-MVP", true, "Tudo ok!", false, 0, 0,
                "1.0-MVP", "development", LocalDateTime.now().toString()
        ));
    }

    @GetMapping("/cenario")
    public ResponseEntity<CenarioDTO> getCenario(HttpSession session) {
        Receita receita = gameService.getProximoCliente(session);
        CenarioDTO cenario = new CenarioDTO();
        cenario.setNomeBebida(receita.nomeBebida());
        cenario.setIngredientesEsperados(
                receita.ingredientesEsperados().stream().map(Enum::name).collect(Collectors.toList())
        );
        return ResponseEntity.ok(cenario);
    }

    @PostMapping("/processar-fala")
    public ResponseEntity<CenarioDTO> processarFala(
            @RequestBody AcaoJogadorDTO acao,
            HttpSession session) {

        // Valida com a IA se a fala do jogador faz sentido no cenário atual.
        // Não retornamos a avaliação diretamente; apenas garantimos que a IA foi consultada.
        try {
            aiService.avaliarResposta(acao.getResposta());
        } catch (Exception e) {
            System.err.println("Erro ao validar fala do jogador: " + e.getMessage());
        }

        // Retorna o cenário atualizado com a bebida e os ingredientes esperados
        Receita receita = gameService.getProximoCliente(session);
        CenarioDTO cenario = new CenarioDTO();
        cenario.setNomeBebida(receita.nomeBebida());
        cenario.setIngredientesEsperados(
                receita.ingredientesEsperados().stream().map(Enum::name).collect(Collectors.toList())
        );
        return ResponseEntity.ok(cenario);
    }

    @PostMapping("/preparar-cafe")
    public ResponseEntity<ResultadoPreparar> prepararCafe(
            @RequestBody PrepararCafeRequest request,
            HttpSession session) {
        ResultadoPreparar resultado = gameService.prepararCafe(session, request.ingredientesEscolhidos());
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
}
