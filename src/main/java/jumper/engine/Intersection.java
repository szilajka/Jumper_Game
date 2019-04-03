package jumper.engine;

import jumper.model.Rect;

import static jumper.helpers.GameEngineHelper.tolerance;

/**
 * A helper class that helps to decide whether an object intersected with another object.
 */
public class Intersection {

    /**
     * Decides whether the first object's left side intersected with the second object's right side.
     *
     * @param firstRect  the object which left side will be examined
     * @param secondRect the object which right side will be examined
     * @return the first object's left side collided with the second object's right side or not
     */
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

    /**
     * Decides whether the first object's left side intersected with the second object's right side.
     *
     * @param firstRect  the object which left side will be examined
     * @param secondRect the object which right side will be examined
     * @param velocityX  the tolerance to calculate with
     * @return the first object's left side collided with the second object's right side or not
     */
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

    /**
     * Decides whether the first object's right side intersected with the second object's left side.
     *
     * @param firstRect  the object which right side will be examined
     * @param secondRect the object which left side will be examined
     * @return the first object's right side collided with the second object's left side or not
     */
    public static boolean rightIntersection(Rect firstRect, Rect secondRect) {
        return leftIntersection(secondRect, firstRect);
    }

    /**
     * Decides whether the first object's right side intersected with the second object's left side.
     *
     * @param firstRect  the object which right side will be examined
     * @param secondRect the object which left side will be examined
     * @param velocityX  the tolerance to calculate with
     * @return the first object's right side collided with the second object's left side or not
     */
    public static boolean rightIntersection(Rect firstRect, Rect secondRect, double velocityX) {
        if (velocityX == 0.0) {
            return leftIntersection(secondRect, firstRect);
        } else {
            return leftIntersection(secondRect, firstRect, velocityX);
        }
    }

    /**
     * Decides whether the first object's upper side intersected with the
     * second object's bottom side.
     *
     * @param firstRect  the object which upper side will be examined
     * @param secondRect the object which bottom side will be examined
     * @return the first object's upper side collided with the second object's bottom side or not
     */
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

    /**
     * Decides whether the first object's upper side intersected with the
     * second object's bottom side.
     *
     * @param firstRect  the object which upper side will be examined
     * @param secondRect the object which bottom side will be examined
     * @param velocityY  the tolerance to calculate with
     * @return the first object's upper side collided with the second object's bottom side or not
     */
    public static boolean upIntersection(Rect firstRect, Rect secondRect, double velocityY) {
        if (velocityY == 0.0) {
            return upIntersection(firstRect, secondRect);
        } else {
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

    /**
     * Decides whether the first object's bottom side intersected with the
     * second object's upper side.
     *
     * @param firstRect  the object which bottom side will be examined
     * @param secondRect the object which upper side will be examined
     * @return the first object's bottom side collided with the second object's upper side or not
     */
    public static boolean bottomIntersection(Rect firstRect, Rect secondRect) {
        return upIntersection(secondRect, firstRect);
    }

    /**
     * Decides whether the first object's bottom side intersected with the
     * second object's upper side.
     *
     * @param firstRect  the object which bottom side will be examined
     * @param secondRect the object which upper side will be examined
     * @param velocityY  the tolerance to calculate with
     * @return the first object's bottom side collided with the second object's upper side or not
     */
    public static boolean bottomIntersection(Rect firstRect, Rect secondRect, double velocityY) {
        if (velocityY == 0.0) {
            return upIntersection(secondRect, firstRect);
        } else {
            return upIntersection(secondRect, firstRect, velocityY);
        }
    }

}
