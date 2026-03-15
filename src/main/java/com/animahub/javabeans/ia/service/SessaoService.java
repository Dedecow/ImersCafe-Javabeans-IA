package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.dto.SessaoDTO;
import com.animahub.javabeans.ia.model.Jogador;
import com.animahub.javabeans.ia.model.Sessao;
import com.animahub.javabeans.ia.repository.JogadorRepository;
import com.animahub.javabeans.ia.repository.SessaoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Slf4j
public class SessaoService {

    private final SessaoRepository sessaoRepository;
    private final JogadorRepository jogadorRepository;

    public SessaoService(SessaoRepository sessaoRepository, JogadorRepository jogadorRepository) {
        this.sessaoRepository = sessaoRepository;
        this.jogadorRepository = jogadorRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SessaoDTO iniciarSessao(UUID jogadorId, String cenarioInicial) {
        log.info("Iniciando nova sessão: jogadorId={}, cenário={}", jogadorId, cenarioInicial);

        Jogador jogador = jogadorRepository.findById(jogadorId)
                .orElseThrow(() -> new NoSuchElementException("Jogador não encontrado: " + jogadorId));

        if (!jogador.getAtivo()) {
            throw new IllegalStateException("Jogador inativo");
        }

        if (sessaoRepository.findByJogadorIdAndAtivaTrue(jogadorId).isPresent()) {
            throw new IllegalStateException("Jogador já possui sessão ativa");
        }

        Sessao sessao = Sessao.builder()
                .jogador(jogador)
                .cenarioAtual(cenarioInicial)
                .xpSessao(0)
                .ativa(true)
                .build();

        Sessao salva = sessaoRepository.save(sessao);
        log.info("Sessão iniciada com ID: {}", salva.getId());

        return mapToDTO(salva);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void encerrarSessao(UUID sessaoId) {
        log.info("Encerrando sessão: sessaoId={}", sessaoId);

        Sessao sessao = sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new NoSuchElementException("Sessão não encontrada: " + sessaoId));

        if (!sessao.getAtiva()) {
            log.warn("Sessão já estava encerrada");
            return;
        }

        sessao.encerrar();
        sessaoRepository.save(sessao);

        Long duracao = sessao.calcularDuracaoMinutos();
        log.info("Sessão encerrada. Duração: {} minutos, XP final: {}",
                duracao, sessao.getXpSessao());
    }

    private SessaoDTO mapToDTO(Sessao sessao) {
        return new SessaoDTO(
                sessao.getId(),
                sessao.getJogador().getId(),
                sessao.getCenarioAtual(),
                sessao.getXpSessao(),
                sessao.getAtiva(),
                sessao.getDataCriacao()
        );
    }
}
