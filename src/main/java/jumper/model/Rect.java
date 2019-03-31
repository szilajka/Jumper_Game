package jumper.model;

/*-
 * #%L
 * jumper_game
 * %%
 * Copyright (C) 2019 Szilárd Németi
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class Rect extends Rectangle {
    private Color color;
    private double velocityX = 0.0;
    private double velocityY = 0.0;
    private double oldVelocityX = 0.0;
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
}
