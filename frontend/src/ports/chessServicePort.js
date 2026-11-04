export const CHESS_SERVICE_METHODS =
  ["createGame", "joinGame", "getGame", "makeMove", "listOpenGames"];

export function assertChessService(service) {
  for (const method of CHESS_SERVICE_METHODS) {
    if (typeof service[method] !== "function") {
      throw new Error("Invalid chess service, missing: " + method);
    }
  }
  return service;
}