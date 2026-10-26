package es.ulpgc.chess.backend.application.port.in;

import es.ulpgc.chess.backend.domain.Game;

import java.util.List;

public interface GetGameUseCase {
    Game get(String gameId);
    List<Game> listAll();
}
