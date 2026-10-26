package es.ulpgc.chess.backend.application.port.in;

import es.ulpgc.chess.backend.domain.Game;

public interface JoinGameUseCase {
    Game join(String gameId, String player);
}
