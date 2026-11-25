/**
 * The playing field: a grid of rows x cols cells with SOLID walls.
 * Row 0 / column 0 is the top-left cell.
 */
public class Board {

    private final int rows;
    private final int cols;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getCellCount() {
        return rows * cols;
    }

    /** True if (row, col) is on the board. Anything else is a wall. */
    public boolean isInside(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
}
