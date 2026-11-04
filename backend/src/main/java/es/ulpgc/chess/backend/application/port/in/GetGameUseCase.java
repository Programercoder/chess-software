package es.ulpgc.chess.backend.application.port.in;

import es.ulpgc.chess.backend.domain.Game;
import es.ulpgc.chess.backend.domain.GameStatus;

import java.util.List;

public interface GetGameUseCase {
    Game get(String gameId);
    List<Game> listAll();
    List<Game> listByStatus(GameStatus status);
}
