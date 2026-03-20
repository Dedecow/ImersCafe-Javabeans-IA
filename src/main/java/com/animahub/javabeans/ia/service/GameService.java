package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.dto.AvaliacaoDTO;
import com.animahub.javabeans.ia.dto.ResultadoPreparar;
import com.animahub.javabeans.ia.engine.GameEngine;
import com.animahub.javabeans.ia.model.Ingrediente;
import com.animahub.javabeans.ia.model.Receita;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {

    private final AIService aiService;
    private final Map<Integer, Receita> cenarios = new HashMap<>();
    private final GameEngine gameEngine;

    public GameService(AIService aiService, GameEngine gameEngine) {
        this.aiService = aiService;
        this.gameEngine = gameEngine;
        carregarReceitasIniciais();
    }
    public AvaliacaoDTO processarResposta(HttpSession session, String respostaAluno) {
        String sessionId = session.getId();

        AvaliacaoDTO avaliacao = gameEngine.processarTurno(sessionId, respostaAluno);

        Integer pontuacaoAtual = (Integer) session.getAttribute("pontuacaoTotal");
        if (pontuacaoAtual == null) pontuacaoAtual = 0;

        pontuacaoAtual += avaliacao.getXpGanho();

        session.setAttribute("pontuacaoTotal", pontuacaoAtual);

        return avaliacao;
    }

    private void carregarReceitasIniciais() {
        cenarios.put(1, new Receita("Cappuccino", List.of(Ingrediente.CAFE, Ingrediente.LEITE, Ingrediente.ESPUMA), 10));
        cenarios.put(2, new Receita("Mocha", List.of(Ingrediente.CAFE, Ingrediente.CHOCOLATE, Ingrediente.LEITE), 12));
        cenarios.put(3, new Receita("Latte", List.of(Ingrediente.CAFE, Ingrediente.LEITE, Ingrediente.BAUNILHA), 8));
        cenarios.put(4, new Receita("Café com Chantilly", List.of(Ingrediente.CAFE, Ingrediente.CHANTILLY, Ingrediente.CARAMELO), 15));
    }

    private Receita getCenarioAtual(HttpSession session) {
        Integer cenario = (Integer) session.getAttribute("cenarioAtual");
        if (cenario == null || !cenarios.containsKey(cenario)) {
            cenario = 1;
            session.setAttribute("cenarioAtual", cenario);
            session.setAttribute("pontuacaoTotal", 0);
        }
        return cenarios.get(cenario);
    }

    private int getPontuacaoTotal(HttpSession session) {
        Integer pontos = (Integer) session.getAttribute("pontuacaoTotal");
        return pontos != null ? pontos : 0;
    }

    private void atualizarEstado(HttpSession session, int novoCenario, int novaPontuacao) {
        session.setAttribute("cenarioAtual", novoCenario);
        session.setAttribute("pontuacaoTotal", novaPontuacao);
    }

    public Receita getProximoCliente(HttpSession session) {
        return getCenarioAtual(session);
    }

    public ResultadoPreparar prepararCafe(HttpSession session, List<String> ingredientesEscolhidosStr) {
        Receita receitaEsperada = getCenarioAtual(session);
        int pontuacaoTotal = getPontuacaoTotal(session);

        List<Ingrediente> escolhidos;
        try {
            escolhidos = ingredientesEscolhidosStr.stream()
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(Ingrediente::valueOf)
                    .toList();
        } catch (IllegalArgumentException e) {
            escolhidos = Collections.emptyList();
        }

        // CORRIGIDO: Agora a ordem em que o aluno clica não importa
        boolean acertou = escolhidos.containsAll(receitaEsperada.ingredientesEsperados()) && 
                          receitaEsperada.ingredientesEsperados().containsAll(escolhidos);

        int pontos = acertou ? receitaEsperada.pontosBase() : 0;
        pontuacaoTotal += pontos;

        String feedbackIA = aiService.gerarFeedbackBarista(
                receitaEsperada.nomeBebida(),
                escolhidos,
                receitaEsperada.ingredientesEsperados(),
                acertou
        );

        // CORRIGIDO: Avanço linear (1, 2, 3, 4, 1...)
        Integer cenarioAtual = (Integer) session.getAttribute("cenarioAtual");
        if (cenarioAtual == null) cenarioAtual = 1;
        int proximoCenario = (cenarioAtual % cenarios.size()) + 1;

        atualizarEstado(session, proximoCenario, pontuacaoTotal);

        return new ResultadoPreparar(acertou, pontos, pontuacaoTotal, feedbackIA, cenarios.get(proximoCenario));
    }
}
