const SYMBOLS = {
  K: "♔", Q: "♕", R: "♖", B: "♗", N: "♘", P: "♙",
  k: "♚", q: "♛", r: "♜", b: "♝", n: "♞", p: "♟",
};

export function parseFen(fen) {
  const placement = fen.split(" ")[0];
  return placement.split("/").map((row) => {
    const cells = [];
    for (const c of row) {
      if (/\d/.test(c)) for (let i = 0; i < Number(c); i++) cells.push(null);
      else cells.push(c);
    }
    return cells;
  });
}

export function createBoardView(container, onSquareClick) {
  let selected = null;
  let flipped = false;

  function render(fen) {
    container.innerHTML = "";
    const rows = parseFen(fen);
    for (let i = 0; i < 8; i++) {
      for (let j = 0; j < 8; j++) {
        const r = flipped ? 7 - i : i;
        const c = flipped ? 7 - j : j;
        const piece = rows[r][c];
        const name = "abcdefgh"[c] + (8 - r);
        const div = document.createElement("div");
        div.className = "square " + ((r + c) % 2 === 0 ? "light" : "dark")
          + (name === selected ? " selected" : "");
        div.textContent = piece ? SYMBOLS[piece] : "";
        div.addEventListener("click", () => onSquareClick(name));
        container.appendChild(div);
      }
    }
  }

  return {
    render,
    setSelected(name) { selected = name; },
    setFlipped(value) { flipped = value; },
  };
}