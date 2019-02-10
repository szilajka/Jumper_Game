package jumper.Controllers;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jumper.Model.FallingRectangle;
import jumper.Model.Player;
import jumper.Model.Point;

import java.util.ArrayList;
import java.util.List;

public class GameFirstLevelController {

    private Canvas firstLevelCanvas;
    private boolean paused = false;
    private Player player;
    private List<FallingRectangle> enemy;
    private int move = 5;
    private AnimationTimer timer;

    public void init(AnchorPane ap) {
        Stage stage = (Stage) ap.getScene().getWindow();
        Scene scene = stage.getScene();
        firstLevelCanvas = new Canvas(scene.getWidth(), scene.getHeight());
        ap.getChildren().add(firstLevelCanvas);
        player = new Player(firstLevelCanvas.getWidth() / 2 - 50, firstLevelCanvas.getHeight() - 100, 100, 100);
        player.setColor(Color.BLUE);
        drawRect(player.getPosition(), player.getWidth(), player.getHeight(), player.getColor());
        enemy = new ArrayList<>();
        keyListener();
        paused = false;
        run();
    }

    private void draw() {
        var gc = firstLevelCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, firstLevelCanvas.getWidth(), firstLevelCanvas.getHeight());
        for (var rect : enemy) {
            drawRect(rect.getPosition(), rect.getWidth(), rect.getHeight(), rect.getColor());
        }
        drawRect(player.getPosition(), player.getWidth(), player.getHeight(), player.getColor());
    }

    private void drawRect(Point ul, double width, double height, Color color) {
        var gc = firstLevelCanvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(ul.getX(), ul.getY(), width, height);
    }

    private void keyListener() {
        firstLevelCanvas.getScene().setOnKeyPressed(keyEvent -> {
            var kc = keyEvent.getCode();
            if (kc == KeyCode.RIGHT && !paused) {
                player.setPosition(player.getPosition().getX() + move, player.getPosition().getY());
            }
            if (kc == KeyCode.UP && !paused && !player.getInAir()) {
                jump(System.nanoTime());
            }
            if (kc == KeyCode.LEFT && !paused) {
                player.setPosition(player.getPosition().getX() - move, player.getPosition().getY());
            }
            if (kc == KeyCode.DOWN && !paused) {
                player.setPosition(player.getPosition().getX(), player.getPosition().getY() + move);
            }
            if (kc == KeyCode.ESCAPE) {
                if (paused) {
                    paused = false;
                    timer.start();
                } else {
                    paused = true;
                    timer.stop();
                }
            }
        });
    }

    private void jump(long startedTime){
        player.setInAir(true);
        player.setOldPosition(player.getPosition());
        new AnimationTimer(){
            @Override
            public void handle(long l) {
                var delta = l - startedTime;
                if(player.getPosition().getY() > player.getOldPosition().getY() - 100.0){
                    player.setPosition(player.getPosition().getX(), player.getPosition().getY() - (delta * 10e-10));
                }
                else{
                    player.setInAir(false);
                    this.stop();
                }
            }
        }.start();
    }

    private void update() {
        //TODO: add falling objects
        draw();
    }

    public void run() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                update();
            }
        };
        timer.start();
    }

}
