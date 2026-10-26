package es.ulpgc.chess.backend.config;

import es.ulpgc.chess.backend.adapter.out.memory.InMemoryGameRepository;
import es.ulpgc.chess.backend.application.port.out.GameRepository;
import es.ulpgc.chess.backend.application.service.GameService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public GameRepository gameRepository() {
        return new InMemoryGameRepository();
    }

    @Bean
    public GameService gameService(GameRepository repository) {
        return new GameService(repository);
    }
}
