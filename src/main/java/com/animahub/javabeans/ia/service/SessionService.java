package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.dto.AvaliacaoDTO;
import com.animahub.javabeans.ia.engine.SessionEngine;
import com.animahub.javabeans.ia.model.AvaliacaoAtendimento;
import com.animahub.javabeans.ia.model.Ingrediente;
import com.animahub.javabeans.ia.model.ReceitaEntity;
import com.animahub.javabeans.ia.model.Rodada;
import com.animahub.javabeans.ia.model.Sessao;
import com.animahub.javabeans.ia.repository.AvaliacaoAtendimentoRepository;
import com.animahub.javabeans.ia.repository.ReceitaRepository;
import com.animahub.javabeans.ia.repository.RodadaRepository;
import com.animahub.javabeans.ia.repository.SessaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

/**
 * Tudo o que lê ou grava o estado do jogo no banco de dados.
 */
@Service
public class SessionService {

    private final SessaoRepository sessaoRepository;
    private final ReceitaRepository receitaRepository;
    private final RodadaRepository rodadaRepository;
    private final AvaliacaoAtendimentoRepository avaliacaoRepository;
    private final SessionEngine sessionEngine;

    public SessionService(SessaoRepository sessaoRepository,
                          ReceitaRepository receitaRepository,
                          RodadaRepository rodadaRepository,
                          AvaliacaoAtendimentoRepository avaliacaoRepository,
                          SessionEngine sessionEngine) {
        this.sessaoRepository = sessaoRepository;
        this.receitaRepository = receitaRepository;
        this.rodadaRepository = rodadaRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.sessionEngine = sessionEngine;
    }

    /**
     * Cria uma sessão nova no banco, começando pelo primeiro cliente da sequência.
     */
    public Sessao iniciarNovaSessao(String nomeBarista) {
        Sessao novaSessao = new Sessao(sessionEngine.iniciarNovaSessao(), nomeBarista, primeiraReceita());
        return sessaoRepository.save(novaSessao);
    }

    public Optional<Sessao> buscarSessao(String sessionId) {
        return sessaoRepository.findBySessionId(sessionId);
    }

    public ReceitaEntity primeiraReceita() {
        return receitaRepository.findFirstByOrderByOrdemAsc()
                .orElseThrow(() -> new IllegalStateException("Nenhuma receita cadastrada no banco."));
    }

    /**
     * Próximo cliente da sequência; depois do último, volta ao primeiro.
     */
    public ReceitaEntity proximaReceita(ReceitaEntity atual) {
        return receitaRepository.findFirstByOrdemGreaterThanOrderByOrdemAsc(atual.getOrdem())
                .orElseGet(this::primeiraReceita);
    }

    /**
     * Grava a rodada de preparo, soma os pontos e avança para o próximo cliente.
     * Tudo numa única transação: ou grava tudo, ou nada. A partida fica travada no banco
     * durante a gravação, para que requisições simultâneas não percam XP.
     */
    @Transactional
    public Sessao registrarRodada(Sessao sessao, ReceitaEntity receita, Collection<Ingrediente> escolhidos,
                                 boolean acertou, int pontos, String falaBarista, String feedbackIa) {
        Sessao travada = travar(sessao);
        // Outra requisição simultânea já atendeu este cliente: não pontua duas vezes
        if (!travada.getReceitaAtual().getId().equals(receita.getId())) {
            throw new RodadaJaRegistradaException();
        }
        rodadaRepository.save(new Rodada(travada, receita, escolhidos, acertou, pontos, falaBarista, feedbackIa));
        travada.adicionarXp(pontos);
        travada.setReceitaAtual(proximaReceita(receita));
        return travada;
    }

    /**
     * Grava a avaliação de atendimento feita pela IA e soma o XP na sessão.
     */
    @Transactional
    public Sessao registrarAvaliacao(Sessao sessao, String respostaAluno, AvaliacaoDTO avaliacao, String nomeBarista) {
        Sessao travada = travar(sessao);
        if (travada.getNomeBarista() == null && nomeBarista != null) {
            travada.setNomeBarista(nomeBarista);
        }
        avaliacaoRepository.save(new AvaliacaoAtendimento(
                travada,
                respostaAluno,
                avaliacao.getTipoCliente(),
                avaliacao.getXpGanho(),
                avaliacao.getFeedbackEducativo(),
                avaliacao.isPersonaQuebrada(),
                avaliacao.isCenarioConcluido()
        ));
        travada.adicionarXp(avaliacao.getXpGanho());
        return travada;
    }

    private Sessao travar(Sessao sessao) {
        return sessaoRepository.buscarParaAtualizar(sessao.getId())
                .orElseThrow(() -> new IllegalStateException("Partida não encontrada: " + sessao.getId()));
    }

    public long contarRodadas(Sessao sessao) {
        return rodadaRepository.countBySessao(sessao);
    }

    public long contarAvaliacoes(Sessao sessao) {
        return avaliacaoRepository.countBySessao(sessao);
    }

    public long contarSessoes() {
        return sessaoRepository.count();
    }

    public long somarXpDistribuido() {
        return sessaoRepository.somarXpTotal();
    }
}
