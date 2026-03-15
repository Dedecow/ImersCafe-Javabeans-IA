package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.dto.JogadorDTO;
import com.animahub.javabeans.ia.model.Jogador;
import com.animahub.javabeans.ia.repository.JogadorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Slf4j
public class JogadorService {

    private final JogadorRepository jogadorRepository;

    public JogadorService(JogadorRepository jogadorRepository) {
        this.jogadorRepository = jogadorRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public JogadorDTO criarJogador(String email, String nome, String nomePersonagem) {
        log.info("Criando novo jogador: email={}, nome={}", email, nome);

        if (jogadorRepository.existsByEmail(email)) {
            log.warn("Email já cadastrado: {}", email);
            throw new IllegalArgumentException("Email já existe: " + email);
        }

        Jogador jogador = Jogador.builder()
                .email(email)
                .nome(nome)
                .nomePersonagem(nomePersonagem)
                .xpTotal(0)
                .nivelGlobal(1)
                .ativo(true)
                .build();

        Jogador salvo = jogadorRepository.save(jogador);
        log.info("Jogador criado com ID: {}", salvo.getId());

        return mapToDTO(salvo);
    }

    @Transactional(readOnly = true)
    public JogadorDTO obterPorId(UUID id) {
        log.debug("Buscando jogador por ID: {}", id);

        Jogador jogador = jogadorRepository.findByIdWithSessoes(id)
                .orElseThrow(() -> new NoSuchElementException("Jogador não encontrado: " + id));

        return mapToDTO(jogador);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void adicionarXP(UUID jogadorId, Integer pontos) {
        log.info("Adicionando XP ao jogador: jogadorId={}, pontos={}", jogadorId, pontos);

        if (pontos < 0) {
            throw new IllegalArgumentException("Pontos não podem ser negativos");
        }

        Jogador jogador = jogadorRepository.findById(jogadorId)
                .orElseThrow(() -> new NoSuchElementException("Jogador não encontrado: " + jogadorId));

        Integer xpAnterior = jogador.getXpTotal();
        Integer nivelAnterior = jogador.getNivelGlobal();

        jogador.adicionarXP(pontos);

        jogadorRepository.save(jogador);

        log.info("XP adicionado. XP anterior={}, novo={}, nível anterior={}, novo={}",
                xpAnterior, jogador.getXpTotal(), nivelAnterior, jogador.getNivelGlobal());
    }

    private JogadorDTO mapToDTO(Jogador jogador) {
        return new JogadorDTO(
                jogador.getId(),
                jogador.getNome(),
                jogador.getNomePersonagem(),
                jogador.getXpTotal(),
                jogador.getNivelGlobal(),
                jogador.getDataCriacao()
        );
    }
}
