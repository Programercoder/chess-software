package es.ulpgc.chess.backend.adapter.out.persistence;

import es.ulpgc.chess.backend.application.port.out.GameRepository;
import es.ulpgc.chess.backend.domain.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JpaGameRepositoryAdapter implements GameRepository {

    private final SpringDataGameRepository jpa;

    public JpaGameRepositoryAdapter(SpringDataGameRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Game game) {
        GameEntity entity = jpa.findById(game.getId()).orElseGet(() -> {
            GameEntity created = new GameEntity(game.getId());
            created.setCreatedAt(Instant.now());
            return created;
        });
        entity.setWhitePlayer(game.getWhitePlayer());
        entity.setBlackPlayer(game.getBlackPlayer());
        entity.setStatus(game.getStatus().name());
        entity.setTurn(game.getTurn().name());
        entity.setFen(game.getBoard().toFen());
        entity.setMoves(serializeMoves(game.getMoves()));
        entity.setUpdatedAt(Instant.now());
        jpa.save(entity);
    }

    @Override
    public Optional<Game> findById(String id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Game> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    private Game toDomain(GameEntity e) {
        return new Game(e.getId(), e.getWhitePlayer(), e.getBlackPlayer(),
                Board.fromFen(e.getFen()),
                Color.valueOf(e.getTurn()),
                GameStatus.valueOf(e.getStatus()),
                parseMoves(e.getMoves()));
    }

    private String serializeMoves(List<Move> moves) {
        return String.join(",", moves.stream()
                .map(m -> m.from().toAlgebraic() + m.to().toAlgebraic())
                .toList());
    }

    private List<Move> parseMoves(String text) {
        List<Move> moves = new ArrayList<>();
        if (text == null || text.isBlank()) return moves;
        for (String item : text.split(",")) {
            moves.add(new Move(Position.fromAlgebraic(item.substring(0, 2)),
                    Position.fromAlgebraic(item.substring(2, 4))));
        }
        return moves;
    }
}
