package com.animahub.javabeans.ia.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando um cenário não é encontrado no banco de dados.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CenarioNaoEncontradoException extends RuntimeException {

    public CenarioNaoEncontradoException(String cenarioId) {
        super("Cenário não encontrado: " + cenarioId);
    }
}
