import java.awt.Color;
import java.awt.Graphics2D;

/**
 * One square piece of the snake's body (inherits row/col from GamePiece).
 */
public class SnakeSegment extends GamePiece {

    private static final Color SNAKE_BLUE = new Color(0, 0, 139); // dark blue

    public SnakeSegment(int row, int col) {
        super(row, col);
    }

    @Override
    public void draw(Graphics2D g, int x, int y, int cellSize) {
        g.setColor(SNAKE_BLUE);
        int arc = cellSize / 3;
        g.fillRoundRect(x + 1, y + 1, cellSize - 2, cellSize - 2, arc, arc);
    }
}
