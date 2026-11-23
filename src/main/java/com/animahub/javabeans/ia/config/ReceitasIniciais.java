package com.animahub.javabeans.ia.config;

import com.animahub.javabeans.ia.model.ReceitaEntity;
import com.animahub.javabeans.ia.repository.ReceitaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.animahub.javabeans.ia.model.Ingrediente.*;

/**
 * Cadastra as receitas do jogo na primeira vez que a aplicação sobe com um banco vazio.
 * São as mesmas receitas que antes ficavam fixas num HashMap do GameService.
 */
@Component
public class ReceitasIniciais implements CommandLineRunner {

    private final ReceitaRepository receitaRepository;

    public ReceitasIniciais(ReceitaRepository receitaRepository) {
        this.receitaRepository = receitaRepository;
    }

    @Override
    public void run(String... args) {
        if (receitaRepository.count() > 0) {
            return;
        }
        receitaRepository.saveAll(List.of(
                new ReceitaEntity("Cappuccino", 10, 1, Set.of(CAFE, LEITE, ESPUMA)),
                new ReceitaEntity("Mocha", 12, 2, Set.of(CAFE, CHOCOLATE, LEITE)),
                new ReceitaEntity("Latte", 8, 3, Set.of(CAFE, LEITE, BAUNILHA)),
                new ReceitaEntity("Café com Chantilly", 15, 4, Set.of(CAFE, CHANTILLY, CARAMELO))
        ));
    }
}
