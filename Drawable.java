import java.awt.Graphics2D;

/**
 * Anything that can paint itself inside one square cell of the board.
 *
 * (x, y) is the pixel position of the cell's top-left corner and
 * cellSize is the width/height of the cell in pixels.
 */
public interface Drawable {
    void draw(Graphics2D g, int x, int y, int cellSize);
}
