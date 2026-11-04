package es.ulpgc.chess.backend.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "games")
public class GameEntity {

    @Id
    private String id;
    private String whitePlayer;
    private String blackPlayer;
    private String status;
    private String turn;
    private String fen;

    @Column(length = 4000)
    private String moves;

    private Instant createdAt;
    private Instant updatedAt;

    protected GameEntity() {}

    public GameEntity(String id) {
        this.id = id;
    }

    public String getId() { return id; }
    public String getWhitePlayer() { return whitePlayer; }
    public void setWhitePlayer(String whitePlayer) { this.whitePlayer = whitePlayer; }
    public String getBlackPlayer() { return blackPlayer; }
    public void setBlackPlayer(String blackPlayer) { this.blackPlayer = blackPlayer; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTurn() { return turn; }
    public void setTurn(String turn) { this.turn = turn; }
    public String getFen() { return fen; }
    public void setFen(String fen) { this.fen = fen; }
    public String getMoves() { return moves; }
    public void setMoves(String moves) { this.moves = moves; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
