/**
 * The snake. Its body is a NodeList of SnakeSegments where the FIRST node is
 * the head and the LAST node is the tail.
 *
 * Moving = add a new head in front, and (unless growing) remove the tail.
 * Both are O(1). Checking whether a cell is part of the snake is O(n).
 */
public class Snake {

    private final NodeList<SnakeSegment> body;
    private Direction direction;

    /**
     * Builds a snake with its head at (headRow, headCol) and the rest of the
     * body trailing out behind it (opposite the travel direction).
     */
    public Snake(int headRow, int headCol, int length, Direction direction) {
        this.body = new NodeList<>();
        this.direction = direction;
        for (int i = 0; i < length; i++) {
            int row = headRow - i * direction.getRowChange();
            int col = headCol - i * direction.getColChange();
            body.addLast(new SnakeSegment(row, col));
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public SnakeSegment getHead() {
        return body.getFirst();
    }

    /** Row the head will enter on the next move. */
    public int getNextHeadRow() {
        return getHead().getRow() + direction.getRowChange();
    }

    /** Column the head will enter on the next move. */
    public int getNextHeadCol() {
        return getHead().getCol() + direction.getColChange();
    }

    /** Is any part of the snake on this cell? O(n) (recursive search in the list). */
    public boolean occupies(int row, int col) {
        return body.contains(new SnakeSegment(row, col));
    }

    /**
     * Would moving the head onto (row, col) run into the snake's own body?
     *
     * If the snake is NOT growing, the tail cell becomes empty on this move,
     * so the head is allowed to move into the cell the tail is leaving.
     * If the snake IS growing (eating), the tail stays put, so it counts as a hit.
     */
    public boolean wouldHitSelf(int row, int col, boolean growing) {
        SnakeSegment target = new SnakeSegment(row, col);
        if (!growing && target.equals(body.getLast())) {
            return false;
        }
        return body.contains(target);
    }

    /** Advances one cell. If grow is true the tail is kept, making the snake longer. O(1) */
    public void move(boolean grow) {
        body.addFirst(new SnakeSegment(getNextHeadRow(), getNextHeadCol()));
        if (!grow) {
            body.removeLast();
        }
    }

    /** Removes the tail segment (used by the death animation). O(1) */
    public void removeTail() {
        if (!body.isEmpty()) {
            body.removeLast();
        }
    }

    public int size() {
        return body.size();
    }

    public boolean isEmpty() {
        return body.isEmpty();
    }

    /** Head-to-tail view of the body for drawing. */
    public Iterable<SnakeSegment> getSegments() {
        return body;
    }
}
