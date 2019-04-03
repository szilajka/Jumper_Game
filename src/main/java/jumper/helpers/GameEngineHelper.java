package jumper.helpers;

/**
 * A helper class for the engine.
 */
public class GameEngineHelper {
    /**
     * The default tolerance value to calculate with.
     */
    public static final double tolerance = 0.5;
    /**
     * How often should the {@link java.util.Timer} call the engine's method to achieve 60FPS.
     */
    public static final double sixtyFpsSeconds = 0.017;
    /**
     * All scene's width.
     */
    public static final double WIDTH = 800.0;
    /**
     * All scene's height.
     */
    public static final double HEIGHT = 600.0;
    /**
     * A constant that helps to calculate the {@code player}' point.
     */
    public static final int upMultiplier = 100;
    /**
     * The level counter that is reached from everywhere.
     */
    public static int levelCounter = 1;

    /**
     * Calculates the {@code player}'s point.
     * @param enemiesNumber number of generated enemies
     * @param steppedEnemies number of enemies that the {@code player} stepped on
     * @param seconds the elapsed time the {@code player} spent in the game while
     *                completing the current level.
     * @return the calculated point
     */
    public static double calculatePoints(int enemiesNumber, int steppedEnemies, double seconds) {
        return Math.floor((enemiesNumber - steppedEnemies) * upMultiplier / seconds);
    }
}
