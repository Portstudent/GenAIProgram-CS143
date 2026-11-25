import java.awt.*;

/** A glowing mango worth five growth steps. */
public class SuperMango extends Mango {
    public SuperMango(int row, int col) { super(row, col); }
    @Override public void draw(Graphics2D g, int x, int y, int cellSize) {
        Graphics2D copy = (Graphics2D) g.create();
        float c = cellSize / 2f;
        copy.setPaint(new RadialGradientPaint(x+c, y+c, c,
                new float[]{0f, .55f, 1f}, new Color[]{new Color(255,255,100,255),
                new Color(255,205,0,190), new Color(255,205,0,0)}));
        copy.fillOval(x, y, cellSize, cellSize);
        int inset = Math.max(1, cellSize / 8);
        super.draw(copy, x+inset, y+inset, cellSize-2*inset);
        copy.setColor(Color.WHITE);
        int r = Math.max(1, cellSize/9);
        int sx = x+cellSize*3/4, sy = y+cellSize/5;
        copy.drawLine(sx-r,sy,sx+r,sy); copy.drawLine(sx,sy-r,sx,sy+r);
        copy.dispose();
    }
}
