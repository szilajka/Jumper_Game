package jumper.Model;

import javafx.scene.Camera;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import jumper.Controllers.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class FallingRectangle extends Rect
{
    private static final Logger logger = LogManager.getLogger("FallingRectangle");
    private static final double radiusBetween = 50.0;
    private static final double fallRectWidth = 100.0;
    private static final double fallRectHeight = 50.0;
    private boolean stopped = false;

    public FallingRectangle(double x, double y, double width, double height)
    {
        super(x, y, width, height);
        super.setVelocityY(5000.0);
    }

    public FallingRectangle(double x, double y, double width, double height, Color color)
    {
        super(x, y, width, height, color);
        super.setVelocityY(5000.0);
    }

    public boolean isStopped()
    {
        return stopped;
    }

    public void setStopped(boolean stopped)
    {
        this.stopped = stopped;
    }

    //region Intersection

    /**
     * If the falling rectangle's left or right side intersects with the rectangle's left or right side.
     *
     * @param rect
     * @return
     */
    public boolean intersectsWithSide(FallingRectangle this, Rect rect)
    {
        return this.leftIntersects(rect) || this.rightIntersects(rect);
    }

    /**
     * If the falling rectangle's left side intersects with the rectangle's right side.
     *
     * @param rect
     * @return
     */
    public boolean leftIntersects(FallingRectangle this, Rect rect)
    {
        return this.inWidthLeft(rect) && this.inHeight(rect);
    }

    /**
     * If the falling rectangle's right side intersects with the rectangle's left side.
     *
     * @param rect
     * @return
     */
    public boolean rightIntersects(FallingRectangle this, Rect rect)
    {
        return this.inWidthRight(rect) && this.inHeight(rect);
    }

    /**
     * If the falling rectangle's upper side intersects with the rectangle's bottom side.
     *
     * @param rect
     * @return
     */
    public boolean upIntersects(FallingRectangle this, Rect rect)
    {
        return this.inWidth(rect) && this.inHeightUp(rect);
    }

    /**
     * If the falling rectangle's bottom side intersects with the rectangle's upper side.
     *
     * @param rect
     * @return
     */
    public boolean downIntersects(FallingRectangle this, Rect rect)
    {
        return this.inWidth(rect) && this.inHeightDown(rect);
    }

    /**
     * When the falling rectangle's left side is in the rectangle's width
     *
     * @param rect
     * @return
     */
    private boolean inWidthLeft(FallingRectangle this, Rect rect)
    {
        return this.inWidthLeft(this.getX(), this.getWidth(), rect);
    }

    private boolean inWidthLeft(double x, double width, Rect rect)
    {

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

        return rect.getX() <= x && (rect.getX() + rect.getWidth()) >= x
                && (rect.getX() + rect.getWidth()) <= (x + width);
    }

    /**
     * When the falling rectangle's right side is in the rectangle's width
     *
     * @param rect
     * @return
     */

    private boolean inWidthRight(FallingRectangle this, Rect rect)
    {
        return this.inWidthRight(this.getX(), this.getWidth(), rect);
    }

    private boolean inWidthRight(double x, double width, Rect rect)
    {
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

        return x <= rect.getX() && (x + width) >= rect.getX()
                && (x + width) <= (rect.getX() + rect.getWidth());
    }

    /**
     * When the falling rectangle is in the rectangle's width
     *
     * @param rect
     * @return
     */
    private boolean inWidthMiddle(FallingRectangle this, Rect rect)
    {
        return this.inWidthMiddle(this.getX(), this.getWidth(), rect);
    }

    private boolean inWidthMiddle(double x, double width, Rect rect)
    {
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

        return x <= rect.getX() && (x + width) >= (rect.getX() + rect.getWidth());
    }

    private boolean inWidth(FallingRectangle this, Rect rect)
    {
        return this.inWidthLeft(rect) || this.inWidthMiddle(rect) || this.inWidthRight(rect);
    }

    /**
     * When the falling rectangle is above the rect.
     *
     * @param rect
     * @return
     */
    private boolean inHeightUp(FallingRectangle this, Rect rect)
    {
        return this.inHeightUp(this.getY(), this.getHeight(), rect);
    }

    private boolean inHeightUp(double y, double height, Rect rect)
    {
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

        return rect.getY() <= y && (rect.getY() + rect.getHeight()) >= y
                && (rect.getY() + rect.getHeight()) <= (y + height);
    }

    /**
     * When the falling rectangle is under the rect
     *
     * @param rect
     * @return
     */
    private boolean inHeightDown(FallingRectangle this, Rect rect)
    {
        return this.inHeightDown(this.getY(), this.getHeight(), rect);
    }

    private boolean inHeightDown(double y, double height, Rect rect)
    {
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

        return y <= rect.getY() && (y + height) >= rect.getY()
                && (y + height) <= (rect.getY() + rect.getHeight());
    }

    /**
     * When the falling rectangle's height is between the rectangle's height.
     *
     * @param rect
     * @return
     */
    private boolean inHeightMiddle(FallingRectangle this, Rect rect)
    {
        return this.inHeightMiddle(this.getY(), this.getHeight(), rect);
    }

    private boolean inHeightMiddle(double y, double height, Rect rect)
    {
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

        return rect.getY() <= y && (rect.getY() + rect.getHeight()) >= (y + height);
    }

    private boolean inHeight(FallingRectangle this, Rect rect)
    {
        return this.inHeightMiddle(rect) || this.inHeightDown(rect) || this.inHeightUp(rect);

    }

    //endregion Intersection

    //region Static Methods
    public static FallingRectangle generateFallingRectangle(Player player, double ratioX, double ratioY, FallingRectangle lastRect, Camera cam)
    {
        try
        {
            logger.debug("generateFallingRectangle() method called.");
            var scene = Main.getPrimaryStage().getScene();
            double x, leftSpace, rightSpace, width, height, rectX, rectWidth;
            Color color;

            //rectX = player.getX();
            //rectWidth = player.getWidth();
            width = fallRectWidth * ratioX;
            height = fallRectHeight * ratioY;
            //leftSpace = player.getX() - radiusBetween;
            //rightSpace = scene.getWidth() - (player.getX() + player.getWidth()) + radiusBetween;
            color = Color.AQUAMARINE;

            var rand = new Random();
            x = rand.nextDouble() * (scene.getWidth() - width) + 10;
            return new FallingRectangle(x, 0.0, width, height, color);

            /*if (leftSpace > rightSpace)
            {
                x = Math.random() * (leftSpace - width) + (radiusBetween / 2);
                //return new FallingRectangle(x, cam.getLayoutY() - 100.0, width, height, color);
                return new FallingRectangle(x, 0.0, width, height, color);
            }
            else
            {
                x = Math.random() * (rightSpace - width) + (rectX + rectWidth) - (radiusBetween / 2);
                //return new FallingRectangle(x, cam.getLayoutY() - 100.0, width, height, color);
                return new FallingRectangle(x, 0.0, width, height, color);
            }*/

        }
        catch (NullPointerException npEx)
        {
            logger.error("Some nullpointer exception, maybe no scene.", npEx);
            return null;
        }
    }

    public static boolean shouldStopFallingRectangle(Player player, FallingRectangle fRect)
    {
        if (!fRect.isStopped())
        {
            if (((player.getX() - 100 <= fRect.getX() + fRect.getWidth()
                    && fRect.getX() + fRect.getWidth() <= player.getX())
                    || (player.getX() + player.getWidth() <= fRect.getX()
                    && player.getX() + player.getWidth() + 100 >= fRect.getX()))
                    && (player.getY() + player.getHeight() <= fRect.getY() - 20))
            {
                return true;
            }

            return false;
        }

        return true;

    }

    public static boolean shouldDestroyFallingRectangle(FallingRectangle fRect, Line startLine)
    {
        if (fRect.getLayoutBounds().intersects(startLine.getLayoutBounds()))
        {
            return true;
        }

        return false;
    }

    //endregion Static Methods

}
