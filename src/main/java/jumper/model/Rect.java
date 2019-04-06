package jumper.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

/**
 * The superior class of the {@link Player} and the {@link FallingRectangle} classes.
 */
public abstract class Rect extends Rectangle {
    /**
     * The object's color.
     */
    private Color color;
    /**
     * The object's X velocity.
     */
    private double velocityX = 0.0;
    /**
     * The object's Y velocity.
     */
    private double velocityY = 0.0;
    /**
     * The object's X velocity before pausing.
     */
    private double oldVelocityX = 0.0;
    /**
     * The object's Y velocity before pausing.
     */
    private double oldVelocityY = 0.0;

    /**
     * {@code Rect} constructor.
     *
     * @param x      {@code Rect}'s upper left X coordinate
     * @param y      {@code Rect}'s upper left Y coordinate
     * @param width  {@code Rect}'s width
     * @param height {@code Rect}'s height
     */
    public Rect(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.color = Color.BLACK;
    }

    /**
     * {@code Rect} constructor.
     *
     * @param x      {@code Rect}'s upper left X coordinate
     * @param y      {@code Rect}'s upper left Y coordinate
     * @param width  {@code Rect}'s width
     * @param height {@code Rect}'s height
     * @param color  {@code Rect}'s {@link Color}
     */
    public Rect(double x, double y, double width, double height, Color color) {
        super(x, y, width, height);
        this.color = color;
    }

    /**
     * Sets the {@code Rect}'s {@link Color}.
     *
     * @param color The {@code Color} of the {@code Rect}
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Gets the {@code Rect}'s {@link Color}.
     *
     * @return The {@code Rect}'s {@code color}
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Gets the {@code Rect}'s X velocity.
     *
     * @return The {@code Rect}'s X velocity
     */
    public double getVelocityX() {
        return velocityX;
    }

    /**
     * Sets the {@code Rect}'s X velocity.
     *
     * @param velocityX the value to be passed
     */
    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    /**
     * Gets the {@code Rect}'s Y velocity.
     *
     * @return The {@code Rect}'s Y velocity
     */
    public double getVelocityY() {
        return velocityY;
    }

    /**
     * Sets the {@code Rect}'s Y velocity.
     *
     * @param velocityY the value to be passed
     */
    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    /**
     * Gets the {@code Rect}'s old X velocity.
     * <p>
     * This is only used when the game continues.
     *
     * @return the {@code Rect}'s X velocity before the has stopped
     */
    public double getOldVelocityX() {
        return oldVelocityX;
    }

    /**
     * Sets the {@code Rect}'s old X velocity.
     * <p>
     * This is only used when the game stops.
     *
     * @param oldVelocityX the actual value of the X velocity
     */
    public void setOldVelocityX(double oldVelocityX) {
        this.oldVelocityX = oldVelocityX;
    }

    /**
     * Gets the {@code Rect}'s old Y velocity.
     * <p>
     * This is only used when the game continues.
     *
     * @return the {@code Rect}'s Y velocity before the has stopped
     */
    public double getOldVelocityY() {
        return oldVelocityY;
    }

    /**
     * Sets the {@code Rect}'s old Y velocity.
     * <p>
     * This is only used when the game stops.
     *
     * @param oldVelocityY the actual value of the Y velocity
     */
    public void setOldVelocityY(double oldVelocityY) {
        this.oldVelocityY = oldVelocityY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Rect rect = (Rect) o;
        return Double.compare(rect.velocityX, velocityX) == 0 &&
            Double.compare(rect.velocityY, velocityY) == 0 &&
            Double.compare(rect.oldVelocityX, oldVelocityX) == 0 &&
            Double.compare(rect.oldVelocityY, oldVelocityY) == 0 &&
            Objects.equals(color, rect.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, velocityX, velocityY, oldVelocityX, oldVelocityY);
    }

    @Override
    public String toString() {
        return "Rect{" +
            "color=" + color +
            ", velocityX=" + velocityX +
            ", velocityY=" + velocityY +
            ", oldVelocityX=" + oldVelocityX +
            ", oldVelocityY=" + oldVelocityY +
            '}';
    }
}
