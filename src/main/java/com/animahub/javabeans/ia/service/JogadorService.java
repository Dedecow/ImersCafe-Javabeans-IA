package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.dto.JogadorDTO;
import com.animahub.javabeans.ia.model.Jogador;
import com.animahub.javabeans.ia.repository.JogadorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Serviço de domínio responsável pelo ciclo de vida do Jogador.
 *
 * Princípio de Responsabilidade Única: Gerencia exclusivamente operações
 * relacionadas à entidade Jogador (criação, consulta e progressão de XP).
 *
 * Conformidade: Chen et al. (2025) - Sistemas gamificados com acúmulo de experiência
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class JogadorService {

    private final JogadorRepository jogadorRepository;

    /**
     * Cria um novo jogador no sistema.
     *
     * Isolamento READ_COMMITTED: Evita leituras sujas ao verificar duplicidade de email.
     * Inicializa: xpTotal=0, nivelGlobal=1, ativo=true.
     *
     * @param email          Email único do jogador
     * @param nome           Nome completo do jogador
     * @param nomePersonagem Nome do personagem (pode ser null)
     * @return JogadorDTO com os dados do jogador criado
     * @throws IllegalArgumentException se o email já estiver cadastrado
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public JogadorDTO criarJogador(String email, String nome, String nomePersonagem) {
        log.info("Criando jogador com email={}, nome={}", email, nome);

        if (jogadorRepository.existsByEmail(email)) {
            log.warn("Tentativa de criar jogador com email duplicado: {}", email);
            throw new IllegalArgumentException("Email já cadastrado: " + email);
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
        log.info("Jogador criado com sucesso: id={}, email={}", salvo.getId(), salvo.getEmail());

        return toDTO(salvo);
    }

    /**
     * Recupera os dados de um jogador pelo seu ID, incluindo sessões associadas.
     *
     * readOnly=true: Garante otimização de leitura sem overhead transacional de escrita.
     * Utiliza LEFT JOIN FETCH para evitar N+1 no carregamento de sessões.
     *
     * @param id UUID do jogador
     * @return JogadorDTO com os dados do jogador
     * @throws IllegalArgumentException se o jogador não for encontrado
     */
    @Transactional(readOnly = true)
    public JogadorDTO obterPorId(UUID id) {
        log.info("Buscando jogador por id={}", id);

        Jogador jogador = jogadorRepository.findByIdWithSessoes(id)
                .orElseThrow(() -> {
                    log.warn("Jogador não encontrado: id={}", id);
                    return new IllegalArgumentException("Jogador não encontrado: " + id);
                });

        log.info("Jogador encontrado: id={}, nivelGlobal={}, xpTotal={}", id, jogador.getNivelGlobal(), jogador.getXpTotal());
        return toDTO(jogador);
    }

    /**
     * Adiciona XP ao jogador e recalcula o nível global de forma atômica.
     *
     * Isolamento SERIALIZABLE: Garante isolamento total na operação crítica de XP,
     * evitando condições de corrida em atualizações concorrentes.
     *
     * @param jogadorId UUID do jogador
     * @param pontos    Quantidade de XP a adicionar (deve ser >= 0)
     * @throws IllegalArgumentException se o jogador não for encontrado ou pontos < 0
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void adicionarXP(UUID jogadorId, Integer pontos) {
        log.info("Adicionando {} XP ao jogador id={}", pontos, jogadorId);

        Jogador jogador = jogadorRepository.findById(jogadorId)
                .orElseThrow(() -> {
                    log.warn("Jogador não encontrado para adição de XP: id={}", jogadorId);
                    return new IllegalArgumentException("Jogador não encontrado: " + jogadorId);
                });

        int xpAntes = jogador.getXpTotal();
        int nivelAntes = jogador.getNivelGlobal();

        jogador.adicionarXP(pontos);
        jogadorRepository.save(jogador);

        log.info("XP adicionado: jogadorId={}, xpAntes={}, xpDepois={}, nivelAntes={}, nivelDepois={}",
                jogadorId, xpAntes, jogador.getXpTotal(), nivelAntes, jogador.getNivelGlobal());
    }

    /**
     * Converte a entidade Jogador para JogadorDTO.
     *
     * @param jogador Entidade JPA
     * @return DTO correspondente
     */
    private JogadorDTO toDTO(Jogador jogador) {
        return new JogadorDTO(
                jogador.getId(),
                jogador.getEmail(),
                jogador.getNome(),
                jogador.getNomePersonagem(),
                jogador.getXpTotal(),
                jogador.getNivelGlobal(),
                jogador.getAtivo(),
                jogador.getDataCriacao()
        );
    }
}
