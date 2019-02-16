package jumper.Model;

import javafx.geometry.Point2D;

import java.util.Optional;

public class Player extends Rect {
    /**
     * Public constructor for Player class.
     * @param x upper left x coordinate of the square
     * @param y upper left y coordinate of the square
     * @param width width of the square
     * @param height height of the square
     */
    public Player(double x, double y, double width, double height){
        super(x, y, width, height, Optional.empty());
    }

    /**
     * Public constructor for Player class
     * @param upperLeft Point2D object to the upper left coordinate of the square
     * @param width width of the square
     * @param height height of the square
     */
    public Player(Point2D upperLeft, double width, double height){
        super(upperLeft.getX(), upperLeft.getY(), width, height, Optional.empty());
    }

    //---------------------Fields

    private final double moveSpeed = 50.0;

    private boolean inAir;

    private Point2D oldPosition;

    //---------------------Getters and Setters
    public void setInAir(boolean value){
        this.inAir = value;
    }

    public boolean getInAir(){
        return this.inAir;
    }

    public Point2D getOldPosition() {
        return oldPosition;
    }

    public void setOldPosition(Point2D oldPosition) {

        this.oldPosition = new Point2D(oldPosition.getX(), oldPosition.getY());
    }

    public double getMoveSpeed(){
        return moveSpeed;
    }
}
