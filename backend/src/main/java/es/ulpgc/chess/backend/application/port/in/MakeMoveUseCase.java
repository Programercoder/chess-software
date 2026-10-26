package es.ulpgc.chess.backend.application.port.in;

import es.ulpgc.chess.backend.domain.Game;
import es.ulpgc.chess.backend.domain.Move;

public interface MakeMoveUseCase {
    Game move(String gameId, String player, Move move);
}
