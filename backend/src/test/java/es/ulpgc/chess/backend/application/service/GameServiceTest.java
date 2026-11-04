package es.ulpgc.chess.backend.application.service;

import es.ulpgc.chess.backend.adapter.out.memory.InMemoryGameRepository;
import es.ulpgc.chess.backend.application.GameNotFoundException;
import es.ulpgc.chess.backend.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private GameService service;

    @BeforeEach
    void setUp() {
        service = new GameService(new InMemoryGameRepository());
    }

    private Move move(String from, String to) {
        return new Move(Position.fromAlgebraic(from), Position.fromAlgebraic(to));
    }

    @Test
    void createdGameWaitsForSecondPlayer() {
        Game game = service.create("alice");
        assertEquals(GameStatus.WAITING_FOR_PLAYER, game.getStatus());
    }

    @Test
    void secondPlayerJoinsAndMovesAreStored() {
        Game game = service.create("alice");
        service.join(game.getId(), "bob");
        service.move(game.getId(), "alice", move("e2", "e4"));
        service.move(game.getId(), "bob", move("e7", "e5"));

        assertEquals(2, service.get(game.getId()).getMoves().size());
    }

    @Test
    void unknownGameThrowsNotFound() {
        assertThrows(GameNotFoundException.class, () -> service.get("NOPE"));
    }

    @Test
    void invalidMoveIsRejected() {
        Game game = service.create("alice");
        service.join(game.getId(), "bob");
        assertThrows(InvalidMoveException.class,
                () -> service.move(game.getId(), "alice", move("e2", "e5")));
    }

    @Test
    void listByStatusReturnsOnlyOpenRooms() {
        Game open = service.create("alice");
        Game full = service.create("carol");
        service.join(full.getId(), "dave");

        var waiting = service.listByStatus(GameStatus.WAITING_FOR_PLAYER);

        assertEquals(1, waiting.size());
        assertEquals(open.getId(), waiting.get(0).getId());
    }
}
