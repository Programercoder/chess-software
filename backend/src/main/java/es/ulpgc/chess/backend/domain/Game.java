package es.ulpgc.chess.backend.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private final String id;
    private final String whitePlayer;
    private String blackPlayer;
    private final Board board;
    private Color turn;
    private GameStatus status;
    private final List<Move> moves = new ArrayList<>();
    private final MoveValidator validator = new MoveValidator();

    public Game(String id, String whitePlayer) {
        this.id = id;
        this.whitePlayer = whitePlayer;
        this.board = Board.initial();
        this.turn = Color.WHITE;
        this.status = GameStatus.WAITING_FOR_PLAYER;
    }

    // Used by the persistence adapter to rebuild a stored game
    public Game(String id, String whitePlayer, String blackPlayer,
                Board board, Color turn, GameStatus status, List<Move> moves) {
        this.id = id;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.board = board;
        this.turn = turn;
        this.status = status;
        this.moves.addAll(moves);
    }

    public void join(String player) {
        if (status != GameStatus.WAITING_FOR_PLAYER) {
            throw new IllegalStateException("The game already has two players");
        }
        if (player.equals(whitePlayer)) {
            throw new IllegalStateException("You cannot play against yourself");
        }
        this.blackPlayer = player;
        this.status = GameStatus.IN_PROGRESS;
    }

    public void play(String player, Move move) {
        if (status != GameStatus.IN_PROGRESS) {
            throw new InvalidMoveException("The game is not in progress");
        }
        Color playerColor = colorOf(player);
        if (playerColor != turn) {
            throw new InvalidMoveException("It is not your turn");
        }
        validator.validate(board, move, turn);

        Piece captured = board.pieceAt(move.to());
        board.movePiece(move.from(), move.to());
        moves.add(move);

        if (captured != null && captured.type() == PieceType.KING) {
            status = GameStatus.FINISHED;
        } else {
            turn = turn.opposite();
        }
    }

    private Color colorOf(String player) {
        if (player.equals(whitePlayer)) return Color.WHITE;
        if (player.equals(blackPlayer)) return Color.BLACK;
        throw new InvalidMoveException("The player does not belong to this game");
    }

    // Castling, en passant and move counters are not implemented, so they are fixed
    public String toFen() {
        return board.toFen() + " " + (turn == Color.WHITE ? "w" : "b") + " - - 0 1";
    }

    public String getId() { return id; }
    public String getWhitePlayer() { return whitePlayer; }
    public String getBlackPlayer() { return blackPlayer; }
    public Board getBoard() { return board; }
    public Color getTurn() { return turn; }
    public GameStatus getStatus() { return status; }
    public List<Move> getMoves() { return Collections.unmodifiableList(moves); }
}
