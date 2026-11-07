const START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1";

function fenToGrid(fen) {
  return fen.split(" ")[0].split("/").map((row) => {
    const cells = [];
    for (const c of row) {
      if (/\d/.test(c)) for (let i = 0; i < Number(c); i++) cells.push(null);
      else cells.push(c);
    }
    return cells;
  });
}

function gridToFen(grid) {
  return grid.map((row) => {
    let out = "";
    let empty = 0;
    for (const cell of row) {
      if (cell === null) empty++;
      else {
        if (empty) { out += empty; empty = 0; }
        out += cell;
      }
    }
    return out + (empty ? empty : "");
  }).join("/");
}

function square(name) {
  return { col: name.charCodeAt(0) - 97, row: 8 - Number(name[1]) };
}

// Returns the FEN after applying the first `step` moves from the initial position.
export function fenAtStep(moves, step) {
  const grid = fenToGrid(START_FEN);
  for (const m of moves.slice(0, step)) {
    const from = square(m.from);
    const to = square(m.to);
    grid[to.row][to.col] = grid[from.row][from.col];
    grid[from.row][from.col] = null;
  }
  return gridToFen(grid) + " w - - 0 1";
}