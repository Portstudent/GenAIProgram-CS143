/**
 * The phases the game moves through:
 *   READY     - board is shown, waiting for the first W/A/S/D key
 *   PLAYING   - snake is moving
 *   DYING     - snake hit something and is disappearing tail-to-head
 *   GAME_OVER - snake is gone, "Game Over" screen is showing
 */
public enum GameState {
    READY,
    PLAYING,
    DYING,
    GAME_OVER
}
