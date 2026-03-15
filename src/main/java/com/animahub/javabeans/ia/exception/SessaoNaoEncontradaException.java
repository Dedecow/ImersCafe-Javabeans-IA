package com.animahub.javabeans.ia.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando uma sessão não é encontrada no banco de dados.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class SessaoNaoEncontradaException extends RuntimeException {

    public SessaoNaoEncontradaException(String sessionId) {
        super("Sessão não encontrada: " + sessionId);
    }
}
