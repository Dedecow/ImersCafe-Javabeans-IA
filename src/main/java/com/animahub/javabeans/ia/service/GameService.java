package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.dto.AvaliacaoDTO;
import com.animahub.javabeans.ia.dto.ResultadoPreparar;
import com.animahub.javabeans.ia.engine.GameEngine;
import com.animahub.javabeans.ia.model.Ingrediente;
import com.animahub.javabeans.ia.model.Receita;
import com.animahub.javabeans.ia.model.ReceitaEntity;
import com.animahub.javabeans.ia.model.Sessao;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GameService {

    // A HttpSession guarda só o identificador da sessão; o estado do jogo fica no banco
    private static final String ATRIBUTO_SESSAO_BANCO = "sessionIdBanco";

    static final String FEEDBACK_IA_INDISPONIVEL =
            "O cliente recebeu o pedido. (O comentário do cliente está indisponível no momento.)";

    private final AIService aiService;
    private final GameEngine gameEngine;
    private final SessionService sessionService;

    public GameService(AIService aiService, GameEngine gameEngine, SessionService sessionService) {
        this.aiService = aiService;
        this.gameEngine = gameEngine;
        this.sessionService = sessionService;
    }

    /**
     * Começa um jogo novo: cria a sessão no banco e guarda o identificador na HttpSession.
     */
    public void iniciarSessao(HttpSession session, String nomeBarista) {
        Sessao sessao = sessionService.iniciarNovaSessao(nomeBarista);
        session.setAttribute(ATRIBUTO_SESSAO_BANCO, sessao.getSessionId());
    }

    public Optional<Sessao> buscarSessaoAtual(HttpSession session) {
        String sessionId = (String) session.getAttribute(ATRIBUTO_SESSAO_BANCO);
        return sessionId == null ? Optional.empty() : sessionService.buscarSessao(sessionId);
    }

    // Se o frontend jogar sem chamar /iniciar, a sessão é criada na primeira jogada
    private Sessao obterOuCriarSessao(HttpSession session) {
        return buscarSessaoAtual(session).orElseGet(() -> {
            iniciarSessao(session, (String) session.getAttribute("nomeJogador"));
            return buscarSessaoAtual(session).orElseThrow();
        });
    }

    public AvaliacaoDTO processarResposta(HttpSession session, String respostaAluno) {
        extrairEPersistirNomeUsuario(respostaAluno, session);

        Sessao sessao = obterOuCriarSessao(session);
        String nomeJogador = (String) session.getAttribute("nomeJogador");

        AvaliacaoDTO avaliacao = gameEngine.processarTurno(session.getId(), respostaAluno, nomeJogador);

        sessionService.registrarAvaliacao(sessao, respostaAluno, avaliacao, nomeJogador);

        return avaliacao;
    }

    private void extrairEPersistirNomeUsuario(String textoUsuario, HttpSession session) {
        if (session.getAttribute("nomeJogador") == null && textoUsuario != null) {
            Pattern pattern = Pattern.compile(
                    "(?i)(?:meu nome é|meu nome e|eu sou o|eu sou a|chamo-me|me chamo|pode me chamar de|chame-me de|chame de)\\s+([A-ZÁ-ÚÀ-Õ][a-zá-úà-õ]+)"
            );
            Matcher matcher = pattern.matcher(textoUsuario.trim());

            if (matcher.find()) {
                String nomeExtraido = matcher.group(1);
                session.setAttribute("nomeJogador", nomeExtraido);
            }
        }
    }

    public Receita getProximoCliente(HttpSession session) {
        ReceitaEntity receita = buscarSessaoAtual(session)
                .map(Sessao::getReceitaAtual)
                .orElseGet(sessionService::primeiraReceita);
        return receita.paraReceita();
    }

    public ResultadoPreparar prepararCafe(HttpSession session, List<String> ingredientesEscolhidosStr, String falaBarista) {
        Sessao sessao = obterOuCriarSessao(session);
        ReceitaEntity receitaEsperada = sessao.getReceitaAtual();

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

        boolean acertou = escolhidos.containsAll(receitaEsperada.getIngredientes())
                && receitaEsperada.getIngredientes().containsAll(escolhidos);

        int pontos = acertou ? receitaEsperada.getPontosBase() : 0;

        String nomeJogador = (String) session.getAttribute("nomeJogador");

        // Repassamos a fala do barista para o motor da IA. Se a IA falhar, a rodada (já avaliada
        // pelo backend) é gravada mesmo assim, com um comentário neutro no lugar do texto da IA.
        String feedbackIA;
        try {
            feedbackIA = aiService.gerarFeedbackBarista(
                    receitaEsperada.getNomeBebida(),
                    escolhidos,
                    nomeJogador,
                    falaBarista
            );
        } catch (RuntimeException e) {
            feedbackIA = FEEDBACK_IA_INDISPONIVEL;
        }

        Sessao atualizada = sessionService.registrarRodada(
                sessao, receitaEsperada, escolhidos, acertou, pontos, falaBarista, feedbackIA);

        return new ResultadoPreparar(acertou, pontos, atualizada.getXpTotal(), feedbackIA,
                atualizada.getReceitaAtual().paraReceita());
    }
}
