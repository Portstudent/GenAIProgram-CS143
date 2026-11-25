import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * GUI "plumbing": shows a small window where the player picks grid options.
 * Returns a GameSettings object, or null if the player cancels.
 */
public class SetupDialog {

    public static GameSettings promptForSettings() {
        JSpinner rowsSpinner = new JSpinner(new SpinnerNumberModel(20, 8, 40, 1));
        JSpinner colsSpinner = new JSpinner(new SpinnerNumberModel(20, 8, 40, 1));
        JSpinner cellSpinner = new JSpinner(new SpinnerNumberModel(25, 10, 40, 1));
        JSpinner delaySpinner = new JSpinner(new SpinnerNumberModel(120, 40, 400, 10));

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Rows (8-40):"));
        panel.add(rowsSpinner);
        panel.add(new JLabel("Columns (8-40):"));
        panel.add(colsSpinner);
        panel.add(new JLabel("Cell size in pixels (10-40):"));
        panel.add(cellSpinner);
        panel.add(new JLabel("Ms per move (40-400, lower = faster):"));
        panel.add(delaySpinner);
        panel.add(new JLabel("Tip: big boards may not fit your screen."));

        int choice = JOptionPane.showConfirmDialog(
                null, panel, "Snake - Grid Options",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION) {
            return null;
        }

        int rows = (Integer) rowsSpinner.getValue();
        int cols = (Integer) colsSpinner.getValue();
        int cellSize = (Integer) cellSpinner.getValue();
        int delay = (Integer) delaySpinner.getValue();
        return new GameSettings(rows, cols, cellSize, delay);
    }
}
