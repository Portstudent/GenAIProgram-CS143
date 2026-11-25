import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * GUI "plumbing": draws the game and turns key presses / timer ticks into
 * calls on the Game object. All game RULES are in Game, not here.
 *
 * Interfaces implemented:
 *   ActionListener - actionPerformed() is called on every timer tick
 *   KeyListener    - keyPressed() is called when a key goes down
 *
 * Collections used:
 *   Map<Integer, Direction> (a HashMap) looks up which Direction a key code
 *   means in O(1) average time.
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private static final Color CREAM = new Color(255, 248, 225);
    private static final Color BORDER_COLOR = Color.BLACK;

    private static final int BORDER_THICKNESS = 6;
    private static final int HEADER_HEIGHT = 44;
    private static final int MIN_WIDTH = 380;
    private static final int MIN_HEIGHT = 440;
    private static final int RESTART_LOCKOUT_MS = 600; // ignore keys right after Game Over so mashing doesn't skip it

    private final GameSettings settings;
    private final Map<Integer, Direction> keyBindings;
    private final Timer timer;

    private Game game;
    private long gameOverTime;

    public GamePanel(GameSettings settings) {
        this.settings = settings;

        keyBindings = new HashMap<>();
        keyBindings.put(KeyEvent.VK_W, Direction.UP);
        keyBindings.put(KeyEvent.VK_A, Direction.LEFT);
        keyBindings.put(KeyEvent.VK_S, Direction.DOWN);
        keyBindings.put(KeyEvent.VK_D, Direction.RIGHT);

        int boardWidth = settings.getCols() * settings.getCellSize();
        int boardHeight = settings.getRows() * settings.getCellSize();
        int width = Math.max(MIN_WIDTH, boardWidth + 2 * (BORDER_THICKNESS + 10));
        int height = Math.max(MIN_HEIGHT, HEADER_HEIGHT + boardHeight + BORDER_THICKNESS + 16);
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);

        game = createGame();
        timer = new Timer(settings.getMoveDelayMs(), this);
        timer.start();
    }

    private Game createGame() {
        return new Game(new Board(settings.getRows(), settings.getCols()));
    }

    private void restart() {
        game = createGame();
        timer.setDelay(settings.getMoveDelayMs());
        timer.start();
        repaint();
    }

    // ---------- Timer tick ----------

    @Override
    public void actionPerformed(ActionEvent e) {
        GameState before = game.getState();
        game.update();
        GameState after = game.getState();

        if (before == GameState.PLAYING && after == GameState.DYING) {
            // Make the disappearing animation take about 1.5 seconds no matter how long the snake is.
            int delay = 1500 / Math.max(1, game.getSnake().size());
            timer.setDelay(Math.max(15, Math.min(100, delay)));
        }
        if (after == GameState.GAME_OVER) {
            gameOverTime = System.currentTimeMillis();
            timer.stop();
        }
        repaint();
    }

    // ---------- Keyboard ----------

    @Override
    public void keyPressed(KeyEvent e) {
        GameState state = game.getState();

        if (state == GameState.GAME_OVER) {
            if (System.currentTimeMillis() - gameOverTime >= RESTART_LOCKOUT_MS) {
                restart(); // any key restarts
            }
            return;
        }

        Direction direction = keyBindings.get(e.getKeyCode());
        if (direction == null) {
            return; // not a W/A/S/D key
        }

        if (state == GameState.READY) {
            game.queueTurn(direction);
            game.start();
        } else if (state == GameState.PLAYING) {
            game.queueTurn(direction);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // not needed
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // not needed
    }

    // ---------- Drawing ----------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int cell = settings.getCellSize();
        int boardWidth = settings.getCols() * cell;
        int boardHeight = settings.getRows() * cell;
        int boardX = (getWidth() - boardWidth) / 2;
        int boardY = HEADER_HEIGHT;

        // Score in the top-left corner while playing
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 20));
        g2.drawString("Score: " + game.getScore(), 16, 28);

        // Black border, then cream background
        g2.setColor(BORDER_COLOR);
        g2.fillRect(boardX - BORDER_THICKNESS, boardY - BORDER_THICKNESS,
                boardWidth + 2 * BORDER_THICKNESS, boardHeight + 2 * BORDER_THICKNESS);
        g2.setColor(CREAM);
        g2.fillRect(boardX, boardY, boardWidth, boardHeight);

        // Mango
        Mango mango = game.getMango();
        if (mango != null) {
            mango.draw(g2, boardX + mango.getCol() * cell, boardY + mango.getRow() * cell, cell);
        }

        // Snake (for-each works because NodeList implements Iterable)
        for (SnakeSegment segment : game.getSnake().getSegments()) {
            segment.draw(g2, boardX + segment.getCol() * cell, boardY + segment.getRow() * cell, cell);
        }

        if (game.getState() == GameState.READY) {
            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            drawCentered(g2, "Press W, A, S or D to start", getWidth() / 2, boardY + boardHeight * 3 / 4);
        }

        if (game.getState() == GameState.GAME_OVER) {
            drawGameOverScreen(g2);
        }
    }

    private void drawGameOverScreen(Graphics2D g2) {
        int boxWidth = 300;
        int boxHeight = 170;
        int boxX = (getWidth() - boxWidth) / 2;
        int boxY = (getHeight() - boxHeight) / 2;

        g2.setColor(BORDER_COLOR);
        g2.fillRect(boxX - 4, boxY - 4, boxWidth + 8, boxHeight + 8);
        g2.setColor(CREAM);
        g2.fillRect(boxX, boxY, boxWidth, boxHeight);

        int centerX = getWidth() / 2;
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 34));
        drawCentered(g2, "GAME OVER", centerX, boxY + 55);
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        drawCentered(g2, "Score: " + game.getScore(), centerX, boxY + 100);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        drawCentered(g2, "Press any key to play again", centerX, boxY + 140);
    }

    /** Draws text horizontally centered on centerX, with its baseline at y. */
    private void drawCentered(Graphics2D g2, String text, int centerX, int y) {
        FontMetrics metrics = g2.getFontMetrics();
        g2.drawString(text, centerX - metrics.stringWidth(text) / 2, y);
    }
}
