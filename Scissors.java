import java.awt.*;
import java.awt.geom.*;

/** Collecting scissors removes up to three tail segments. */
public class Scissors extends GamePiece {
    public Scissors(int row, int col) { super(row, col); }
    @Override public void draw(Graphics2D g, int x, int y, int cellSize) {
        Graphics2D a = (Graphics2D) g.create();
        a.translate(x,y); a.scale(cellSize/24.0,cellSize/24.0);
        a.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        a.setColor(new Color(85,100,115));
        a.drawLine(7,15,19,3); a.drawLine(17,15,5,3);
        a.setColor(new Color(210,55,55));
        a.draw(new Ellipse2D.Double(3,14,8,8));
        a.draw(new Ellipse2D.Double(13,14,8,8));
        a.setColor(new Color(230,235,240)); a.fill(new Ellipse2D.Double(10,9,4,4));
        a.dispose();
    }
}
