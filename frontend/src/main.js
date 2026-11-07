import { createHttpChessService } from "./adapters/httpChessService.js";
import { assertChessService } from "./ports/chessServicePort.js";
import { createGameUseCases } from "./application/gameUseCases.js";
import { createBoardView } from "./ui/boardView.js";
import { fenAtStep } from "./application/replay.js";

const service = assertChessService(createHttpChessService("http://localhost:8080"));
const useCases = createGameUseCases(service);

const $ = (id) => document.getElementById(id);
let player = null;
let gameId = null;
let selected = null;
let timer = null;
let replayMoves = null;
let replayStep = 0;
let lastTurn = null;
let currentGame = null;

const board = createBoardView($("board"), onSquareClick);

function showError(message) {
  $("error").textContent = message || "";
}

function playerName() {
  const name = $("playerName").value.trim();
  if (!name) throw new Error("Escribe tu nombre");
  return name;
}

function describe(game) {
  if (game.status === "WAITING_FOR_PLAYER") return "Esperando al segundo jugador...";
  if (game.status === "FINISHED") return "Partida terminada";
  const mine = (game.turn === "WHITE" ? game.whitePlayer : game.blackPlayer) === player;
  return mine ? "Tu turno" : "Turno del rival (" + game.turn + ")";
}

function renderGame(game) {
  currentGame = game;
  $("roomInfo").textContent =
    "Sala " + game.id + " | Blancas: " + game.whitePlayer +
    " | Negras: " + (game.blackPlayer || "-") + " | Tú: " + player;
  board.setFlipped(game.blackPlayer === player);
  board.render(game.fen);
  $("status").textContent = describe(game);
  if (game.turn !== lastTurn) {
    showError("");
    lastTurn = game.turn;
  }
}

async function enterGame(game) {
  gameId = game.id;
  $("lobby").classList.add("hidden");
  $("game").classList.remove("hidden");
  renderGame(game);
  clearInterval(timer);
  timer = setInterval(refreshGame, 1500);
}

async function refreshGame() {
  if (replayMoves) return;
  try {
    renderGame(await useCases.getGame(gameId));
  } catch (e) {
    showError(e.message);
  }
}

function isMyTurn(game) {
  if (game.status !== "IN_PROGRESS") return false;
  const turnPlayer = game.turn === "WHITE" ? game.whitePlayer : game.blackPlayer;
  return turnPlayer === player;
}

async function onSquareClick(square) {
  if (!gameId || replayMoves) return;
  if (currentGame && currentGame.status === "IN_PROGRESS" && !isMyTurn(currentGame)) {
    showError("No es tu turno");
    return;
  }
  if (!selected) {
    selected = square;
    board.setSelected(square);
    await refreshGame();
    return;
  }
  const from = selected;
  selected = null;
  board.setSelected(null);
  try {
    showError("");
    renderGame(await useCases.makeMove(gameId, player, from, square));
  } catch (e) {
    showError(e.message);
    await refreshGame();
  }
}

$("createBtn").addEventListener("click", async () => {
  try {
    showError("");
    player = playerName();
    await enterGame(await useCases.createGame(player));
  } catch (e) { showError(e.message); }
});

$("joinBtn").addEventListener("click", async () => {
  try {
    showError("");
    player = playerName();
    const code = $("codeInput").value.trim().toUpperCase();
    await enterGame(await useCases.joinGame(code, player));
  } catch (e) { showError(e.message); }
});

$("refreshBtn").addEventListener("click", async () => {
  try {
    showError("");
    const games = await useCases.listOpenGames();
    const box = $("openGames");
    box.innerHTML = games.length ? "" : "No hay salas abiertas";
    games.forEach((g) => {
      const b = document.createElement("button");
      b.textContent = g.id + " (" + g.whitePlayer + ")";
      b.addEventListener("click", () => { $("codeInput").value = g.id; });
      box.appendChild(b);
    });
  } catch (e) { showError(e.message); }
});

function showStep() {
  board.render(fenAtStep(replayMoves, replayStep));
  $("stepLabel").textContent = " Jugada " + replayStep + " / " + replayMoves.length + " ";
}

function setStep(n) {
  replayStep = Math.max(0, Math.min(replayMoves.length, n));
  showStep();
}

$("replayBtn").addEventListener("click", async () => {
  try {
    showError("");
    const game = await useCases.getGame(gameId);
    replayMoves = game.moves;
    $("replayControls").classList.remove("hidden");
    $("replayBtn").classList.add("hidden");
    setStep(replayMoves.length);
  } catch (e) { showError(e.message); }
});

$("firstBtn").addEventListener("click", () => setStep(0));
$("prevBtn").addEventListener("click", () => setStep(replayStep - 1));
$("nextBtn").addEventListener("click", () => setStep(replayStep + 1));
$("lastBtn").addEventListener("click", () => setStep(replayMoves.length));
$("exitReplayBtn").addEventListener("click", async () => {
  replayMoves = null;
  $("replayControls").classList.add("hidden");
  $("replayBtn").classList.remove("hidden");
  await refreshGame();
});

board.render("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1");