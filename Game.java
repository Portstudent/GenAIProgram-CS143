import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

/**
 * All of the game RULES live here. This class knows nothing about windows or
 * drawing, so it could be tested or reused with any kind of display.
 *
 * Collections used:
 *   - A queue of Directions (a Deque, which is a Queue that also lets us peek at
 *     the BACK) buffers turn requests in order, so two quick key presses between
 *     moves are both honored, in the order pressed.
 */
public class Game {

    private static final int START_LENGTH = 3;
    private static final int MAX_QUEUED_TURNS = 2;

    private final Board board;
    private final Random random;
    private final Deque<Direction> pendingTurns; // used as a queue: add at the back, poll from the front

    private Snake snake;
    private Mango mango;
    private int score;
    private GameState state;

    public Game(Board board) {
        this.board = board;
        this.random = new Random();
        this.pendingTurns = new ArrayDeque<>();
        this.snake = new Snake(board.getRows() / 2, board.getCols() / 2, START_LENGTH, Direction.RIGHT);
        this.score = 0;
        this.state = GameState.READY;
        placeMango();
    }

    // ---------- Getters ----------

    public Board getBoard() {
        return board;
    }

    public Snake getSnake() {
        return snake;
    }

    public Mango getMango() {
        return mango;
    }

    public int getScore() {
        return score;
    }

    public GameState getState() {
        return state;
    }

    // ---------- Input ----------

    /** Leaves the READY state and begins moving. */
    public void start() {
        if (state == GameState.READY) {
            state = GameState.PLAYING;
        }
    }

    /**
     * Requests a turn. Ignored if it points the same way as, or directly
     * opposite to, the snake's most recent (or most recently queued) direction.
     * That rule is what stops the snake from reversing into itself.
     */
    public void queueTurn(Direction requested) {
        Direction reference = pendingTurns.isEmpty() ? snake.getDirection() : pendingTurns.peekLast();
        if (requested == reference || requested.isOpposite(reference)) {
            return;
        }
        if (pendingTurns.size() < MAX_QUEUED_TURNS) {
            pendingTurns.add(requested);
        }
    }

    // ---------- Game loop step ----------

    /** Called once per timer tick. What happens depends on the current state. */
    public void update() {
        if (state == GameState.PLAYING) {
            step();
        } else if (state == GameState.DYING) {
            dieStep();
        }
    }

    /** One normal move of the snake. */
    private void step() {
        Direction nextTurn = pendingTurns.poll();
        if (nextTurn != null) {
            snake.setDirection(nextTurn);
        }

        int nextRow = snake.getNextHeadRow();
        int nextCol = snake.getNextHeadCol();

        // Solid walls: leaving the board is fatal.
        if (!board.isInside(nextRow, nextCol)) {
            state = GameState.DYING;
            return;
        }

        boolean eating = mango != null && mango.isAt(nextRow, nextCol);

        if (snake.wouldHitSelf(nextRow, nextCol, eating)) {
            state = GameState.DYING;
            return;
        }

        snake.move(eating);

        if (eating) {
            score++;
            placeMango();
            if (mango == null) {
                // The snake fills the whole board - nowhere left for a mango.
                state = GameState.DYING;
            }
        }
    }

    /** One frame of the death animation: remove the tail until nothing is left. */
    private void dieStep() {
        snake.removeTail();
        if (snake.isEmpty()) {
            state = GameState.GAME_OVER;
        }
    }

    // ---------- Mango placement (recursive) ----------

    /**
     * Puts a mango on an empty cell, or sets mango to null if the board is full.
     * Picks a random starting cell, then the recursive helper scans forward
     * (wrapping around) until it finds an empty cell.
     *
     * Note: this is not perfectly uniform - a cell that follows a run of
     * occupied cells is slightly more likely to be chosen. That's fine for a game.
     */
    private void placeMango() {
        int total = board.getCellCount();
        int startIndex = random.nextInt(total);
        mango = findFreeCell(startIndex, 0, total);
    }

    /**
     * Recursive helper. Treats the grid as cells numbered 0..total-1, row by row.
     * Base case 1: we've checked every cell -> board is full, return null.
     * Base case 2: the cell at 'index' is empty -> make a Mango there.
     * Recursive case: try the next cell (wrapping back to 0 after the last).
     *
     * Worst case O(total * n): up to 'total' cells are checked, and each check
     * calls snake.occupies(), which is O(n) for a snake of length n.
     * Recursion depth is at most 'total' (e.g. 1600 for a 40x40 board).
     */
    private Mango findFreeCell(int index, int cellsChecked, int total) {
        if (cellsChecked == total) {
            return null;
        }
        int row = index / board.getCols();
        int col = index % board.getCols();
        if (!snake.occupies(row, col)) {
            return new Mango(row, col);
        }
        return findFreeCell((index + 1) % total, cellsChecked + 1, total);
    }
}
