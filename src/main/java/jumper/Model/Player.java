package jumper.Model;

import javafx.scene.paint.Color;

public class Player extends Rectangle {
    /**
     * Public constructor for Player class.
     * @param x upper left x coordinate of the square
     * @param y upper left y coordinate of the square
     * @param width width of the square
     * @param height height of the square
     */
    public Player(double x, double y, double width, double height){
        this.upperLeft = new Point(x, y);
        this.height = height;
        this.width = width;
    }

    /**
     * Public constructor for Player class
     * @param upperLeft Point object to the upper left coordinate of the square
     * @param width width of the square
     * @param height height of the square
     */
    public Player(Point upperLeft, double width, double height){
        this.upperLeft = new Point(upperLeft);
        this.height = height;
        this.width = width;
    }

    private boolean inAir;

    public void setInAir(boolean value){
        this.inAir = value;
    }

    public boolean getInAir(){
        return this.inAir;
    }

    private Point oldPosition;

    public Point getOldPosition() {
        return oldPosition;
    }

    public void setOldPosition(Point oldPosition) {

        this.oldPosition = new Point(oldPosition);
    }
}
