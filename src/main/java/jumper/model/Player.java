package jumper.model;

import javafx.scene.paint.Color;
import org.tinylog.Logger;

import java.util.Objects;

/**
 * The player class, this represents the user in the game.
 */
public class Player extends Rect {
    /**
     * The player's width.
     */
    public static double playerWidth = 100.0;
    /**
     * The player's height.
     */
    public static double playerHeight = 100.0;
    /**
     * A constant, the player's starting Y velocity.
     */
    public final static int finalStartVelocityY = -250;
    /**
     * The player's starting Y velocity.
     * <p>
     * When the player is crashing, this value will be reduced.
     */
    private double startVelocityY = finalStartVelocityY;
    /**
     * The amount of which the player's Y velocity should be decreased.
     */
    private final double decreaseStartVelocityY = 50.0;
    /**
     * The player's maximum X velocity.
     */
    private final double maxVelocityX = 100.0;
    /**
     * The player's upper left Y position at the start.
     * <p>
     * This coordinate is recomputed when the player jumps or crashed or lands.
     */
    private double startY;
    /**
     * The player's actual upper left Y position.
     */
    private double actualY;
    /**
     * Indicates whether the player is jumping or not.
     */
    private boolean jumping = false;
    /**
     * Indicates whether the player is falling or not.
     */
    private boolean falling = false;
    /**
     * How many {@link jumper.helpers.EnemyType#SpikeEnemy} crashed with the player.
     * <p>
     * If this number reaches 3, then the game ends.
     */
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
        Logger.debug("Player constructor called with 4 parameters.");
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
        Logger.debug("Player constructor called with 5 parameters.");
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
     *
     * @param crashedSpike the number to be set.
     */
    public void setCrashedSpike(int crashedSpike) {
        this.crashedSpike = crashedSpike;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Player player = (Player) o;
        return Double.compare(player.startVelocityY, startVelocityY) == 0 &&
                Double.compare(player.decreaseStartVelocityY, decreaseStartVelocityY) == 0 &&
                Double.compare(player.maxVelocityX, maxVelocityX) == 0 &&
                Double.compare(player.startY, startY) == 0 &&
                Double.compare(player.actualY, actualY) == 0 &&
                jumping == player.jumping &&
                falling == player.falling &&
                crashedSpike == player.crashedSpike;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), startVelocityY, decreaseStartVelocityY, maxVelocityX, startY, actualY, jumping, falling, crashedSpike);
    }

    @Override
    public String toString() {
        return "Player{" +
                "startVelocityY=" + startVelocityY +
                ", decreaseStartVelocityY=" + decreaseStartVelocityY +
                ", maxVelocityX=" + maxVelocityX +
                ", startY=" + startY +
                ", actualY=" + actualY +
                ", jumping=" + jumping +
                ", falling=" + falling +
                ", crashedSpike=" + crashedSpike +
                '}';
    }
}
