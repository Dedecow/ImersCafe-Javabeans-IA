package com.animahub.javabeans.ia.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Registra cada requisição do jogo no log: método, rota, status e duração.
 * Cada requisição recebe um ID curto (MDC "requisicao") que aparece em todas as linhas
 * de log geradas por ela, inclusive nos erros, para facilitar o rastreio.
 */
@Component
public class RequisicaoLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger("javabeans.requisicoes");
    private static final long LIMITE_LENTA_MS = 5000;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/actuator") || "OPTIONS".equals(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        MDC.put("requisicao", UUID.randomUUID().toString().substring(0, 8));
        long inicio = System.currentTimeMillis();
        try {
            chain.doFilter(request, response);
        } finally {
            long ms = System.currentTimeMillis() - inicio;
            int status = response.getStatus();
            if (status >= 500) {
                log.error("{} {} -> {} em {} ms", request.getMethod(), request.getRequestURI(), status, ms);
            } else if (ms >= LIMITE_LENTA_MS) {
                log.warn("{} {} -> {} em {} ms (lenta)", request.getMethod(), request.getRequestURI(), status, ms);
            } else {
                log.info("{} {} -> {} em {} ms", request.getMethod(), request.getRequestURI(), status, ms);
            }
            MDC.remove("requisicao");
        }
    }
}
