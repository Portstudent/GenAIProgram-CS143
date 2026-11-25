import javax.swing.JFrame;

/**
 * Program entry point. Run THIS class in jGRASP.
 *
 * Shows the grid-options dialog, then opens the game window.
 */
public class SnakeGame {

    public static void main(String[] args) {
        GameSettings settings = SetupDialog.promptForSettings();
        if (settings == null) {
            System.exit(0); // player cancelled
        }

        JFrame frame = new JFrame("Snake");
        GamePanel panel = new GamePanel(settings);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        panel.requestFocusInWindow();
    }
}
