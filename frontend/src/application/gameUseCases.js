export function createGameUseCases(service) {
  return {
    createGame: (player) => service.createGame(player),
    joinGame: (id, player) => service.joinGame(id, player),
    getGame: (id) => service.getGame(id),
    makeMove: (id, player, from, to) => service.makeMove(id, player, from, to),
    listOpenGames: () => service.listOpenGames(),
  };
}