package com.animahub.javabeans.ia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuração JPA para o simulador JavaBeans-IA.
 * Habilita repositórios Spring Data JPA e gerenciamento transacional.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.animahub.javabeans.ia.repository")
@EnableTransactionManagement
public class JpaConfig {
}
