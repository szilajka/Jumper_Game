package jumper.Controllers;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameFirstLevelController {

    private static Canvas firstLevelCanvas;
    public static AnchorPane gameFirstLevelAp;
    private static GraphicsContext graphicsContext;

    public static void showText(AnchorPane ap) {
        var gc = firstLevelCanvas.getGraphicsContext2D();
        gc.strokeText("Helo a birodalomban!", 50, 100);

    }

    public static void init(AnchorPane ap){
        Stage stage = (Stage) ap.getScene().getWindow();
        Scene scene = stage.getScene();
        firstLevelCanvas = new Canvas(scene.getWidth(), scene.getHeight());
        ap.getChildren().add(firstLevelCanvas);
    }

}
