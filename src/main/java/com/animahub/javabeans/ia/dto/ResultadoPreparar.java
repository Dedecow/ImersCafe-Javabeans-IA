package com.animahub.javabeans.ia.dto;


import com.animahub.javabeans.ia.model.Receita;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ResultadoPreparar(
        boolean acertou,
        @JsonProperty("pontosNestaRodada") int pontosNestaRodada,
        int pontuacaoTotal,
        String feedbackIA,
        Receita proximoCliente
) {}
