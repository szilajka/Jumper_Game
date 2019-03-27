package jumper.engine;

import jumper.model.Rect;

import static jumper.helpers.MathHelper.tolerance;

public class Intersection {
    //Tűréshatár/Hibahatár

    public static boolean leftIntersection(Rect firstRect, Rect secondRect) {
        boolean whileSecondYisHigher = firstRect.getY() + tolerance >= secondRect.getY() &&
                firstRect.getY() + tolerance <= secondRect.getY() + secondRect.getHeight();

        boolean afterFirstYisHigher = firstRect.getY() + tolerance <= secondRect.getY() &&
                firstRect.getY() + firstRect.getHeight() - tolerance >= secondRect.getY();

        double secondRectRight = secondRect.getX() + secondRect.getWidth();

        boolean xPosMatching = Math.abs(firstRect.getX() - (secondRectRight)) <= tolerance;

        return (whileSecondYisHigher || afterFirstYisHigher)
                && xPosMatching;
    }

    public static boolean leftIntersection(Rect firstRect, Rect secondRect, double velocityX) {
        if (velocityX == 0.0) {
            return leftIntersection(firstRect, secondRect);
        } else {
            boolean whileSecondYisHigher = firstRect.getY() + tolerance >= secondRect.getY() &&
                    firstRect.getY() + tolerance <= secondRect.getY() + secondRect.getHeight();

            boolean afterFirstYisHigher = firstRect.getY() + tolerance <= secondRect.getY() &&
                    firstRect.getY() + firstRect.getHeight() - tolerance >= secondRect.getY();

            double secondRectRight = secondRect.getX() + secondRect.getWidth();

            boolean xPosMatching = Math.abs(firstRect.getX() - (secondRectRight)) <= velocityX;

            return (whileSecondYisHigher || afterFirstYisHigher)
                    && xPosMatching;
        }
    }

    public static boolean rightIntersection(Rect firstRect, Rect secondRect) {
        return leftIntersection(secondRect, firstRect);
    }

    public static boolean rightIntersection(Rect firstRect, Rect secondRect, double velocityX) {
        if (velocityX == 0.0) {
            return leftIntersection(secondRect, firstRect);
        } else {
            return leftIntersection(secondRect, firstRect, velocityX);
        }
    }

    public static boolean upIntersection(Rect firstRect, Rect secondRect) {
        boolean afterFirstXisHigher = firstRect.getX() + tolerance >= secondRect.getX() &&
                firstRect.getX() + tolerance <= secondRect.getX() + secondRect.getWidth();

        boolean whileSecondXisHigher = firstRect.getX() + tolerance <= secondRect.getX() &&
                firstRect.getX() + firstRect.getWidth() - tolerance >= secondRect.getX();

        double secondRectBottom = secondRect.getY() + secondRect.getHeight();

        boolean yPosMatching = Math.abs(firstRect.getY() - (secondRectBottom)) <= tolerance;

        boolean yPosIn = firstRect.getY() <= secondRectBottom
                && firstRect.getY() + firstRect.getHeight() >= secondRectBottom;

        return (whileSecondXisHigher || afterFirstXisHigher)
                && (yPosMatching || yPosIn);
    }

    public static boolean upIntersection(Rect firstRect, Rect secondRect, double velocityY) {
        if(velocityY == 0.0){
            return  upIntersection(firstRect, secondRect);
        }
        else {
            boolean afterFirstXisHigher = firstRect.getX() + tolerance >= secondRect.getX() &&
                    firstRect.getX() + tolerance <= secondRect.getX() + secondRect.getWidth();

            boolean whileSecondXisHigher = firstRect.getX() + tolerance <= secondRect.getX() &&
                    firstRect.getX() + firstRect.getWidth() - tolerance >= secondRect.getX();

            double secondRectBottom = secondRect.getY() + secondRect.getHeight();

            boolean yPosMatching = Math.abs(firstRect.getY() - (secondRectBottom)) <= velocityY;

            boolean yPosIn = firstRect.getY() <= secondRectBottom
                    && firstRect.getY() + firstRect.getHeight() >= secondRectBottom;

            return (whileSecondXisHigher || afterFirstXisHigher)
                    && (yPosMatching || yPosIn);
        }
    }

    public static boolean bottomIntersection(Rect firstRect, Rect secondRect) {
        return upIntersection(secondRect, firstRect);
    }

    public static boolean bottomIntersection(Rect firstRect, Rect secondRect, double velocityY) {
        if (velocityY == 0.0) {
            return upIntersection(secondRect, firstRect);
        } else {
            return upIntersection(secondRect, firstRect, velocityY);
        }
    }

}