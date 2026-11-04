package es.ulpgc.chess.backend.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataGameRepository extends JpaRepository<GameEntity, String> {
}