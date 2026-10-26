package es.ulpgc.chess.backend.adapter.out.memory;

import es.ulpgc.chess.backend.application.port.out.GameRepository;
import es.ulpgc.chess.backend.domain.Game;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryGameRepository implements GameRepository {
    private final Map<String, Game> games = new ConcurrentHashMap<>();

    @Override
    public void save(Game game) {
        games.put(game.getId(), game);
    }

    @Override
    public Optional<Game> findById(String id) {
        return Optional.ofNullable(games.get(id));
    }

    @Override
    public List<Game> findAll() {
        return List.copyOf(games.values());
    }
}
