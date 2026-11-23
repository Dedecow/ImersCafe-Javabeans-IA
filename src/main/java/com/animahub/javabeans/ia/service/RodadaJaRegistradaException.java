package com.animahub.javabeans.ia.service;

/**
 * A rodada daquele cliente já foi registrada por outra requisição simultânea
 * (duplo clique, aba duplicada). Vira HTTP 409 no TratadorDeErros.
 */
public class RodadaJaRegistradaException extends RuntimeException {
    public RodadaJaRegistradaException() {
        super("Esta rodada já foi registrada. Atualize a tela para ver o próximo cliente.");
    }
}
