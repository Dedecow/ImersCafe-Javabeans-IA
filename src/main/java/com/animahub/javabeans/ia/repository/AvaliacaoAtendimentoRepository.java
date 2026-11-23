package com.animahub.javabeans.ia.repository;

import com.animahub.javabeans.ia.model.AvaliacaoAtendimento;
import com.animahub.javabeans.ia.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoAtendimentoRepository extends JpaRepository<AvaliacaoAtendimento, Long> {
    long countBySessao(Sessao sessao);
}
