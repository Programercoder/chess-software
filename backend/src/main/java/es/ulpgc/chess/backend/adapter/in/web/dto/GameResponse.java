package es.ulpgc.chess.backend.adapter.in.web.dto;

import es.ulpgc.chess.backend.domain.Game;

import java.util.List;

public record GameResponse(String id, String whitePlayer, String blackPlayer,
                           String status, String turn, String fen,
                           List<MoveResponse> moves) {

    public static GameResponse from(Game game) {
        List<MoveResponse> moves = game.getMoves().stream()
                .map(m -> new MoveResponse(m.from().toAlgebraic(), m.to().toAlgebraic()))
                .toList();
        return new GameResponse(game.getId(), game.getWhitePlayer(), game.getBlackPlayer(),
                game.getStatus().name(), game.getTurn().name(), game.toFen(), moves);
    }
}
