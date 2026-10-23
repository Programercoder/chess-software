package es.ulpgc.chess.backend.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game("g1", "alice");
        game.join("bob");
    }

    private Move move(String from, String to) {
        return new Move(Position.fromAlgebraic(from), Position.fromAlgebraic(to));
    }

    @Test
    void gameStartsInProgressAfterSecondPlayerJoins() {
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        assertEquals(Color.WHITE, game.getTurn());
    }

    @Test
    void whitePawnCanAdvanceTwoSquaresFromStart() {
        game.play("alice", move("e2", "e4"));

        assertEquals(1, game.getMoves().size());
        assertEquals(Color.BLACK, game.getTurn());
        assertNull(game.getBoard().pieceAt(Position.fromAlgebraic("e2")));
        assertNotNull(game.getBoard().pieceAt(Position.fromAlgebraic("e4")));
    }

    @Test
    void knightCanJumpOverPieces() {
        game.play("alice", move("g1", "f3"));

        assertEquals(PieceType.KNIGHT,
                game.getBoard().pieceAt(Position.fromAlgebraic("f3")).type());
    }

    @Test
    void movingOutOfTurnIsRejected() {
        assertThrows(InvalidMoveException.class,
                () -> game.play("bob", move("e7", "e5")));
    }

    @Test
    void capturingOwnPieceIsRejected() {
        assertThrows(InvalidMoveException.class,
                () -> game.play("alice", move("a1", "a2")));
    }

    @Test
    void rookCannotJumpOverPieces() {
        assertThrows(InvalidMoveException.class,
                () -> game.play("alice", move("a1", "a3")));
    }

    @Test
    void movingEmptySquareIsRejected() {
        assertThrows(InvalidMoveException.class,
                () -> game.play("alice", move("e4", "e5")));
    }

    @Test
    void playerOutsideTheGameIsRejected() {
        assertThrows(InvalidMoveException.class,
                () -> game.play("mallory", move("e2", "e4")));
    }

    @Test
    void initialPositionProducesStandardFen() {
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1",
                new Game("g1", "alice").toFen());
    }

    @Test
    void fenReflectsMoveAndTurn() {
        game.play("alice", move("e2", "e4"));
        assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b - - 0 1", game.toFen());
    }

    @Test
    void boardCanBeRebuiltFromFen() {
        String fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR";
        assertEquals(fen, Board.fromFen(fen).toFen());
    }

    @Test
    void playerCannotJoinOwnGame() {
        Game own = new Game("g2", "alice");
        assertThrows(IllegalStateException.class, () -> own.join("alice"));
    }
}
