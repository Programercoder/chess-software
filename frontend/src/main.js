import { createBoardView } from "./ui/boardView.js";

const board = createBoardView(document.getElementById("board"), (square) => {
  console.log("clicked", square);
});

board.render("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1");
document.getElementById("status").textContent = "Posición inicial (estática)";