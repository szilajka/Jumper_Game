package jumper.model;

import javafx.scene.paint.Color;
import jumper.helpers.EnemyType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FallingRectangle extends Rect {
    private static final Logger logger = LogManager.getLogger("FallingRectangle");
    public static final double basicEnemyWidth = 100.0;
    public static final double basicEnemyHeight = 25.0;
    public static final double spikeEnemyWidth = 150.0;
    public static final double spikeEnemyHeight = 50.0;
    public static final double basicEnemyVelocitiyY = 100.0;
    public static final double spikeEnemyVelocityY = 150.0;
    private double startY = 0.0;
    private EnemyType enemyType;

    /**
     * {@code FallingRectangle}'s constructor.
     *
     * @param x         {@code FallingRectangle}'s upper left X coordinate
     * @param y         {@code FallingRectangle}'s upper left Y coordinate
     * @param width     {@code FallingRectangle}'s width
     * @param height    {@code FallingRectangle}'s height
     * @param enemyType {@code FallingRectangle}'s type
     */
    public FallingRectangle(double x, double y, double width, double height, EnemyType enemyType) {
        super(x, y, width, height);
        logger.debug("FallingRectangle constructor called with 4 parameters.");
        this.startY = y;
        this.enemyType = enemyType;
        super.setVelocityY(basicEnemyVelocitiyY);
    }

    /**
     * {@code FallingRectangle}'s constructor.
     *
     * @param x         {@code FallingRectangle}'s upper left X coordinate
     * @param y         {@code FallingRectangle}'s upper left Y coordinate
     * @param width     {@code FallingRectangle}'s width
     * @param height    {@code FallingRectangle}'s height
     * @param color     {@code FallingRectangle}'s {@link Color}
     * @param enemyType {@code FallingRectangle}'s type
     */
    public FallingRectangle(double x, double y, double width, double height, Color color,
                            EnemyType enemyType) {
        super(x, y, width, height, color);
        logger.debug("FallingRectangle constructor called with 5 parameters, Color.");
        this.startY = y;
        this.enemyType = enemyType;
        super.setVelocityY(basicEnemyVelocitiyY);
    }

    /**
     * {@code FallingRectangle}'s constructor.
     *
     * @param x         {@code FallingRectangle}'s upper left X coordinate
     * @param y         {@code FallingRectangle}'s upper left Y coordinate
     * @param width     {@code FallingRectangle}'s width
     * @param height    {@code FallingRectangle}'s height
     * @param velocityY {@code FallingRectangle}'s Y velocity
     * @param enemyType {@code FallingRectangle}'s type
     */
    public FallingRectangle(double x, double y, double width, double height, double velocityY,
                            EnemyType enemyType) {
        super(x, y, width, height);
        logger.debug("FallingRectangle constructor called with 5 parameters, velocityY.");
        this.startY = y;
        this.enemyType = enemyType;
        super.setVelocityY(velocityY);
    }

    /**
     * {@code FallingRectangle}'s constructor.
     *
     * @param x         {@code FallingRectangle}'s upper left X coordinate
     * @param y         {@code FallingRectangle}'s upper left Y coordinate
     * @param width     {@code FallingRectangle}'s width
     * @param height    {@code FallingRectangle}'s height
     * @param color     {@code FallingRectangle}'s {@link Color}
     * @param velocityY {@code FallingRectangle}'s Y velocity
     * @param enemyType {@code FallingRectangle}'s type
     */
    public FallingRectangle(double x, double y, double width, double height, Color color,
                            double velocityY, EnemyType enemyType) {
        super(x, y, width, height, color);
        logger.debug("FallingRectangle constructor called with 6 parameters.");
        this.startY = y;
        this.enemyType = enemyType;
        super.setVelocityY(velocityY);
    }

    /**
     * Gets the {@code FallingRectangle}'s initial Y position.
     *
     * @return the value of the {@code FallingRectangle}'s initial Y position.
     */
    public double getStartY() {
        return startY;
    }

    /**
     * Gets the type of the enemy.
     * <p>
     * It can be a basic or a spike enemy.
     *
     * @return the type of the enemy
     */
    public EnemyType getEnemyType() {
        return enemyType;
    }
}
