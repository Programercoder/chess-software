package es.ulpgc.chess.backend.domain;

public class MoveValidator {

    public void validate(Board board, Move move, Color turn) {
        Piece piece = board.pieceAt(move.from());
        if (piece == null) {
            throw new InvalidMoveException("There is no piece at " + move.from().toAlgebraic());
        }
        if (piece.color() != turn) {
            throw new InvalidMoveException("It is not " + piece.color() + "'s turn");
        }
        if (move.from().equals(move.to())) {
            throw new InvalidMoveException("Origin and destination are the same");
        }
        Piece target = board.pieceAt(move.to());
        if (target != null && target.color() == piece.color()) {
            throw new InvalidMoveException("Cannot capture your own piece");
        }
        if (!isLegalShape(board, piece, move, target)) {
            throw new InvalidMoveException("Illegal move for " + piece.type());
        }
    }

    private boolean isLegalShape(Board board, Piece piece, Move move, Piece target) {
        int dr = move.to().row() - move.from().row();
        int dc = move.to().col() - move.from().col();
        int adr = Math.abs(dr);
        int adc = Math.abs(dc);

        return switch (piece.type()) {
            case KNIGHT -> (adr == 2 && adc == 1) || (adr == 1 && adc == 2);
            case KING -> adr <= 1 && adc <= 1;
            case ROOK -> (dr == 0 || dc == 0) && pathClear(board, move);
            case BISHOP -> adr == adc && pathClear(board, move);
            case QUEEN -> (dr == 0 || dc == 0 || adr == adc) && pathClear(board, move);
            case PAWN -> isLegalPawnMove(board, piece, move, dr, dc, target);
        };
    }

    private boolean isLegalPawnMove(Board board, Piece piece, Move move, int dr, int dc, Piece target) {
        int direction = piece.color() == Color.WHITE ? -1 : 1;
        int startRow = piece.color() == Color.WHITE ? 6 : 1;

        if (dc == 0 && dr == direction) {
            return target == null;
        }
        if (dc == 0 && dr == 2 * direction && move.from().row() == startRow) {
            return target == null && pathClear(board, move);
        }
        if (Math.abs(dc) == 1 && dr == direction) {
            return target != null;
        }
        return false;
    }

    private boolean pathClear(Board board, Move move) {
        int stepRow = Integer.signum(move.to().row() - move.from().row());
        int stepCol = Integer.signum(move.to().col() - move.from().col());
        int row = move.from().row() + stepRow;
        int col = move.from().col() + stepCol;
        while (row != move.to().row() || col != move.to().col()) {
            if (board.pieceAt(new Position(row, col)) != null) {
                return false;
            }
            row += stepRow;
            col += stepCol;
        }
        return true;
    }
}