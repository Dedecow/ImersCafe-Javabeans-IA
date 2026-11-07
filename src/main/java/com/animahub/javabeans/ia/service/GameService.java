package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.dto.AvaliacaoDTO;
import com.animahub.javabeans.ia.dto.ResultadoPreparar;
import com.animahub.javabeans.ia.engine.GameEngine;
import com.animahub.javabeans.ia.model.Ingrediente;
import com.animahub.javabeans.ia.model.Receita;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GameService {

    private static final Pattern NOME_PATTERN = Pattern.compile(
            "(?i)(?:meu nome é|meu nome e|eu sou o|eu sou a|chamo-me|me chamo|pode me chamar de|chame-me de|chame de)\\s+([A-ZÁ-ÚÀ-Õ][a-zá-úà-õ]+)"
    );

    private final AIService aiService;
    private final GameEngine gameEngine;
    private final Map<Integer, Receita> cenarios = new HashMap<>();

    public GameService(AIService aiService, GameEngine gameEngine) {
        this.aiService = aiService;
        this.gameEngine = gameEngine;
        carregarReceitasIniciais();
    }

    public AvaliacaoDTO processarResposta(HttpSession session, String respostaJogador) {
        extrairEPersistirNomeUsuario(respostaJogador, session);

        String sessionId = session.getId();
        String nomeJogador = (String) session.getAttribute("nomeJogador");

        AvaliacaoDTO avaliacao = gameEngine.processarTurno(
                sessionId,
                respostaJogador,
                nomeJogador
        );

        int pontuacaoAtual = getPontuacaoTotal(session);
        pontuacaoAtual += avaliacao.getXpGanho();

        session.setAttribute("pontuacaoTotal", pontuacaoAtual);

        return avaliacao;
    }

    private void extrairEPersistirNomeUsuario(String textoUsuario, HttpSession session) {
        if (session.getAttribute("nomeJogador") != null || textoUsuario == null) {
            return;
        }

        Matcher matcher = NOME_PATTERN.matcher(textoUsuario.trim());

        if (matcher.find()) {
            session.setAttribute("nomeJogador", matcher.group(1));
        }
    }

    private void carregarReceitasIniciais() {
        cenarios.put(1, new Receita(
                "Cappuccino",
                List.of(Ingrediente.CAFE, Ingrediente.LEITE, Ingrediente.ESPUMA),
                10
        ));

        cenarios.put(2, new Receita(
                "Mocha",
                List.of(Ingrediente.CAFE, Ingrediente.CHOCOLATE, Ingrediente.LEITE),
                12
        ));

        cenarios.put(3, new Receita(
                "Latte",
                List.of(Ingrediente.CAFE, Ingrediente.LEITE, Ingrediente.BAUNILHA),
                8
        ));

        cenarios.put(4, new Receita(
                "Café com Chantilly",
                List.of(Ingrediente.CAFE, Ingrediente.CHANTILLY, Ingrediente.CARAMELO),
                15
        ));
    }

    public Receita getProximoCliente(HttpSession session) {
        return getCenarioAtual(session);
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

    public ResultadoPreparar prepararCafe(
            HttpSession session,
            List<String> ingredientesEscolhidosStr,
            String falaBarista
    ) {
        Receita receitaEsperada = getCenarioAtual(session);
        int pontuacaoTotal = getPontuacaoTotal(session);

        List<Ingrediente> escolhidos = converterIngredientes(ingredientesEscolhidosStr);

        boolean acertou = validarReceita(receitaEsperada, escolhidos);

        int pontos = acertou ? receitaEsperada.pontosBase() : 0;
        pontuacaoTotal += pontos;

        String nomeJogador = (String) session.getAttribute("nomeJogador");

        String feedbackIA = aiService.gerarFeedbackBarista(
                receitaEsperada.nomeBebida(),
                escolhidos,
                nomeJogador,
                falaBarista
        );

        int proximoCenario = calcularProximoCenario(session);

        atualizarEstado(session, proximoCenario, pontuacaoTotal);

        return new ResultadoPreparar(
                acertou,
                pontos,
                pontuacaoTotal,
                feedbackIA,
                cenarios.get(proximoCenario)
        );
    }

    private List<Ingrediente> converterIngredientes(List<String> ingredientesEscolhidosStr) {
        try {
            return ingredientesEscolhidosStr.stream()
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(Ingrediente::valueOf)
                    .toList();
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

    private boolean validarReceita(Receita receitaEsperada, List<Ingrediente> escolhidos) {
        return escolhidos.containsAll(receitaEsperada.ingredientesEsperados())
                && receitaEsperada.ingredientesEsperados().containsAll(escolhidos);
    }

    private int calcularProximoCenario(HttpSession session) {
        Integer cenarioAtual = (Integer) session.getAttribute("cenarioAtual");

        if (cenarioAtual == null) {
            cenarioAtual = 1;
        }

        return (cenarioAtual % cenarios.size()) + 1;
    }

    private int getPontuacaoTotal(HttpSession session) {
        Integer pontos = (Integer) session.getAttribute("pontuacaoTotal");
        return pontos != null ? pontos : 0;
    }

    private void atualizarEstado(HttpSession session, int novoCenario, int novaPontuacao) {
        session.setAttribute("cenarioAtual", novoCenario);
        session.setAttribute("pontuacaoTotal", novaPontuacao);
    }
}
