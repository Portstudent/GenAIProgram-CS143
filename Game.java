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
    private SuperMango superMango;
    private Scissors scissors;
    private int growthRemaining;
    private long superMangoSpawnTime;
    private long scissorsSpawnTime;
    private long superMangoAppeared, scissorsAppeared;
    private static final long FLASH_NS = 10_000_000_000L;
    private static final long EXPIRE_NS = 15_000_000_000L;
    private static final long BLINK_NS = 250_000_000L;
    private static final long MIN_SPAWN_MS = 10_000;
    private static final int SPAWN_RANGE_MS = 35_001;
    private boolean collisionDeath;
    public boolean isCollisionDeath() { return collisionDeath; }
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

    public SuperMango getSuperMango() { return superMango; }

    public Scissors getScissors() { return scissors; }

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
            superMangoSpawnTime = nextSpawnTime();
            scissorsSpawnTime = nextSpawnTime();
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
            placeSpecialItems();
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
            collisionDeath = true;
            state = GameState.DYING;
            return;
        }

        boolean eating = mango != null && mango.isAt(nextRow, nextCol);
        boolean eatingSuper = superMango != null && superMango.isAt(nextRow, nextCol);
        boolean cutting = scissors != null && scissors.isAt(nextRow, nextCol);
        boolean growing = growthRemaining > 0 || eating || eatingSuper;

        if (snake.wouldHitSelf(nextRow, nextCol, growing)) {
            collisionDeath = true;
            state = GameState.DYING;
            return;
        }
        if (eating) { growthRemaining++; score++; mango = null; }
        if (eatingSuper) { growthRemaining += 5; score += 5; superMango = null; superMangoSpawnTime = nextSpawnTime(); }
        snake.move(growing);
        if (growing) growthRemaining--;
        if (cutting) {
            scissors = null;
            scissorsSpawnTime = nextSpawnTime();
            for (int i = 0; i < 3 && snake.size() > 1; i++) snake.removeTail();
        }
        if (mango == null) placeMango();
        placeSpecialItems();
        if (snake.size() == board.getCellCount()) state = GameState.DYING;
    }

    // Monotonic clock: independent of movement speed and wall-clock changes.
    private long nextSpawnTime() {
        return System.nanoTime() + (MIN_SPAWN_MS + random.nextInt(SPAWN_RANGE_MS)) * 1_000_000L;
    }

    public void placeSpecialItems() {
        if (state != GameState.PLAYING) return;
        long now = System.nanoTime();
        if (superMango != null && now - superMangoAppeared >= EXPIRE_NS) {
            superMango = null;
            superMangoSpawnTime = nextSpawnTime();
        }
        if (scissors != null && now - scissorsAppeared >= EXPIRE_NS) {
            scissors = null;
            scissorsSpawnTime = nextSpawnTime();
        }
        if (superMango == null && now - superMangoSpawnTime >= 0) {
            Mango cell = randomFreeCell();
            if (cell != null) {
                superMango = new SuperMango(cell.getRow(), cell.getCol());
                superMangoAppeared = now;
            }
        }
        if (scissors == null && now - scissorsSpawnTime >= 0) {
            Mango cell = randomFreeCell();
            if (cell != null) {
                scissors = new Scissors(cell.getRow(), cell.getCol());
                scissorsAppeared = now;
            }
        }
    }

    public boolean isSuperMangoVisible() {
        return superMango != null && visibleAt(superMangoAppeared);
    }

    public boolean isScissorsVisible() {
        return scissors != null && visibleAt(scissorsAppeared);
    }

    private boolean visibleAt(long appeared) {
        long age = System.nanoTime() - appeared;
        return age < FLASH_NS || (age < EXPIRE_NS && ((age - FLASH_NS) / BLINK_NS) % 2 == 0);
    }

    private Mango randomFreeCell() {
        int total = board.getCellCount();
        return findFreeCell(random.nextInt(total), 0, total);
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
        if (!snake.occupies(row, col)
                && (mango == null || !mango.isAt(row, col))
                && (superMango == null || !superMango.isAt(row, col))
                && (scissors == null || !scissors.isAt(row, col))) {
            return new Mango(row, col);
        }
        return findFreeCell((index + 1) % total, cellsChecked + 1, total);
    }
}
