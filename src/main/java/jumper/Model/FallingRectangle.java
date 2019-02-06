package jumper.Model;

import javafx.scene.paint.Color;

public class FallingRectangle extends Rectangle {
    public FallingRectangle(Point upperLeft, double width, double height){
        this.upperLeft = new Point(upperLeft.getX(), upperLeft.getY());
        this.width = width;
        this.height = height;
    }

}
