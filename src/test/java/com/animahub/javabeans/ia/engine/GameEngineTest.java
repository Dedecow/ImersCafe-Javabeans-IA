package com.animahub.javabeans.ia.engine;

import com.animahub.javabeans.ia.dto.AvaliacaoDTO;
import com.animahub.javabeans.ia.service.AIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class GameEngineTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AIService aiService;
    private GameEngine gameEngine;

    @BeforeEach
    void configurar() {
        aiService = mock(AIService.class);
        gameEngine = spy(new GameEngine(
                new SessionEngine(),
                aiService,
                objectMapper
        ));
        doReturn(TipoCliente.CALMO).when(gameEngine).sortearTipoCliente();
    }

    @Test
    void respostaValidaGeraAvaliacaoNormalmente() throws JsonProcessingException {
        simularConteudo("""
                {
                  "xpGanho": 100,
                  "feedbackEducativo": "Atendimento claro e cordial.",
                  "personaQuebrada": false,
                  "cenarioConcluido": true
                }
                """);

        AvaliacaoDTO resultado = processarTurno();

        assertNotNull(resultado);
        assertEquals(105, resultado.getXpGanho());
        assertEquals(
                "Atendimento claro e cordial.",
                resultado.getFeedbackEducativo()
        );
        assertFalse(resultado.isPersonaQuebrada());
        assertTrue(resultado.isCenarioConcluido());
        assertSame(TipoCliente.CALMO, resultado.getTipoCliente());
    }

    @Test
    void jsonInternoInvalidoRetornaFallback() throws JsonProcessingException {
        simularConteudo("{");

        assertFallback(processarTurno());
    }

    @Test
    void campoObrigatorioAusenteRetornaFallback() throws JsonProcessingException {
        simularConteudo("""
                {
                  "feedbackEducativo": "Atendimento claro e cordial.",
                  "personaQuebrada": false,
                  "cenarioConcluido": true
                }
                """);

        assertFallback(processarTurno());
    }

    @Test
    void campoObrigatorioComTipoIncorretoRetornaFallback()
            throws JsonProcessingException {
        simularConteudo("""
                {
                  "xpGanho": "100",
                  "feedbackEducativo": "Atendimento claro e cordial.",
                  "personaQuebrada": false,
                  "cenarioConcluido": true
                }
                """);

        assertFallback(processarTurno());
    }

    private void simularConteudo(String content) throws JsonProcessingException {
        String resposta = objectMapper.writeValueAsString(Map.of(
                "choices", List.of(Map.of(
                        "message", Map.of("content", content)
                ))
        ));

        when(aiService.avaliarResposta("Olá!", "Ana")).thenReturn(resposta);
    }

    private AvaliacaoDTO processarTurno() {
        return gameEngine.processarTurno("sessao-teste", "Olá!", "Ana");
    }

    private void assertFallback(AvaliacaoDTO resultado) {
        assertNotNull(resultado);
        assertEquals(0, resultado.getXpGanho());
        assertEquals(
                "Erro de comunicação com a IA. Tente novamente.",
                resultado.getFeedbackEducativo()
        );
        assertFalse(resultado.isPersonaQuebrada());
        assertFalse(resultado.isCenarioConcluido());
        assertNull(resultado.getTipoCliente());
    }
}
