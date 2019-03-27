package jumper.model;

import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class Rect extends Rectangle {
    private Color color;
    //speed
    private double velocityX = 0.0;
    private double velocityY = 0.0;
    private double oldVelocityX = 0.0;
    private double oldVelocityY = 0.0;
    private boolean isMoving = false;

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public Rect(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.color = Color.BLACK;
    }

    public Rect(double x, double y, double width, double height, Color color) {
        super(x, y, width, height);
        this.color = color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public void addVelocityX(double velocityX) {
        this.velocityX += velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    public void addVelocityY(double velocityY) {
        this.velocityY += velocityY;
    }

    public double getOldVelocityX() {
        return oldVelocityX;
    }

    public void setOldVelocityX(double oldVelocityX) {
        this.oldVelocityX = oldVelocityX;
    }

    public double getOldVelocityY() {
        return oldVelocityY;
    }

    public void setOldVelocityY(double oldVelocityY) {
        this.oldVelocityY = oldVelocityY;
    }

    private Rectangle2D getBoundaries() {
        return new Rectangle2D(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public boolean isCollided(Rect rect) {
        return getBoundaries().intersects(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }
}
