# chess-software

Sistema de salas de ajedrez formado por un frontend y un backend. Un jugador
crea una partida, comparte su código y un segundo jugador se une. El backend
valida los movimientos y las partidas quedan registradas y se pueden
consultar después.

## Requisitos

- JDK 25
- Maven (o el Maven Wrapper incluido en `backend/`)
- Spring Boot 4.1.2, Spring Data JPA, base de datos H2 y JUnit 5
- Para el frontend: Node.js (`npx serve`) o Python (`python -m http.server`)

## Estructura del repositorio

    chess-software/
    ├── backend/        API REST (Spring Boot, arquitectura hexagonal)
    ├── frontend/       Cliente web (HTML + JavaScript con módulos ES)
    ├── screenshots/    Capturas usadas en este README
    └── README.md

## Ejecutar el backend

```bash
cd backend
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

La API escucha en `http://localhost:8080`.

Ejecutar los tests:

```bash
cd backend
./mvnw test
```

## Configuración

Fichero `backend/src/main/resources/application.properties`:

| Propiedad | Valor | Descripción |
|---|---|---|
| `server.port` | `8080` | Puerto de la API |
| `spring.datasource.url` | `jdbc:h2:file:./data/chess` | Base de datos H2 en fichero |
| `spring.jpa.hibernate.ddl-auto` | `update` | Crea y actualiza las tablas automáticamente |

La base de datos se guarda en `backend/data/chess.mv.db` (relativa al
directorio de ejecución) y no se sube al repositorio. Para empezar de cero,
para el backend y borra la carpeta `data/`.

## Ejecutar el frontend

```bash
cd frontend
npx serve -l 5173             # o: python -m http.server 5173
```

Abrir `http://localhost:5173`. El frontend llama al backend en
`http://localhost:8080` (CORS habilitado en `WebConfig`). Los módulos ES no
funcionan abriendo `index.html` con doble clic: hace falta un servidor.

## Arquitectura de sistemas

TODO (diagrama: frontend, backend, base de datos y contrato de comunicación)

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
| Adaptador de salida | `adapter.out.persistence` | `JpaGameRepositoryAdapter`, `GameEntity`, `SpringDataGameRepository` |
| Doble de prueba | `adapter.out.memory` | `InMemoryGameRepository` (usado en `GameServiceTest`) |
| Configuración | `config` | `BeanConfig`, `WebConfig` (CORS) |

`GameService` recibe un `GameRepository` por constructor. Para cambiar el
almacenamiento solo se modifica el bean `gameRepository` en `BeanConfig`;
dominio, casos de uso y controlador no cambian.

## Arquitectura del frontend (hexagonal ligera)

| Capa | Módulo | Responsabilidad |
|---|---|---|
| UI (adaptador de entrada) | `src/ui/boardView.js` | Pinta el tablero a partir del FEN y notifica los clics |
| Casos de uso | `src/application/gameUseCases.js` | Crear, unirse, consultar y mover; dependen solo del puerto |
| Puerto de servicio | `src/ports/chessServicePort.js` | Contrato que debe cumplir cualquier servicio de ajedrez |
| Adaptador de comunicación | `src/adapters/httpChessService.js` | Único módulo que usa `fetch` |
| Composición | `src/main.js` | Conecta adaptador, casos de uso y UI |

Como el adaptador es sustituible, se puede usar un doble de prueba
(`mockChessService.js`) sin tocar los casos de uso ni la UI.

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
| GET | `/api/games` | | Listar partidas. Filtro opcional `?status=WAITING_FOR_PLAYER` para ver las salas abiertas |

Respuesta de una partida:

```json
{
  "id": "0CCF95",
  "whitePlayer": "alice",
  "blackPlayer": "bob",
  "status": "IN_PROGRESS",
  "turn": "BLACK",
  "fen": "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b - - 0 1",
  "moves": [{ "from": "e2", "to": "e4" }]
}
```

Errores:

| HTTP | Código de error | Causa |
|---|---|---|
| 400 | `BAD_REQUEST` | Faltan campos, JSON incorrecto o `status` desconocido |
| 404 | `GAME_NOT_FOUND` | La partida no existe |
| 409 | `CONFLICT` | Movimiento ilegal, turno incorrecto o estado de partida inválido |

## Decisiones de diseño

- Una sala es una partida: su id de 6 caracteres es el código de sala.
- Un usuario es solo un nombre de jugador, sin cuenta ni login.

## Modelo de datos

Tabla `games` (entidad `GameEntity`):

| Campo | Descripción |
|---|---|
| `id` | Código de la partida / sala (clave primaria) |
| `whitePlayer`, `blackPlayer` | Nombres de los jugadores |
| `status` | `WAITING_FOR_PLAYER`, `IN_PROGRESS` o `FINISHED` |
| `turn` | `WHITE` o `BLACK` |
| `fen` | Colocación de las piezas en notación FEN |
| `moves` | Movimientos en texto, por ejemplo `e2e4,e7e5` |
| `createdAt`, `updatedAt` | Fechas de creación y última modificación |

El tablero se reconstruye con `Board.fromFen` al leer la partida.

## Cómo se conectan los usuarios

El creador recibe un id de partida de 6 caracteres. El segundo jugador lo
envía a `POST /api/games/{id}/join`. La partida pasa de
`WAITING_FOR_PLAYER` a `IN_PROGRESS`. Además, `GET /api/games?status=WAITING_FOR_PLAYER`
lista las salas abiertas.

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

Automatizada (18 tests): 12 de dominio (`GameTest`), 5 de casos de uso
(`GameServiceTest`, con el repositorio en memoria como doble de prueba) y el
test de arranque de Spring (`BackendApplicationTests`).

Comprobaciones manuales con PowerShell, con el backend arrancado:

```powershell
$g = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/games -ContentType "application/json" -Body '{"player":"alice"}'
$id = $g.id
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/games/$id/join -ContentType "application/json" -Body '{"player":"bob"}'
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/games/$id/moves -ContentType "application/json" -Body '{"player":"alice","from":"e2","to":"e4"}'
Invoke-RestMethod http://localhost:8080/api/games/$id/moves | ConvertTo-Json
Invoke-RestMethod "http://localhost:8080/api/games?status=WAITING_FOR_PLAYER" | ConvertTo-Json -Depth 5
```

Casos de error:

| Petición | Resultado |
|---|---|
| `GET /api/games/NOPE` | 404 `{"error":"GAME_NOT_FOUND","message":"Game not found: NOPE"}` |
| `POST /api/games` con `{}` | 400 `{"error":"BAD_REQUEST","message":"Missing field: player"}` |
| `POST /api/games/{id}/moves` e2 a e5 | 409 `{"error":"CONFLICT","message":"Illegal move for PAWN"}` |

Prueba de persistencia: se creó la partida `0CCF95`, se unió un segundo
jugador y se movió e2 a e4. Se paró y se volvió a arrancar el backend, y al
consultarla con `Invoke-RestMethod http://localhost:8080/api/games/0CCF95`
la partida seguía con `status: IN_PROGRESS`, `turn: BLACK` y el movimiento
`e2 → e4`.

## Capturas

<!-- Descomenta cada línea cuando exista la imagen en screenshots/ -->
<!-- ![Tests en verde](screenshots/backend-tests.png) -->
<!-- ![Partida tras reiniciar el backend](screenshots/backend-persistencia.png) -->
<!-- ![Casos de error](screenshots/backend-errores.png) -->
<!-- ![Tablero](screenshots/frontend-tablero.png) -->
<!-- ![Sala](screenshots/frontend-sala.png) -->
<!-- ![Histórico de una partida](screenshots/frontend-historico.png) -->

## Límites conocidos

- Las partidas se guardan en una base de datos H2 en fichero local; no está pensada para producción
- Los movimientos se guardan como texto en una sola columna, sin tabla propia
- Reglas de ajedrez reducidas (ver arriba)
- Sin autenticación: el jugador se identifica solo por el nombre enviado
- Los movimientos concurrentes sobre la misma partida no están sincronizados
- El frontend usa polling para ver los movimientos del rival (sin WebSocket)