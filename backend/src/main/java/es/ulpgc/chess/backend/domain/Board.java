package es.ulpgc.chess.backend.domain;

public class Board {
    private final Piece[][] squares = new Piece[8][8];

    public static Board initial() {
        Board board = new Board();
        PieceType[] back = {
                PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };
        for (int col = 0; col < 8; col++) {
            board.squares[0][col] = new Piece(back[col], Color.BLACK);
            board.squares[1][col] = new Piece(PieceType.PAWN, Color.BLACK);
            board.squares[6][col] = new Piece(PieceType.PAWN, Color.WHITE);
            board.squares[7][col] = new Piece(back[col], Color.WHITE);
        }
        return board;
    }

    public static Board fromFen(String placement) {
        String[] rows = placement.split("/");
        if (rows.length != 8) {
            throw new IllegalArgumentException("Invalid FEN: expected 8 rows");
        }
        Board board = new Board();
        for (int row = 0; row < 8; row++) {
            int col = 0;
            for (char c : rows[row].toCharArray()) {
                if (Character.isDigit(c)) {
                    col += Character.getNumericValue(c);
                } else {
                    if (col > 7) {
                        throw new IllegalArgumentException("Invalid FEN: row too long");
                    }
                    board.squares[row][col] = pieceFromSymbol(c);
                    col++;
                }
            }
            if (col != 8) {
                throw new IllegalArgumentException("Invalid FEN: row " + (8 - row) + " does not have 8 squares");
            }
        }
        return board;
    }

    public Piece pieceAt(Position position) {
        return squares[position.row()][position.col()];
    }

    public void place(Position position, Piece piece) {
        squares[position.row()][position.col()] = piece;
    }

    public void movePiece(Position from, Position to) {
        squares[to.row()][to.col()] = squares[from.row()][from.col()];
        squares[from.row()][from.col()] = null;
    }

    public boolean isEmpty(Position position) {
        return pieceAt(position) == null;
    }

    public String toFen() {
        StringBuilder fen = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            int empty = 0;
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece == null) {
                    empty++;
                } else {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append(symbolOf(piece));
                }
            }
            if (empty > 0) fen.append(empty);
            if (row < 7) fen.append('/');
        }
        return fen.toString();
    }

    private static char symbolOf(Piece piece) {
        char c = switch (piece.type()) {
            case PAWN -> 'p';
            case ROOK -> 'r';
            case KNIGHT -> 'n';
            case BISHOP -> 'b';
            case QUEEN -> 'q';
            case KING -> 'k';
        };
        return piece.color() == Color.WHITE ? Character.toUpperCase(c) : c;
    }

    private static Piece pieceFromSymbol(char symbol) {
        Color color = Character.isUpperCase(symbol) ? Color.WHITE : Color.BLACK;
        PieceType type = switch (Character.toLowerCase(symbol)) {
            case 'p' -> PieceType.PAWN;
            case 'r' -> PieceType.ROOK;
            case 'n' -> PieceType.KNIGHT;
            case 'b' -> PieceType.BISHOP;
            case 'q' -> PieceType.QUEEN;
            case 'k' -> PieceType.KING;
            default -> throw new IllegalArgumentException("Invalid FEN symbol: " + symbol);
        };
        return new Piece(type, color);
    }
}
