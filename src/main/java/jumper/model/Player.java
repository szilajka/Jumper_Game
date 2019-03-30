package jumper.model;

import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Player extends Rect {
    private static final Logger logger = LogManager.getLogger("Player");
    public static double playerWidth = 100.0;
    public static double playerHeight = 100.0;
    private double startVelocityY = -250.0;
    private final double decreaseStartVelocityY = 10.0;
    private final double maxVelocityX = 100.0;
    private double startY;
    private double actualY;
    private boolean jumping = false;
    private boolean falling = false;
    private int crashedSpike = 0;

    /**
     * {@code Player}'s constructor
     *
     * @param x      {@code Player}'s upper left X coordinate
     * @param y      {@code Player}'s upper left Y coordinate
     * @param width  {@code Player}'s width
     * @param height {@code Player}'s height
     */
    public Player(double x, double y, double width, double height) {
        super(x, y, width, height);
        logger.debug("Player constructor called with 4 parameters.");
    }

    /**
     * {@code Player}'s constructor
     *
     * @param x      {@code Player}'s upper left X coordinate
     * @param y      {@code Player}'s upper left Y coordinate
     * @param width  {@code Player}'s width
     * @param height {@code Player}'s height
     * @param color  {@code Player}'s {@link Color}
     */
    public Player(double x, double y, double width, double height, Color color) {
        super(x, y, width, height, color);
        logger.debug("Player constructor called with 5 parameters.");
    }

    /**
     * Gets the value of {@code falling}.
     * <p>
     * If the {@code Player} is falling, this returns true.
     *
     * @return the value of {@code falling}.
     */
    public boolean isFalling() {
        return falling;
    }

    /**
     * Sets the value of {@code falling}.
     *
     * @param falling the value to be passed
     */
    public void setFalling(boolean falling) {
        this.falling = falling;
    }

    /**
     * Gets the {@code Player}'s Y coordinate from where it jumped or where it crashed.
     *
     * @return the value of the Y start coordinate
     */
    public double getStartY() {
        return startY;
    }

    /**
     * Sets the {@code Player}'s Y coordinate from where it jumped or where it crashed.
     *
     * @param startY the value to be passed
     */
    public void setStartY(double startY) {
        this.startY = startY;
    }

    /**
     * Gets the {@code Player}'s actual Y coordinate.
     *
     * @return the value of the actual Y coordinate
     */
    public double getActualY() {
        return actualY;
    }

    /**
     * Sets the {@code Player}'s actual Y coordinate.
     *
     * @param actualY the value to be passed
     */
    public void setActualY(double actualY) {
        this.actualY = actualY;
    }

    /**
     * Gets the {@code Player}'s Y velocity.
     * <p>
     * This returns a constant value that is used to calculate the actual Y coordinate of
     * the {@code Player}.
     *
     * @return the value of the {@code Player}'s Y velocity
     */
    public double getStartVelocityY() {
        return startVelocityY;
    }

    /**
     * Sets the {@code Player}'s Y velocity.
     * <p>
     * This is used when initializing and when it crashes with something, then the value
     * of this is decreased.
     *
     * @param startVelocityY the value to be passed
     */
    public void setStartVelocityY(double startVelocityY) {
        this.startVelocityY = startVelocityY;
    }

    /**
     * Gets the value that decreases the {@code Player}'s Y velocity.
     *
     * @return the decreasing value
     */
    public double getDecreaseStartVelocityY() {
        return decreaseStartVelocityY;
    }

    /**
     * Gets the {@code Player}'s X velocity.
     *
     * @return the {@code Player}'s X velocity
     */
    public double getMaxVelocityX() {
        return maxVelocityX;
    }

    /**
     * Gets if {@code Player} is jumping or not.
     *
     * @return true, if the {@code Player} is jumping, else false
     */
    public boolean isJumping() {
        return this.jumping;
    }

    /**
     * Sets the value of jumping.
     *
     * @param jumping the value to be passed
     */
    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    /**
     * Gets the number of crashing with spike {@code enemy}.
     *
     * @return number of crashing with spike {@code enemy}
     */
    public int getCrashedSpike() {
        return crashedSpike;
    }

    /**
     * Sets the number of crashing with spike {@code enemy}.
     * @param crashedSpike the number to be set.
     */
    public void setCrashedSpike(int crashedSpike) {
        this.crashedSpike = crashedSpike;
    }
}
