package jumper.Model;

import javafx.scene.paint.Color;

/**
 * Rectangle abstract class
 * x and y are the upper left position of the rectangle.
 * width and height are the rectangle's width and height.
 */

public abstract class Rectangle {
    public double width;
    public double height;
    public Point upperLeft;
    public Color color;

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public void setPosition(double x, double y) {
        this.upperLeft = new Point(x, y);
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWidthAndHeight(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public Point getPosition() {
        return this.upperLeft;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }
}