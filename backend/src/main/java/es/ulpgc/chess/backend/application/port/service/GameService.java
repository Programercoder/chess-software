package es.ulpgc.chess.backend.application.service;

import es.ulpgc.chess.backend.application.GameNotFoundException;
import es.ulpgc.chess.backend.application.port.in.*;
import es.ulpgc.chess.backend.application.port.out.GameRepository;
import es.ulpgc.chess.backend.domain.Game;
import es.ulpgc.chess.backend.domain.Move;

import java.util.List;
import java.util.UUID;

public class GameService implements CreateGameUseCase, JoinGameUseCase,
        MakeMoveUseCase, GetGameUseCase {

    private final GameRepository repository;

    public GameService(GameRepository repository) {
        this.repository = repository;
    }

    @Override
    public Game create(String player) {
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Game game = new Game(code, player);
        repository.save(game);
        return game;
    }

    @Override
    public Game join(String gameId, String player) {
        Game game = find(gameId);
        game.join(player);
        repository.save(game);
        return game;
    }

    @Override
    public Game move(String gameId, String player, Move move) {
        Game game = find(gameId);
        game.play(player, move);
        repository.save(game);
        return game;
    }

    @Override
    public Game get(String gameId) {
        return find(gameId);
    }

    @Override
    public List<Game> listAll() {
        return repository.findAll();
    }

    private Game find(String gameId) {
        return repository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
    }
}
