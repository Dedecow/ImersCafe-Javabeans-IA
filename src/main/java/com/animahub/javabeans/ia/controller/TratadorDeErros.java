package com.animahub.javabeans.ia.controller;

import com.animahub.javabeans.ia.service.RodadaJaRegistradaException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ponto central de tratamento de erros: registra no log todo erro inesperado (com a pilha e o
 * ID da requisição) e devolve ao frontend um JSON simples, sem detalhes internos.
 * Erros padrão do Spring (JSON malformado, método não suportado...) continuam sendo
 * tratados pela classe-mãe com os status corretos (400, 405...).
 */
@RestControllerAdvice
public class TratadorDeErros extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(TratadorDeErros.class);

    @ExceptionHandler(RodadaJaRegistradaException.class)
    public ResponseEntity<Map<String, Object>> rodadaJaRegistrada(RodadaJaRegistradaException e, HttpServletRequest req) {
        log.warn("Rodada duplicada recusada em {} {}", req.getMethod(), req.getRequestURI());
        return resposta(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> erroInesperado(Exception e, HttpServletRequest req) {
        log.error("Erro inesperado em {} {}", req.getMethod(), req.getRequestURI(), e);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor. Tente novamente.");
    }

    private ResponseEntity<Map<String, Object>> resposta(HttpStatus status, String mensagem) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("status", status.value());
        corpo.put("erro", mensagem);
        corpo.put("requisicao", MDC.get("requisicao"));
        return ResponseEntity.status(status).body(corpo);
    }
}
