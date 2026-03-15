package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.dto.SessaoDTO;
import com.animahub.javabeans.ia.model.Jogador;
import com.animahub.javabeans.ia.model.Sessao;
import com.animahub.javabeans.ia.repository.JogadorRepository;
import com.animahub.javabeans.ia.repository.SessaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Serviço de domínio responsável pelo ciclo de vida do contexto narrativo (Sessao).
 *
 * Princípio de Responsabilidade Única: Gerencia exclusivamente operações relacionadas
 * ao ciclo de vida das sessões de jogo (início, encerramento e auditoria temporal).
 *
 * Conformidade:
 * - Matthew et al. (2024): Contexto narrativo do LLM como tutor personalizado
 * - Gupta et al. (2025): Consistência transacional em arquiteturas com IA
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SessaoService {

    private final SessaoRepository sessaoRepository;
    private final JogadorRepository jogadorRepository;

    /**
     * Inicia uma nova sessão de jogo para o jogador especificado.
     *
     * Isolamento READ_COMMITTED: Garante que o estado do jogador lido seja confirmado,
     * evitando iniciar sessões com dados sujos.
     *
     * Validações de precondição:
     * - Jogador deve existir e estar ativo
     * - Jogador não pode ter outra sessão ativa em andamento (invariante)
     *
     * @param jogadorId     UUID do jogador
     * @param cenarioInicial Cenário inicial da sessão
     * @return SessaoDTO com os dados da sessão criada
     * @throws IllegalArgumentException se o jogador não existir ou estiver inativo
     * @throws IllegalStateException    se já houver sessão ativa em andamento
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SessaoDTO iniciarSessao(UUID jogadorId, String cenarioInicial) {
        log.info("[AUDITORIA] Iniciando sessão para jogadorId={}, cenario={}", jogadorId, cenarioInicial);

        Jogador jogador = jogadorRepository.findById(jogadorId)
                .orElseThrow(() -> {
                    log.warn("Jogador não encontrado para iniciar sessão: id={}", jogadorId);
                    return new IllegalArgumentException("Jogador não encontrado: " + jogadorId);
                });

        if (!jogador.getAtivo()) {
            log.warn("Tentativa de iniciar sessão para jogador inativo: id={}", jogadorId);
            throw new IllegalArgumentException("Jogador inativo não pode iniciar sessão: " + jogadorId);
        }

        sessaoRepository.findByJogadorIdAndAtivaTrue(jogadorId).ifPresent(sessaoAtiva -> {
            log.warn("Violação de invariante: jogador já possui sessão ativa. jogadorId={}, sessaoAtivaId={}",
                    jogadorId, sessaoAtiva.getId());
            throw new IllegalStateException(
                    "Jogador já possui uma sessão ativa em andamento: " + sessaoAtiva.getId());
        });

        Sessao novaSessao = Sessao.builder()
                .jogador(jogador)
                .cenarioAtual(cenarioInicial)
                .xpSessao(0)
                .ativa(true)
                .build();

        Sessao salva = sessaoRepository.save(novaSessao);
        log.info("[AUDITORIA] Sessão iniciada: sessaoId={}, jogadorId={}, cenario={}",
                salva.getId(), jogadorId, cenarioInicial);

        return toDTO(salva);
    }

    /**
     * Encerra uma sessão de jogo ativa.
     *
     * Isolamento SERIALIZABLE: Garante consistência total da trilha de auditoria temporal,
     * evitando condições de corrida no encerramento concorrente de sessões.
     *
     * @param sessaoId UUID da sessão a encerrar
     * @throws IllegalArgumentException se a sessão não for encontrada
     * @throws IllegalStateException    se a sessão já estiver encerrada
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void encerrarSessao(UUID sessaoId) {
        log.info("[AUDITORIA] Encerrando sessão: sessaoId={}", sessaoId);

        Sessao sessao = sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> {
                    log.warn("Sessão não encontrada para encerramento: id={}", sessaoId);
                    return new IllegalArgumentException("Sessão não encontrada: " + sessaoId);
                });

        if (!sessao.getAtiva()) {
            log.warn("Tentativa de encerrar sessão já encerrada: id={}", sessaoId);
            throw new IllegalStateException("Sessão já está encerrada: " + sessaoId);
        }

        sessao.encerrar();
        sessaoRepository.save(sessao);

        Long duracaoMinutos = sessao.calcularDuracaoMinutos();
        Long duracaoSegundos = sessao.calcularDuracaoSegundos();

        log.info("[AUDITORIA] Sessão encerrada: sessaoId={}, jogadorId={}, xpSessao={}, duracaoMinutos={}, duracaoSegundos={}",
                sessaoId,
                sessao.getJogador().getId(),
                sessao.getXpSessao(),
                duracaoMinutos,
                duracaoSegundos);
    }

    /**
     * Converte a entidade Sessao para SessaoDTO.
     *
     * @param sessao Entidade JPA
     * @return DTO correspondente
     */
    private SessaoDTO toDTO(Sessao sessao) {
        return new SessaoDTO(
                sessao.getId(),
                sessao.getJogador().getId(),
                sessao.getCenarioAtual(),
                sessao.getXpSessao(),
                sessao.getAtiva(),
                sessao.getDataCriacao(),
                sessao.getDataEncerramento()
        );
    }
}
