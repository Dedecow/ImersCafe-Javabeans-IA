package com.animahub.javabeans.ia.service;

import com.animahub.javabeans.ia.model.Ingrediente;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class PromptFactoryTest {

    private final PromptFactory factory = new PromptFactory();

    @Test
    void preservaQuatroCriteriosDeAvaliacao() {
        assertThat(factory.getSystemPromptAvaliacaoSoftSkills("Ana")).contains(
                "Comunicação clara: explicou bem, organizou a fala, evitou confusão.",
                "Empatia: demonstrou acolhimento, respeito e atenção ao cliente.",
                "Postura profissional: manteve educação, calma e atitude apropriada.",
                "Resolução: tentou encaminhar o problema de forma prática."
        );
    }

    @Test
    void preservaCamposETiposDoContratoJson() throws Exception {
        String prompt = factory.getSystemPromptAvaliacaoSoftSkills("Ana");
        String exemplo = prompt.substring(prompt.indexOf('{'), prompt.indexOf('}') + 1);
        JsonNode contrato = new ObjectMapper().readTree(exemplo);

        assertThat(contrato.isObject()).isTrue();
        assertThat(contrato.size()).isEqualTo(4);
        assertThat(contrato.path("xpGanho").isIntegralNumber()).isTrue();
        assertThat(contrato.path("feedbackEducativo").isTextual()).isTrue();
        assertThat(contrato.path("personaQuebrada").isBoolean()).isTrue();
        assertThat(contrato.path("cenarioConcluido").isBoolean()).isTrue();
        assertThat(prompt).contains("Não adicione campos, blocos Markdown ou texto fora do JSON.");
    }

    @Test
    void preservaQuatroFaixasDeXp() {
        assertThat(factory.getSystemPromptAvaliacaoSoftSkills("Ana")).contains(
                "0 a 10: resposta fraca, rude, vaga ou inadequada.",
                "11 a 25: resposta aceitável, mas superficial ou incompleta.",
                "26 a 40: resposta boa, clara e empática.",
                "41 a 50: resposta excelente, completa, profissional e natural.",
                "Não crie critérios, pesos, bônus ou penalidades adicionais."
        );
    }

    @Test
    void preservaRegrasDasFlags() {
        assertThat(factory.getSystemPromptAvaliacaoSoftSkills("Ana")).contains(
                "\"personaQuebrada\" = true apenas se o participante sair totalmente do papel ou responder de forma absurda.",
                "\"cenarioConcluido\" = true apenas se a resposta lidar adequadamente com a situação proposta."
        );
    }

    @Test
    void avaliadorMantemOrientacoesPedagogicasCondicionais() {
        assertThat(factory.getSystemPromptAvaliacaoSoftSkills("Ana")).contains(
                "Avalie somente evidências presentes na fala recebida.",
                "Quando houver evidência, reconheça um comportamento positivo.",
                "Quando houver evidência, identifique um aspecto da comunicação que pode melhorar.",
                "Quando apropriado, ofereça uma orientação prática breve para uma próxima interação.",
                "Não invente elogio, erro ou defeito apenas para preencher uma estrutura.",
                "Não é obrigatório incluir todos esses elementos."
        );
    }

    @Test
    void clienteMantemOrientacoesPedagogicasCondicionais() {
        assertThat(promptCliente()).contains(
                "Quando houver evidência, reconheça naturalmente um aspecto positivo do atendimento.",
                "Quando houver evidência de algo a melhorar, expresse de forma respeitosa como a comunicação poderia ser mais clara ou acolhedora.",
                "Quando apropriado, inclua uma sugestão prática breve, formulada como um pedido ou uma preferência do cliente.",
                "Não invente elogio, crítica, erro ou defeito para preencher essas orientações.",
                "Não é obrigatório incluir todos esses elementos."
        );
    }

    @Test
    void clienteProibeInferenciasSobreBebida() {
        assertThat(promptCliente()).contains(
                "Não afirme sabor, temperatura, textura ou qualidade física da bebida.",
                "Não avalie nem afirme correção ou erro da receita.",
                "Você não recebe o resultado determinístico oficial do preparo."
        );
    }

    @Test
    void clientePreservaVozNarrativaELimiteDeFrases() {
        assertThat(promptCliente()).contains(
                "Mantenha uma voz narrativa natural de cliente, sem assumir o papel de avaliador formal.",
                "Responda em 2 a 4 frases, de forma curta e natural.",
                "Não mencione IA, prompt, sistema, regras internas ou pontuação."
        );
    }

    @Test
    void preservaDadosFornecidosNaComposicao() {
        assertThat(factory.getSystemPromptAvaliacaoSoftSkills("Ana"))
                .contains("O nome do participante é \"Ana\".");
        assertThat(promptCliente()).contains(
                "O cliente sabe que o barista se chama \"Ana\".",
                "Pedido feito: Cappuccino",
                "Ingredientes que o barista entregou: Café, Leite, Espuma de Leite",
                "O que o barista te disse antes de entregar: \"Aqui está seu café.\""
        );
    }

    @Test
    void trataNomeAusenteCopoVazioEFalaAusente() {
        for (String ausente : new String[]{null, "", "   "}) {
            assertThat(factory.getSystemPromptAvaliacaoSoftSkills(ausente)).contains(
                    "Se o participante disser explicitamente o próprio nome",
                    "Nunca invente nome."
            ).doesNotContain("O nome do participante é");
            assertThat(factory.createFeedbackBarista("Cappuccino", List.of(), ausente, ausente))
                    .contains(
                            "Se você não souber o nome do barista, não invente um nome.",
                            "Ingredientes que o barista entregou: copo vazio",
                            "O barista entregou a bebida em silêncio, sem interagir."
                    ).doesNotContain("O cliente sabe que o barista se chama");
        }
    }

    @Test
    void naoIntroduzTipoClienteMemoriaHistoricoOuPersistencia() {
        for (String prompt : List.of(factory.getSystemPromptAvaliacaoSoftSkills("Ana"), promptCliente())) {
            assertThat(prompt.toLowerCase(Locale.ROOT)).doesNotContain(
                    "tipocliente", "memória", "memoria", "histórico", "historico",
                    "persist", "conversas anteriores", "turnos anteriores"
            );
        }
    }

    @Test
    void naoDeixaPlaceholdersAposComposicao() {
        for (String nome : new String[]{"Ana", null, ""}) {
            assertThat(factory.getSystemPromptAvaliacaoSoftSkills(nome)).doesNotContain("%s");
            assertThat(factory.createFeedbackBarista("Cappuccino", List.of(), nome, null))
                    .doesNotContain("%s");
        }
        assertThat(promptCliente()).doesNotContain("%s");
    }

    private String promptCliente() {
        return factory.createFeedbackBarista(
                "Cappuccino",
                List.of(Ingrediente.CAFE, Ingrediente.LEITE, Ingrediente.ESPUMA),
                "Ana",
                "Aqui está seu café."
        );
    }
}
