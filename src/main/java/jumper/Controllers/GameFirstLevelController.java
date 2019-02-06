package jumper.Controllers;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jumper.Model.FallingRectangle;
import jumper.Model.Player;
import jumper.Model.Point;

import java.util.ArrayList;
import java.util.List;

public class GameFirstLevelController {

    private static Canvas firstLevelCanvas;
    private static boolean started = false;
    private static Player player;
    private static List<FallingRectangle> enemy;

    public static void init(AnchorPane ap) {
        Stage stage = (Stage) ap.getScene().getWindow();
        Scene scene = stage.getScene();
        firstLevelCanvas = new Canvas(scene.getWidth(), scene.getHeight());
        ap.getChildren().add(firstLevelCanvas);
        player = new Player(firstLevelCanvas.getWidth() / 2 - 100, firstLevelCanvas.getHeight() - 100, 100, 100);
        player.setColor(Color.BLUE);
        drawRect(player.getPosition(), player.getWidth(), player.getHeight(), player.getColor());
        enemy = new ArrayList<>();
        //TODO: add keyEventListener
        /*firstLevelCanvas.setOnKeyPressed(keyEvent -> {

        });*/
        started = true;
    }

    private void draw() {
        var gc = firstLevelCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, firstLevelCanvas.getWidth(), firstLevelCanvas.getHeight());
        for(var rect : enemy){
            drawRect(rect.getPosition(), rect.getWidth(), rect.getHeight(), rect.getColor());
        }
        drawRect(player.getPosition(), player.getWidth(), player.getHeight(), player.getColor());
    }

    private static void drawRect(Point ul, double width, double height, Color color) {
        var gc = firstLevelCanvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(ul.getX(), ul.getY(), width, height);
    }

    private void update(){
        //TODO: keyEventListener changes apply to player and falling objects
        draw();
    }

    public void run() {
        draw();
        while (true) {
            update();
            break;
        }
    }

}
