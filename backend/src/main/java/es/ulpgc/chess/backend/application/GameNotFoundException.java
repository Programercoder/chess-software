package es.ulpgc.chess.backend.application;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String id) {
        super("Game not found: " + id);
    }
}
