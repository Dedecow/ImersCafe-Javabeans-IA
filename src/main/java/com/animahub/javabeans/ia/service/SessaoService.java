package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.dto.SessaoCreateRequest;
import com.animahub.javabeans.ia.dto.SessaoDTO;
import com.animahub.javabeans.ia.exception.SessaoNaoEncontradaException;
import com.animahub.javabeans.ia.model.Sessao;
import com.animahub.javabeans.ia.repository.SessaoRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * Serviço responsável pela gestão do ciclo de vida das sessões de jogo.
 * Gera UUIDs únicos para rastreabilidade e persiste o estado inicial no H2.
 */
@Service
public class SessaoService {

    private final SessaoRepository sessaoRepository;

    public SessaoService(SessaoRepository sessaoRepository) {
        this.sessaoRepository = sessaoRepository;
    }

    /**
     * Inicializa uma nova sessão de usuário com UUID único.
     *
     * @param request contém nome do usuário e nome do personagem escolhido
     * @return SessaoDTO com sessionId, dados do jogador e estado inicial de progressão
     */
    public SessaoDTO criarSessao(SessaoCreateRequest request) {
        if (request.getNomeUsuario() == null || request.getNomeUsuario().isBlank()) {
            throw new IllegalArgumentException("nomeUsuario é obrigatório");
        }
        if (request.getNomePersonagem() == null || request.getNomePersonagem().isBlank()) {
            throw new IllegalArgumentException("nomePersonagem é obrigatório");
        }

        String sessionId = UUID.randomUUID().toString();
        Sessao novaSessao = new Sessao(sessionId, request.getNomeUsuario(), request.getNomePersonagem());
        sessaoRepository.save(novaSessao);

        return toDTO(novaSessao);
    }

    /**
     * Busca uma sessão existente pelo UUID.
     *
     * @param sessionId UUID da sessão
     * @return SessaoDTO com estado atual da sessão
     * @throws SessaoNaoEncontradaException se a sessão não existir
     */
    public SessaoDTO buscarSessao(String sessionId) {
        Sessao sessao = sessaoRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SessaoNaoEncontradaException(sessionId));
        return toDTO(sessao);
    }

    private SessaoDTO toDTO(Sessao sessao) {
        return new SessaoDTO(
                sessao.getSessionId(),
                sessao.getNomeUsuario(),
                sessao.getNomePersonagem(),
                sessao.getXpTotal(),
                sessao.getNivelAtual(),
                sessao.getDataInicio()
        );
    }
}
