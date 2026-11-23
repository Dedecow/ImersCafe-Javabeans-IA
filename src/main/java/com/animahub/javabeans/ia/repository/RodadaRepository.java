package com.animahub.javabeans.ia.repository;

import com.animahub.javabeans.ia.model.Rodada;
import com.animahub.javabeans.ia.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RodadaRepository extends JpaRepository<Rodada, Long> {
    long countBySessao(Sessao sessao);
}
