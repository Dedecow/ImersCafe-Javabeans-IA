package com.animahub.javabeans.ia.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Tratamento centralizado de exceções da API REST.
 * Garante respostas padronizadas e códigos HTTP corretos para todos os endpoints.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata sessões não encontradas retornando HTTP 404.
     */
    @ExceptionHandler(SessaoNaoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleSessaoNaoEncontrada(SessaoNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "status", 404,
                "erro", "Sessão não encontrada",
                "mensagem", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Trata cenários não encontrados retornando HTTP 404.
     */
    @ExceptionHandler(CenarioNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleCenarioNaoEncontrado(CenarioNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "status", 404,
                "erro", "Cenário não encontrado",
                "mensagem", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Trata argumentos inválidos retornando HTTP 400.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", 400,
                "erro", "Requisição inválida",
                "mensagem", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Trata exceções genéricas não mapeadas retornando HTTP 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", 500,
                "erro", "Erro interno do servidor",
                "mensagem", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
