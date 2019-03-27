package jumper.model;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Player extends Rect {
    public Player(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public Player(double x, double y, double width, double height, Color color) {
        super(x, y, width, height, color);
    }

    //---------------------Fields
    //------------- Engine-hez kell ------------------------------------------------
    public static double playerWidth = 100.0;
    public static double playerHeight = 100.0;
    private double startVelocityY = -250.0;
    private final double decreaseStartVelocityY = 10.0;
    //private final double increaseVelocityX = 15.0;
    //private final double maxVelocityX = 120.0;
    private final double maxVelocityX = 100.0;
    private double startY;
    private double actualY;
    private boolean jumping = false, falling = false;

    public boolean isFalling() {
        return falling;
    }

    public void setFalling(boolean falling) {
        this.falling = falling;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getActualY() {
        return actualY;
    }

    public void setActualY(double actualY) {
        this.actualY = actualY;
    }

    public double getStartVelocityY() {
        return startVelocityY;
    }

    public void setStartVelocityY(double startVelocityY) {
        this.startVelocityY = startVelocityY;
    }

    public double getDecreaseStartVelocityY() {
        return decreaseStartVelocityY;
    }

    /*public double getIncreaseVelocityX() {
        return increaseVelocityX;
    }*/

    public double getMaxVelocityX() {
        return maxVelocityX;
    }

    public boolean isJumping() {
        return this.jumping;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    //---------------------------------------------------------------------------------
    public boolean isStandingOnStartLine() {
        return standingOnStartLine;
    }

    public void setStandingOnStartLine(boolean standingOnStartLine) {
        this.standingOnStartLine = standingOnStartLine;
    }

    private boolean standingOnStartLine = true;
    private final double moveSpeed = 100.0;

    private boolean inAir;

    private Point2D oldPosition;

    //---------------------Getters and Setters
    public void setInAir(boolean value) {
        this.inAir = value;
    }

    public boolean getInAir() {
        return this.inAir;
    }

    public Point2D getOldPosition() {
        return oldPosition;
    }

    public void setOldPosition(Point2D oldPosition) {

        this.oldPosition = new Point2D(oldPosition.getX(), oldPosition.getY());
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }
}
