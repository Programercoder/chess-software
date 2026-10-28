# chess-software
# chess-software

Sistema de salas de ajedrez formado por un frontend y un backend. Un jugador
crea una partida, comparte su código y un segundo jugador se une. El backend
valida los movimientos y las partidas se pueden consultar después.

## Requisitos

- JDK 21
- Maven (o el Maven Wrapper incluido en `backend/`)

## Ejecutar el backend

```bash
cd backend
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

La API escucha en `http://localhost:8080` (configurado en
`backend/src/main/resources/application.properties`).

Ejecutar los tests:

```bash
cd backend
./mvnw test
```

## Ejecutar el frontend

TODO

## Arquitectura de sistemas

TODO (frontend, backend, base de datos y contrato de comunicación)

## Arquitectura del backend (hexagonal)

El dominio y la capa de aplicación no dependen de Spring. Solo los
adaptadores y `config` conocen el framework.

| Capa | Paquete | Clases |
|---|---|---|
| Dominio | `domain` | `Game`, `Board`, `Move`, `Piece`, `PieceType`, `Color`, `Position`, `GameStatus`, `MoveValidator`, `InvalidMoveException` |
| Puertos de entrada | `application.port.in` | `CreateGameUseCase`, `JoinGameUseCase`, `MakeMoveUseCase`, `GetGameUseCase` |
| Puertos de salida | `application.port.out` | `GameRepository` |
| Casos de uso | `application.service` | `GameService` |
| Adaptador de entrada | `adapter.in.web` | `GameController`, `ApiExceptionHandler`, DTOs en `dto` |
| Adaptador de salida | `adapter.out.memory` | `InMemoryGameRepository` |
| Configuración | `config` | `BeanConfig`, `WebConfig` (CORS) |

`GameService` recibe un `GameRepository` por constructor, así que el
almacenamiento se puede sustituir sin tocar los casos de uso.

## Contrato de la API

Todos los cuerpos son JSON. El id de la partida es un código de 6 caracteres
que funciona como código de sala.

| Método | Ruta | Cuerpo | Descripción |
|---|---|---|---|
| POST | `/api/games` | `{"player":"alice"}` | Crea una partida (201) |
| POST | `/api/games/{id}/join` | `{"player":"bob"}` | Unirse a una partida |
| POST | `/api/games/{id}/moves` | `{"player":"alice","from":"e2","to":"e4"}` | Realizar un movimiento |
| GET | `/api/games/{id}` | | Estado actual |
| GET | `/api/games/{id}/moves` | | Histórico de movimientos |
| GET | `/api/games` | | Listar partidas |

Respuesta de una partida:

```json
{
  "id": "C63844",
  "whitePlayer": "alice",
  "blackPlayer": "bob",
  "status": "IN_PROGRESS",
  "turn": "WHITE",
  "fen": "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1",
  "moves": []
}
```

Errores:

| HTTP | Código de error | Causa |
|---|---|---|
| 400 | `BAD_REQUEST` | Faltan campos o el JSON es incorrecto |
| 404 | `GAME_NOT_FOUND` | La partida no existe |
| 409 | `CONFLICT` | Movimiento ilegal, turno incorrecto o estado de partida inválido |

## Modelo de datos

TODO (al añadir la persistencia)

## Cómo se conectan los usuarios

El creador recibe un id de partida de 6 caracteres. El segundo jugador lo
envía a `POST /api/games/{id}/join`. La partida pasa de
`WAITING_FOR_PLAYER` a `IN_PROGRESS`.

## Reglas de ajedrez

Implementado:
- Turnos y pertenencia de las piezas
- Movimiento de todas las piezas (peón, torre, caballo, alfil, dama, rey)
- Caminos bloqueados para torre, alfil, dama y doble paso del peón
- Capturas; capturar una pieza propia se rechaza
- Peón: un paso, dos pasos desde la fila inicial y captura en diagonal

No implementado: enroque, captura al paso, promoción, jaque y jaque mate.
La partida termina cuando se captura al rey.

El estado del tablero se expone en notación FEN. Solo la colocación de las
piezas y el turno son significativos; el resto de campos son fijos
(`- - 0 1`).

## Idea propia

TODO

## Verificación

Automatizada: 12 tests de dominio (`GameTest`) y 4 tests de casos de uso
(`GameServiceTest`, que usa el repositorio en memoria).

Comprobaciones manuales con PowerShell, con el backend arrancado:

```powershell
$g = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/games -ContentType "application/json" -Body '{"player":"alice"}'
$id = $g.id
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/games/$id/join -ContentType "application/json" -Body '{"player":"bob"}'
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/games/$id/moves -ContentType "application/json" -Body '{"player":"alice","from":"e2","to":"e4"}'
Invoke-RestMethod http://localhost:8080/api/games/$id/moves | ConvertTo-Json
```

Casos de error:

| Petición | Resultado |
|---|---|
| `GET /api/games/NOPE` | 404 `{"error":"GAME_NOT_FOUND","message":"Game not found: NOPE"}` |
| `POST /api/games` con `{}` | 400 `{"error":"BAD_REQUEST","message":"Missing field: player"}` |
| `POST /api/games/C63844/moves` e2 a e5 | 409 `{"error":"CONFLICT","message":"Illegal move for PAWN"}` |

## Capturas

TODO

## Límites conocidos

- Las partidas se guardan en memoria y se pierden al reiniciar (hasta añadir la persistencia)
- Reglas de ajedrez reducidas (ver arriba)
- Sin autenticación: el jugador se identifica solo por el nombre enviado
- Los movimientos concurrentes sobre la misma partida no están sincronizados