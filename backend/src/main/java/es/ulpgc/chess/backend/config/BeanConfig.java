package es.ulpgc.chess.backend.config;

import es.ulpgc.chess.backend.application.port.out.GameRepository;
import es.ulpgc.chess.backend.application.service.GameService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import es.ulpgc.chess.backend.adapter.out.persistence.JpaGameRepositoryAdapter;
import es.ulpgc.chess.backend.adapter.out.persistence.SpringDataGameRepository;

@Configuration
public class BeanConfig {

    @Bean
    public GameRepository gameRepository(SpringDataGameRepository jpa) {
        return new JpaGameRepositoryAdapter(jpa);
    }

    @Bean
    public GameService gameService(GameRepository repository) {
        return new GameService(repository);
    }
}
