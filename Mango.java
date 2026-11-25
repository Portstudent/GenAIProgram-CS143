import java.awt.Color;
import java.awt.Graphics2D;

/**
 * The fruit the snake eats to grow: an orange circle with a green leaf.
 */
public class Mango extends GamePiece {

    private static final Color MANGO_ORANGE = new Color(255, 140, 0);
    private static final Color LEAF_GREEN = new Color(34, 139, 34);

    public Mango(int row, int col) {
        super(row, col);
    }

    @Override
    public void draw(Graphics2D g, int x, int y, int cellSize) {
        // The orange body sits toward the bottom of the cell to leave room for the leaf.
        int fruitSize = (int) (cellSize * 0.75);
        int fruitX = x + (cellSize - fruitSize) / 2;
        int fruitY = y + cellSize - fruitSize - Math.max(1, cellSize / 16);

        g.setPaint(new java.awt.GradientPaint(fruitX, fruitY, new Color(255, 218, 75),
                fruitX + fruitSize, fruitY + fruitSize, new Color(225, 85, 12)));
        g.fillOval(fruitX, fruitY, fruitSize, fruitSize);
        g.setColor(new Color(255, 245, 180, 180));
        g.fillOval(fruitX + fruitSize / 5, fruitY + fruitSize / 6,
                Math.max(1, fruitSize / 5), Math.max(1, fruitSize / 3));
        g.setColor(new Color(115, 75, 30));
        g.drawLine(x + cellSize / 2, fruitY + 2, x + cellSize / 2, fruitY - 2);

        // The green leaf is a small tilted oval at the top of the fruit.
        int leafWidth = Math.max(4, cellSize / 3);
        int leafHeight = Math.max(2, cellSize / 6);
        int leafCenterX = x + (int) (cellSize * 0.6);
        int leafCenterY = fruitY + 1;

        Graphics2D leafGraphics = (Graphics2D) g.create(); // copy so the tilt doesn't affect other drawing
        leafGraphics.rotate(Math.toRadians(-35), leafCenterX, leafCenterY);
        leafGraphics.setColor(LEAF_GREEN);
        leafGraphics.fillOval(leafCenterX - leafWidth / 2, leafCenterY - leafHeight / 2, leafWidth, leafHeight);
        leafGraphics.dispose();
    }
}
