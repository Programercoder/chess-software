package es.ulpgc.chess.backend.domain;

public record Position(int row, int col) {
    public Position {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            throw new IllegalArgumentException("Position is out of bounds: " + row + "," + col);
        }
    }

    // "e2" -> Position(6, 4)
    public static Position fromAlgebraic(String text) {
        if (text == null || text.length() != 2) {
            throw new IllegalArgumentException("invalid square: " + text);
        }
        int col = text.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(text.charAt(1));
        return new Position(row, col);
    }

    public String toAlgebraic() {
        return "" + (char) ('a' + col) + (8 - row);
    }
}
