/**
 * The four directions the snake can travel.
 * Each direction knows how it changes a row and a column on the grid.
 * (Row 0 is the TOP of the board, so UP means the row number gets smaller.)
 */
public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int rowChange;
    private final int colChange;

    Direction(int rowChange, int colChange) {
        this.rowChange = rowChange;
        this.colChange = colChange;
    }

    public int getRowChange() {
        return rowChange;
    }

    public int getColChange() {
        return colChange;
    }

    /** True if 'other' points the exact opposite way (UP/DOWN or LEFT/RIGHT). */
    public boolean isOpposite(Direction other) {
        return rowChange == -other.rowChange && colChange == -other.colChange;
    }
}
