package jumper.helpers;

public class GameEngineHelper {
    public static final double tolerance = 0.5;
    public static final double sixtyFpsSeconds = 0.017;
    public static final double WIDTH = 800.0;
    public static final double HEIGHT = 600.0;
    public static final int upMultiplier = 100;
    public static int levelCounter = 1;

    public static double calculatePoints(int enemiesNumber, int steppedEnemies, double seconds) {
        return Math.floor((enemiesNumber - steppedEnemies) * upMultiplier / seconds);
    }
}
