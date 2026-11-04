export function createHttpChessService(baseUrl = "http://localhost:8080") {
  async function request(path, options = {}) {
    const response = await fetch(baseUrl + path, {
      headers: { "Content-Type": "application/json" },
      ...options,
    });
    const body = await response.json().catch(() => ({}));
    if (!response.ok) {
      throw new Error(body.message || "Request failed (" + response.status + ")");
    }
    return body;
  }

  const post = (path, data) =>
    request(path, { method: "POST", body: JSON.stringify(data) });

  return {
    createGame: (player) => post("/api/games", { player }),
    joinGame: (id, player) => post("/api/games/" + id + "/join", { player }),
    getGame: (id) => request("/api/games/" + id),
    makeMove: (id, player, from, to) =>
      post("/api/games/" + id + "/moves", { player, from, to }),
    listOpenGames: () => request("/api/games?status=WAITING_FOR_PLAYER"),
  };
}