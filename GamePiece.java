/**
 * Base class for everything that sits on one cell of the board
 * (snake segments and the mango).
 *
 * It stores the piece's row and column (encapsulated - private fields with
 * getters) and implements the Drawable interface. Because draw() is not
 * implemented here, this class is abstract: each subclass decides how it
 * looks.
 *
 * equals() compares row and column, so two pieces of the SAME type on the
 * same cell are considered equal. NodeList.contains() relies on this.
 */
public abstract class GamePiece implements Drawable {

    private final int row;
    private final int col;

    public GamePiece(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isAt(int otherRow, int otherCol) {
        return row == otherRow && col == otherCol;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GamePiece other = (GamePiece) obj;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }
}
