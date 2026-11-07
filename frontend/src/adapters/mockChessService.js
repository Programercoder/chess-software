export function createMockChessService() {
  const games = new Map();
  let counter = 0;

  function fail(message) {
    throw new Error(message);
  }

  function find(id) {
    return games.get(id) || fail("Game not found: " + id);
  }

  return {
    async createGame(player) {
      const id = "MOCK" + String(++counter).padStart(2, "0");
      const game = {
        id,
        whitePlayer: player,
        blackPlayer: null,
        status: "WAITING_FOR_PLAYER",
        turn: "WHITE",
        fen: "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1",
        moves: [],
      };
      games.set(id, game);
      return structuredClone(game);
    },

    async joinGame(id, player) {
      const game = find(id);
      if (game.status !== "WAITING_FOR_PLAYER") fail("The game already has two players");
      if (player === game.whitePlayer) fail("You cannot play against yourself");
      game.blackPlayer = player;
      game.status = "IN_PROGRESS";
      return structuredClone(game);
    },

    async getGame(id) {
      return structuredClone(find(id));
    },

    async makeMove(id, player, from, to) {
      const game = find(id);
      const turnPlayer = game.turn === "WHITE" ? game.whitePlayer : game.blackPlayer;
      if (player !== turnPlayer) fail("It is not your turn");
      game.moves.push({ from, to });
      game.turn = game.turn === "WHITE" ? "BLACK" : "WHITE";
      return structuredClone(game);
    },

    async listOpenGames() {
      return [...games.values()]
        .filter((g) => g.status === "WAITING_FOR_PLAYER")
        .map((g) => structuredClone(g));
    },
  };
}