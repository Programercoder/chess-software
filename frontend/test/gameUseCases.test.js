import test from "node:test";
import assert from "node:assert/strict";
import { createMockChessService } from "../src/adapters/mockChessService.js";
import { assertChessService } from "../src/ports/chessServicePort.js";
import { createGameUseCases } from "../src/application/gameUseCases.js";
import { fenAtStep } from "../src/application/replay.js";

function setup() {
  const service = assertChessService(createMockChessService());
  return createGameUseCases(service);
}

test("the mock fulfils the service port", () => {
  assert.doesNotThrow(() => assertChessService(createMockChessService()));
});

test("create and join a game without network", async () => {
  const useCases = setup();
  const game = await useCases.createGame("alice");
  assert.equal(game.status, "WAITING_FOR_PLAYER");
  const joined = await useCases.joinGame(game.id, "bob");
  assert.equal(joined.status, "IN_PROGRESS");
});

test("open games list only contains rooms waiting for a player", async () => {
  const useCases = setup();
  const open = await useCases.createGame("alice");
  const full = await useCases.createGame("carol");
  await useCases.joinGame(full.id, "dave");
  const list = await useCases.listOpenGames();
  assert.equal(list.length, 1);
  assert.equal(list[0].id, open.id);
});

test("a move out of turn is rejected", async () => {
  const useCases = setup();
  const game = await useCases.createGame("alice");
  await useCases.joinGame(game.id, "bob");
  await assert.rejects(() => useCases.makeMove(game.id, "bob", "e7", "e5"), /not your turn/);
});

test("replay rebuilds the position after the first move", () => {
  const fen = fenAtStep([{ from: "e2", to: "e4" }], 1);
  assert.equal(fen.split(" ")[0], "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR");
});

test("replay at step 0 is the initial position", () => {
  const fen = fenAtStep([{ from: "e2", to: "e4" }], 0);
  assert.equal(fen.split(" ")[0], "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
});