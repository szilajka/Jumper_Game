package jumper.Controllers;

import javafx.animation.*;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import jumper.Model.FallingRectangle;
import jumper.Model.Player;

import java.util.ArrayList;
import java.util.List;

public class GameFirstLevelController {

    private Canvas firstLevelCanvas;
    private boolean paused = false;
    private Player player;
    private List<FallingRectangle> enemy;
    private AnimationTimer timer;
    private Line startLine;
    private boolean rightPressed = false, leftPressed = false, upPressed = false;

    public void init(AnchorPane ap) {
        Stage stage = (Stage) ap.getScene().getWindow();
        Scene scene = stage.getScene();
        firstLevelCanvas = new Canvas(scene.getWidth(), scene.getHeight());
        ap.getChildren().add(firstLevelCanvas);
        startLine = new Line(0, (firstLevelCanvas.getHeight() - firstLevelCanvas.getHeight() / 50), firstLevelCanvas.getWidth(),
                (firstLevelCanvas.getHeight() - firstLevelCanvas.getHeight() / 50));
        drawLine();
        player = new Player(firstLevelCanvas.getWidth() / 2 - 50, startLine.getStartY() - 100, 100, 100);
        player.setColor(Color.BLUE);
        drawRect(player.getX(), player.getY(), player.getWidth(), player.getHeight(), player.getColor());
        enemy = new ArrayList<>();
        keyListener();
        paused = false;
        run();
    }

    private void draw() {
        var gc = firstLevelCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, firstLevelCanvas.getWidth(), firstLevelCanvas.getHeight());
        for (var rect : enemy) {
            drawRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), rect.getColor());
        }
        drawRect(player.getX(), player.getY(), player.getWidth(), player.getHeight(), player.getColor());
        drawLine();
    }

    private void drawLine() {
        var gc = firstLevelCanvas.getGraphicsContext2D();
        gc.strokeLine(startLine.getStartX(), startLine.getStartY(), startLine.getEndX(), startLine.getEndY());
        gc.clearRect(startLine.getStartX(), startLine.getStartY() + 2, firstLevelCanvas.getWidth(), firstLevelCanvas.getHeight());
    }

    private void drawRect(double minX, double minY, double width, double height, Color color) {
        var gc = firstLevelCanvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(minX, minY, width, height);
    }


    private void keyListener() {
        firstLevelCanvas.getScene().setOnKeyPressed(keyEvent -> {
            var kc = keyEvent.getCode();
            if (kc == KeyCode.RIGHT) {
                player.addVelocityX(player.getMoveSpeed());
                rightPressed = true;
            }
            if (kc == KeyCode.UP && !paused && !upPressed && !player.isMoving()) {
                player.addVelocityY(-player.getMoveSpeed() * 10);
                upPressed = true;
                player.setMoving(true);
            }
            if (kc == KeyCode.LEFT && !paused) {
                player.addVelocityX(-player.getMoveSpeed());
                leftPressed = true;
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

        firstLevelCanvas.getScene().setOnKeyReleased(keyEvent -> {
            var kc = keyEvent.getCode();
            if (kc == KeyCode.RIGHT) {
                rightPressed = false;
            }
            if (kc == KeyCode.LEFT && !paused) {
                leftPressed = false;
            }
            if (kc == KeyCode.UP && !paused) {
                upPressed = false;
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

    private void updatePlayer(double time) {
        player.setX(player.getX() + player.getVelocityX() * time);
        player.setY(player.getY() + player.getVelocityY() * time);
    }

    private void update(double time) {
        updatePlayer(time);
        //TODO: add falling objects
        draw();

        if (!rightPressed && !leftPressed) {
            if (player.getVelocityX() > 10e-6) {
                if (player.getVelocityX() <= 1.0) {
                    player.setVelocityX(0.0);
                } else {
                    player.addVelocityX(-1.0);
                }
            } else if (player.getVelocityX() <= 10e-6) {
                if (player.getVelocityX() > -1.0) {
                    player.setVelocityX(0.0);
                } else {
                    player.addVelocityX(1.0);
                }
            }
        }
        if (player.isMoving()) {
            if (player.getY() < startLine.getStartY() - player.getHeight()) {
                player.addVelocityY(0.91);
            } else if(player.getY() >= startLine.getStartY() - player.getHeight()){
                player.setY(startLine.getStartY() - player.getHeight());
                player.setVelocityY(0.0);
                player.setMoving(false);
            }
        }


    }

    public void run() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                var delta = 2 * 10e-4;  //if it were time based, it wouldn't be a constant
                update(delta);
            }
        };
        timer.start();
    }

}
