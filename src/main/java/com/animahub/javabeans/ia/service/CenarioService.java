package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.dto.CenarioDTO;
import com.animahub.javabeans.ia.exception.CenarioNaoEncontradoException;
import com.animahub.javabeans.ia.exception.SessaoNaoEncontradaException;
import com.animahub.javabeans.ia.model.Cenario;
import com.animahub.javabeans.ia.model.enums.NivelDificuldade;
import com.animahub.javabeans.ia.repository.CenarioRepository;
import com.animahub.javabeans.ia.repository.SessaoRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

/**
 * Serviço responsável pelo fornecimento de metadados dos cenários pedagógicos.
 * Inicializa cenários padrão no banco H2 e os retorna com base no contexto da sessão.
 */
@Service
public class CenarioService {

    private final CenarioRepository cenarioRepository;
    private final SessaoRepository sessaoRepository;

    public CenarioService(CenarioRepository cenarioRepository, SessaoRepository sessaoRepository) {
        this.cenarioRepository = cenarioRepository;
        this.sessaoRepository = sessaoRepository;
    }

    /**
     * Inicializa cenários pedagógicos padrão no banco de dados caso não existam.
     * Executado automaticamente na inicialização da aplicação.
     */
    @PostConstruct
    public void inicializarCenarios() {
        if (cenarioRepository.count() == 0) {
            Cenario cenario1 = new Cenario(
                    "CEN-001",
                    "O Cliente Apressado",
                    "Um cliente entra na cafeteria pedindo um espresso para viagem, mas o sistema de pagamentos acabou de cair.",
                    "Você é um barista experiente. O cliente está visivelmente estressado e com pressa. O terminal de pagamento apresenta falha e há fila se formando atrás dele.",
                    List.of("Comunicação Clara sob Pressão", "Resolução de Problemas", "Empatia com o Cliente"),
                    NivelDificuldade.FACIL,
                    300, // 5 minutos em segundos
                    100
            );

            Cenario cenario2 = new Cenario(
                    "CEN-002",
                    "O Pedido Especial",
                    "Um cliente solicita uma bebida personalizada com múltiplas restrições alimentares e o fornecedor atrasou a entrega.",
                    "A cafeteria está movimentada. O cliente tem intolerância à lactose, é vegano e alérgico a nozes. Vários ingredientes estão em falta hoje.",
                    List.of("Gestão de Expectativas", "Criatividade na Solução", "Comunicação Assertiva"),
                    NivelDificuldade.MEDIO,
                    420, // 7 minutos em segundos
                    150
            );

            Cenario cenario3 = new Cenario(
                    "CEN-003",
                    "Crise no Horário de Pico",
                    "Três clientes reclamam simultaneamente enquanto a máquina de espresso apresenta falhas intermitentes.",
                    "São 8h da manhã, horário de pico. A máquina de espresso principal está com defeito. Três clientes estão insatisfeitos com atrasos e dois novos chegam à fila.",
                    List.of("Liderança sob Pressão", "Priorização de Tarefas", "Comunicação Multidirecional", "Inteligência Emocional"),
                    NivelDificuldade.DIFICIL,
                    600, // 10 minutos em segundos
                    250
            );

            cenarioRepository.saveAll(List.of(cenario1, cenario2, cenario3));
        }
    }

    /**
     * Retorna metadados do cenário atual associado à sessão do jogador.
     *
     * @param sessionId UUID da sessão do jogador
     * @return CenarioDTO com todos os metadados do cenário atual
     * @throws SessaoNaoEncontradaException  se a sessão não existir
     * @throws CenarioNaoEncontradoException se nenhum cenário estiver disponível
     */
    public CenarioDTO getCenario(String sessionId) {
        sessaoRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new SessaoNaoEncontradaException(sessionId));

        // Por ora, retorna o primeiro cenário disponível (Fase 1 - placeholder)
        Cenario cenario = cenarioRepository.findById("CEN-001")
                .orElseThrow(() -> new CenarioNaoEncontradoException("CEN-001"));

        return toDTO(cenario);
    }

    /**
     * Busca um cenário específico por ID.
     *
     * @param cenarioId identificador do cenário
     * @return CenarioDTO com metadados do cenário
     * @throws CenarioNaoEncontradoException se o cenário não existir
     */
    public CenarioDTO getCenarioPorId(String cenarioId) {
        Cenario cenario = cenarioRepository.findById(cenarioId)
                .orElseThrow(() -> new CenarioNaoEncontradoException(cenarioId));
        return toDTO(cenario);
    }

    private CenarioDTO toDTO(Cenario cenario) {
        CenarioDTO dto = new CenarioDTO();
        dto.setCenarioId(cenario.getCenarioId());
        dto.setTitulo(cenario.getTitulo());
        dto.setDescricao(cenario.getDescricao());
        dto.setContextoNarrativo(cenario.getContextoNarrativo());
        dto.setObjetivosPedagogicos(cenario.getObjetivosPedagogicos());
        dto.setDificuldade(cenario.getDificuldade());
        dto.setTempoEstimado(cenario.getTempoEstimado());
        dto.setXpRecompensa(cenario.getXpRecompensa());
        return dto;
    }
}
