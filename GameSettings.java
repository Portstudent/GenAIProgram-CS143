/**
 * The options the player picks before the game starts.
 * Immutable: values are set once in the constructor and only read afterward.
 */
public class GameSettings {

    private final int rows;
    private final int cols;
    private final int cellSize;     // pixels per cell
    private final int moveDelayMs;  // milliseconds between snake moves (smaller = faster)

    public GameSettings(int rows, int cols, int cellSize, int moveDelayMs) {
        this.rows = rows;
        this.cols = cols;
        this.cellSize = cellSize;
        this.moveDelayMs = moveDelayMs;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getCellSize() {
        return cellSize;
    }

    public int getMoveDelayMs() {
        return moveDelayMs;
    }
}
