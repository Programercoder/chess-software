package es.ulpgc.chess.backend.adapter.in.web;

import es.ulpgc.chess.backend.adapter.in.web.dto.*;
import es.ulpgc.chess.backend.application.port.in.*;
import es.ulpgc.chess.backend.domain.Move;
import es.ulpgc.chess.backend.domain.Position;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final CreateGameUseCase createGame;
    private final JoinGameUseCase joinGame;
    private final MakeMoveUseCase makeMove;
    private final GetGameUseCase getGame;

    public GameController(CreateGameUseCase createGame, JoinGameUseCase joinGame,
                          MakeMoveUseCase makeMove, GetGameUseCase getGame) {
        this.createGame = createGame;
        this.joinGame = joinGame;
        this.makeMove = makeMove;
        this.getGame = getGame;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponse create(@RequestBody CreateGameRequest request) {
        requireText(request.player(), "player");
        return GameResponse.from(createGame.create(request.player()));
    }

    @PostMapping("/{id}/join")
    public GameResponse join(@PathVariable String id, @RequestBody JoinGameRequest request) {
        requireText(request.player(), "player");
        return GameResponse.from(joinGame.join(id, request.player()));
    }

    @PostMapping("/{id}/moves")
    public GameResponse move(@PathVariable String id, @RequestBody MoveRequest request) {
        requireText(request.player(), "player");
        requireText(request.from(), "from");
        requireText(request.to(), "to");
        Move move = new Move(Position.fromAlgebraic(request.from()),
                Position.fromAlgebraic(request.to()));
        return GameResponse.from(makeMove.move(id, request.player(), move));
    }

    @GetMapping("/{id}")
    public GameResponse get(@PathVariable String id) {
        return GameResponse.from(getGame.get(id));
    }

    @GetMapping("/{id}/moves")
    public List<MoveResponse> moves(@PathVariable String id) {
        return GameResponse.from(getGame.get(id)).moves();
    }

    @GetMapping
    public List<GameResponse> list() {
        return getGame.listAll().stream().map(GameResponse::from).toList();
    }

    private void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing field: " + field);
        }
    }
}