import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

/** Draws the linked body as one continuous rounded shape. */
public class SnakeRenderer {
    public static void draw(Graphics2D g, Snake snake, int x, int y, int cell) {
        ArrayList<Point2D.Double> points = new ArrayList<>();
        for (SnakeSegment segment : snake.getSegments()) {
            points.add(new Point2D.Double(x+(segment.getCol()+.5)*cell,
                    y+(segment.getRow()+.5)*cell));
        }
        if (points.isEmpty()) return;
        Graphics2D a = (Graphics2D) g.create();
        double radius = cell*.43;
        a.setColor(new Color(0,0,139));
        Point2D.Double head = points.get(0);
        if (points.size() > 1) {
            Path2D body = new Path2D.Double();
            body.moveTo(head.x,head.y);
            for (int i=1;i<points.size()-1;i++) body.lineTo(points.get(i).x,points.get(i).y);
            a.setStroke(new BasicStroke((float)(2*radius), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            a.draw(body);
            Point2D.Double base = points.get(points.size()-2), tail = points.get(points.size()-1);
            double dx=(tail.x-base.x)/cell, dy=(tail.y-base.y)/cell;
            Path2D tip = new Path2D.Double();
            tip.moveTo(base.x-dy*radius,base.y+dx*radius);
            tip.lineTo(tail.x+dx*cell*.4,tail.y+dy*cell*.4);
            tip.lineTo(base.x+dy*radius,base.y-dx*radius); tip.closePath();
            a.fill(tip);
            a.fill(new Ellipse2D.Double(base.x-radius,base.y-radius,2*radius,2*radius));
        }
        a.fill(new Ellipse2D.Double(head.x-radius,head.y-radius,2*radius,2*radius));
        Direction direction = snake.getDirection();
        double dx=direction.getColChange(), dy=direction.getRowChange();
        for (int side : new int[]{-1,1}) {
            double ex=head.x+dx*cell*.19-dy*side*cell*.20;
            double ey=head.y+dy*cell*.19+dx*side*cell*.20;
            double er=cell*.13;
            a.setColor(Color.WHITE); a.fill(new Ellipse2D.Double(ex-er,ey-er,2*er,2*er));
            double pr=cell*.065;
            a.setColor(Color.BLACK); a.fill(new Ellipse2D.Double(ex+dx*cell*.045-pr,ey+dy*cell*.045-pr,2*pr,2*pr));
        }
        a.dispose();
    }
}
