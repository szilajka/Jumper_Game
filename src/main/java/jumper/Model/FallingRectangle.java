package jumper.Model;

import javafx.scene.Camera;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import jumper.Controllers.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        super.setVelocityY(250.0);
    }

    public FallingRectangle(double x, double y, double width, double height, Color color)
    {
        super(x, y, width, height, color);
        super.setVelocityY(250.0);
    }

    public boolean isStopped()
    {
        return stopped;
    }

    public void setStopped(boolean stopped)
    {
        this.stopped = stopped;
    }

    public static FallingRectangle generateFallingRectangle(Player player, double ratioX, double ratioY, FallingRectangle lastRect, Camera cam)
    {
        try
        {
            logger.debug("generateFallingRectangle() method called.");
            var scene = Main.getPrimaryStage().getScene();
            double x, leftSpace, rightSpace, width, height, rectX, rectWidth;
            Color color;

            rectX = player.getX();
            rectWidth = player.getWidth();
            width = fallRectWidth * ratioX;
            height = fallRectHeight * ratioY;
            leftSpace = player.getX() - radiusBetween;
            rightSpace = scene.getWidth() - (player.getX() + player.getWidth()) + radiusBetween;
            color = Color.AQUAMARINE;

            if (leftSpace > rightSpace)
            {
                x = Math.random() * (leftSpace - width) + (radiusBetween / 2);
                return new FallingRectangle(x, cam.getLayoutY() - 100.0, width, height, color);
                //return new FallingRectangle(x, 0.0, width, height, color);
            }
            else
            {
                x = Math.random() * (rightSpace - width) + (rectX + rectWidth) - (radiusBetween / 2);
                return new FallingRectangle(x, cam.getLayoutY() - 100.0, width, height, color);
                //return new FallingRectangle(x, 0.0, width, height, color);
            }

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

}
