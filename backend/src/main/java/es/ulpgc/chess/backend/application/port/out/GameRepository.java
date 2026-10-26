package es.ulpgc.chess.backend.application.port.out;

import es.ulpgc.chess.backend.domain.Game;

import java.util.List;
import java.util.Optional;

public interface GameRepository {
    void save(Game game);
    Optional<Game> findById(String id);
    List<Game> findAll();
}
