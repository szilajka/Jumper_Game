package jumper.model;

import javafx.scene.Camera;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import jumper.controllers.Main;
import jumper.helpers.MathH;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class FallingRectangle extends Rect {
    private static final Logger logger = LogManager.getLogger("FallingRectangle");
    private static final double radiusBetween = 50.0;
    private boolean stopped = false;


    public double getStartY() {
        return startY;
    }

    private double startY = 0.0;

    public FallingRectangle(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.startY = y;
        super.setVelocityY(250.0);
    }

    public FallingRectangle(double x, double y, double width, double height, Color color) {
        super(x, y, width, height, color);
        this.startY = y;
        super.setVelocityY(250.0);
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    //region Intersection

    /**
     * If the falling rectangle's left or right side intersects with the rectangle's left or right side.
     *
     * @param rect
     * @return
     */
    public boolean intersectsWithLeftOrRightSide(Rect rect) {
        return this.leftIntersects(rect) || this.rightIntersects(rect);
    }

    public boolean intersectsWithUpperOrBottomSide(Rect rect) {
        return this.upIntersects(rect) || this.downIntersects(rect);
    }

    /**
     * If the falling rectangle's left side intersects with the rectangle's right side.
     *
     * @param rect
     * @return
     */
    public boolean leftIntersects(Rect rect) {
        return Math.abs(MathH.round(this.getX(), 3) - MathH.round(rect.getX() + rect.getWidth(), 3)) <= 2.0
                && this.inHeight(rect) && !this.somethingIsStandingOn(rect);
        //return this.inWidthLeft(rect) && this.inHeight(rect) && !this.somethingIsStandingOn(rect);
    }

    /**
     * If the falling rectangle's right side intersects with the rectangle's left side.
     *
     * @param rect
     * @return
     */
    public boolean rightIntersects(Rect rect) {
        return Math.abs(MathH.round(this.getX() + this.getWidth(), 3) - MathH.round(rect.getX(), 3)) <= 2.0
                && this.inHeight(rect) && !this.somethingIsStandingOn(rect);
        //return this.inWidthRight(rect) && this.inHeight(rect) && !this.somethingIsStandingOn(rect);
    }

    /**
     * If the falling rectangle's upper side intersects with the rectangle's bottom side.
     *
     * @param rect
     * @return
     */
    public boolean upIntersects(Rect rect) {
        return this.inWidth(rect) && this.inHeightUp(rect);
    }

    /**
     * If the falling rectangle's bottom side intersects with the rectangle's upper side.
     *
     * @param rect
     * @return
     */
    public boolean downIntersects(Rect rect) {
        return this.inWidth(rect) && this.inHeightDown(rect);
    }

    /**
     * If something is standing on the falling rectangle
     *
     * @param rect
     * @return
     */
    public boolean getSomethingIsStandingOn(Rect rect) {
        return this.inWidth(rect) && this.somethingIsStandingOn(rect);
    }

    /**
     * If the falling rectangle is standing on something
     *
     * @param rect
     * @return
     */
    public boolean getIsStandingOnSomething(Rect rect) {
        return this.inWidth(rect) && this.isStandingOnSomething(rect);
    }

    /**
     * When the falling rectangle's left side is in the rectangle's width
     *
     * @param rect
     * @return
     */
    private boolean inWidthLeft(Rect rect) {
        return this.inWidthLeft(this.getX(), this.getWidth(), rect);
    }

    private boolean inWidthLeft(double x, double width, Rect rect) {

        /*
         *     +
         * -------->
         *
         *     R
         *  ---------
         * |         |
         * |         |
         * |         |
         * |         |
         *  ---------
         *               F
         *        --------------
         *       |              |
         *        --------------
         *
         * ( R.x <= F.x ) && ( ( R.x + R.w ) >= F.x ) && ( ( R.x + R.w ) <= ( F.x + F.w ) )
         *
         * */

        return MathH.round(rect.getX(), 3) <= MathH.round(x, 3)
                && (MathH.round(rect.getX() + rect.getWidth(), 3)) >= MathH.round(x, 3)
                && MathH.round(rect.getX() + rect.getWidth(), 3) <= MathH.round(x + width, 3);
    }

    /**
     * When the falling rectangle's right side is in the rectangle's width
     *
     * @param rect
     * @return
     */

    private boolean inWidthRight(Rect rect) {
        return this.inWidthRight(this.getX(), this.getWidth(), rect);
    }

    private boolean inWidthRight(double x, double width, Rect rect) {
        /*
         *     +
         * -------->
         *
         *              R
         *           ---------
         *          |         |
         *          |         |
         *          |         |
         *          |         |
         *           ---------
         *       F
         *  --------------
         * |              |
         *  --------------
         *
         * ( F.x <= R.x ) && ( ( F.x + F.w ) >= R.x ) && ( ( F.x + F.w ) <= ( R.x + R.w ) )
         *
         * */

        return MathH.round(x, 3) <= MathH.round(rect.getX(), 3)
                && MathH.round(x + width, 3) >= MathH.round(rect.getX(), 3)
                && MathH.round(x + width, 3) <= MathH.round(rect.getX() + rect.getWidth(), 3);
    }

    /**
     * When the falling rectangle is in the rectangle's width
     *
     * @param rect
     * @return
     */
    private boolean inWidthMiddle(Rect rect) {
        return this.inWidthMiddle(this.getX(), this.getWidth(), rect);
    }

    private boolean inWidthMiddle(double x, double width, Rect rect) {
        /*
         *     +
         * -------->
         *
         *        R
         *     ---------
         *    |         |
         *    |         |
         *    |         |
         *    |         |
         *     ---------
         *        F
         *  ----------------
         * |                |
         *  ----------------
         *
         * F.x <= R.x && F.x + F.w >= R.x + R.w
         *
         * */

        return MathH.round(x, 3) <= MathH.round(rect.getX(), 3)
                && MathH.round(x + width, 3) >= MathH.round(rect.getX() + rect.getWidth(), 3);
    }

    private boolean inWidth(Rect rect) {
        return this.inWidthLeft(rect) || this.inWidthMiddle(rect) || this.inWidthRight(rect);
    }

    /**
     * When the falling rectangle is above the rect.
     *
     * @param rect
     * @return
     */
    private boolean inHeightUp(Rect rect) {
        return this.inHeightUp(this.getY(), this.getHeight(), rect);
    }

    private boolean inHeightUp(double y, double height, Rect rect) {
        /*
         *
         *    R
         *  -------
         * |       |
         * |       |    F
         * |       |---------------
         *  -------|               |
         *         |               |
         *          ---------------
         *
         * ( R.y <= F.y ) && ( ( R.y + R.h ) >= F.y ) && ( ( R.y + R.h ) <= ( F.y + F.h ) )
         *
         * */

        return MathH.round(rect.getY(), 3) <= MathH.round(y, 3)
                && MathH.round(rect.getY() + rect.getHeight(), 3) >= MathH.round(y, 3)
                && MathH.round(rect.getY() + rect.getHeight(), 3) <= MathH.round(y + height, 3);
    }

    /**
     * If some shape is standing on the falling rectangle.
     *
     * @param rect
     * @return
     */
    private boolean somethingIsStandingOn(Rect rect) {
        return this.somethingIsStandingOn(rect.getY(), rect.getHeight(), this);
    }


    private boolean somethingIsStandingOn(double y, double height, Rect rect) {
        var ret = MathH.round(y + height, 3) <= MathH.round(rect.getY() + 2.0, 3)
                && MathH.round(y + height, 3) >= MathH.round(rect.getY() - 2.0, 3);
        if (ret) {
            return true;
        } else {
            return false;
        }
        //return MathH.round(y + height, 3) <= MathH.round(rect.getY() + 0.5, 3)
        //        && MathH.round(y + height, 3) >= MathH.round(rect.getY() - 0.5, 3);
    }

    /**
     * If the falling rectangle is standing on some shape.
     *
     * @param rect
     * @return
     */
    private boolean isStandingOnSomething(Rect rect) {
        return this.somethingIsStandingOn(this.getY(), this.getHeight(), rect);
    }

    /**
     * When the falling rectangle is under the rect
     *
     * @param rect
     * @return
     */
    private boolean inHeightDown(Rect rect) {
        return this.inHeightDown(this.getY(), this.getHeight(), rect);
    }

    private boolean inHeightDown(double y, double height, Rect rect) {
        /*
         *              F
         *          ---------------
         *    R    |               |
         *  -------|               |
         * |       |---------------
         * |       |
         * |       |
         *  -------
         *
         * ( F.y <= R.y ) && ( ( F.y + F.h ) >= R.y ) && ( ( F.y + F.h ) <= ( R.y + R.h ) )
         *
         */

        return MathH.round(y, 3) <= MathH.round(rect.getY(), 3)
                && MathH.round(y + height, 3) >= MathH.round(rect.getY(), 3)
                && MathH.round(y + height, 3) <= MathH.round(rect.getY() + rect.getHeight(), 3);
    }

    /**
     * When the falling rectangle's height is between the rectangle's height.
     *
     * @param rect
     * @return
     */
    private boolean inHeightMiddle(Rect rect) {
        return this.inHeightMiddle(this.getY(), this.getHeight(), rect);
    }

    private boolean inHeightMiddle(double y, double height, Rect rect) {
        /*
         *    R
         *   ------    F
         *  |      |-------    |
         *  |      |       |   |  +
         *  |      |-------   \ /
         *   ------
         *
         * ( R.y <= F.y ) && ( ( R.y + R.h ) >= ( F.y + F.h ) )
         *
         * */

        return MathH.round(rect.getY(), 3) <= MathH.round(y, 3)
                && MathH.round(rect.getY() + rect.getHeight(), 3) >= MathH.round(y + height, 3);
    }

    private boolean inHeight(Rect rect) {
        return this.inHeightMiddle(rect) || this.inHeightDown(rect) || this.inHeightUp(rect);

    }

    //endregion Intersection

    //region Static Methods

    public static FallingRectangle generateFallingRectangle(Player player, FallingRectangle lastRect, Camera cam) {
        try {
            logger.debug("generateFallingRectangle() method called.");
            var sceneWidth = Main.getPrimaryStage().getScene().getWidth();
            double x = 0.0, y, width, height;
            Color color;

            width = player.getWidth() * 1.2;
            height = player.getHeight() * 0.3;
            color = Color.AQUAMARINE;

            if (lastRect == null) {
                y = Math.floor(cam.getLayoutY() - 100);
                x = Math.floor(new Random().nextDouble() * (sceneWidth - width));
            } else {
                y = Math.floor(lastRect.getStartY() - 50);
                double remainingSpace = sceneWidth - lastRect.getX() - lastRect.getWidth();
                if (remainingSpace < lastRect.getWidth() * 1.5) {
                    boolean xIsCorrect = false;
                    for (int i = 0; i < 3; i++) {
                        x = Math.floor(new Random().nextDouble() * lastRect.getX());
                        if (x + lastRect.getWidth() < lastRect.getX()) {
                            xIsCorrect = true;
                            break;
                        }
                    }
                    if (!xIsCorrect) {
                        x = Math.floor(lastRect.getX() - lastRect.getWidth() - 50);
                    }
                } else {
                    x = Math.floor(new Random().nextDouble() * (remainingSpace) + lastRect.getX() + lastRect.getWidth());
                }
            }

            return new FallingRectangle(MathH.round(x, 3), y, width, height, color);


        } catch (NullPointerException npEx) {
            logger.error("Some nullpointer exception, maybe no scene.", npEx);
            return null;
        }
    }

    public static boolean shouldStopFallingRectangle(Player player, FallingRectangle fRect) {
        if (!fRect.isStopped()) {
            if (MathH.round(fRect.getY(), 3) >= MathH.round(player.getY() + player.getHeight(), 3)
                    && MathH.round(player.getX() - radiusBetween, 3) <= MathH.round(fRect.getX() + fRect.getWidth(), 3)
                    && MathH.round(player.getX() + radiusBetween, 3) >= MathH.round(fRect.getX(), 3)) {
                return true;
            }

            return false;
        }

        return true;

    }

    public static boolean shouldDestroyFallingRectangle(FallingRectangle fRect, Line startLine) {
        if (fRect.getLayoutBounds().intersects(startLine.getLayoutBounds())) {
            return true;
        }

        return false;
    }

    public static boolean shouldDestroyFallingRectangle(FallingRectangle rect1, Rect rect2) {
        return rect1.intersectsWithLeftOrRightSide(rect2) || rect1.intersectsWithUpperOrBottomSide(rect2);
    }

    public static FallingRectangle whichFallingRectangleShouldBeDestroyed(FallingRectangle fRect1, FallingRectangle fRect2) {
        if (shouldDestroyFallingRectangle(fRect1, fRect2)) {
            return fRect1;
        } else if (shouldDestroyFallingRectangle(fRect2, fRect1)) {
            return fRect2;
        }

        return null;
    }

    //endregion Static Methods

}
