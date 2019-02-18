package jumper.Controllers;

import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import jumper.Model.FallingRectangle;
import jumper.Model.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameFirstLevelController
{
    private Canvas firstLevelCanvas;
    private boolean paused = false;
    private Player player;
    private List<FallingRectangle> enemy;
    private AnimationTimer timer;
    private Line startLine;
    private boolean rightPressed = false, leftPressed = false, upPressed = false;
    private List<Line> borders;
    private Scene gameScene;

    public void init(AnchorPane ap)
    {
        initUI(ap);
        //TODO: add falling objects
        initObjects();
        keyListenerFirstLevel();
        initScenes();
        paused = false;
        run();
    }

    private void initScenes()
    {
        gameScene = firstLevelCanvas.getScene();
    }

    private void initObjects()
    {
        startLine = new Line(0, (firstLevelCanvas.getHeight() - firstLevelCanvas.getHeight() / 50), firstLevelCanvas.getWidth(),
                (firstLevelCanvas.getHeight() - firstLevelCanvas.getHeight() / 50));
        startLine.setStrokeWidth(3.0);
        startLine.setStroke(Color.BLACK);
        borders = new ArrayList<>(2);
        borders.add(new Line(1.5, startLine.getStartY() - 1.5, 1.5, 0));
        borders.add(new Line(startLine.getEndX() - 1.5, startLine.getEndY() - 1.5, startLine.getEndX() - 1.5, 0));
        for (var line : borders)
        {
            line.setStrokeWidth(3.0);
            line.setStroke(Color.BLACK);
        }
        if (player == null)
        {
            player = new Player(firstLevelCanvas.getWidth() / 2 - 50, startLine.getStartY() - 100, 100, 100);
        }
        player.setColor(Color.BLUE);
        enemy = new ArrayList<>();
    }

    private void initUI(AnchorPane ap)
    {
        Stage stage = (Stage) ap.getScene().getWindow();
        Scene scene = stage.getScene();
        firstLevelCanvas = new Canvas(scene.getWidth(), scene.getHeight());
        ap.getChildren().add(firstLevelCanvas);
    }

    private void draw()
    {
        var gc = firstLevelCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, firstLevelCanvas.getWidth(), firstLevelCanvas.getHeight());
        for (var rect : enemy)
        {
            drawRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), rect.getColor());
        }
        drawRect(player.getX(), player.getY(), player.getWidth(), player.getHeight(), player.getColor());
        drawLine(startLine);
        for (var line : borders)
        {
            drawLine(line);
        }
    }

    private void drawLine(Line line)
    {
        var gc = firstLevelCanvas.getGraphicsContext2D();
        gc.setStroke(line.getStroke());
        gc.setLineWidth(line.getStrokeWidth());
        gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
    }

    private void drawRect(double minX, double minY, double width, double height, Color color)
    {
        var gc = firstLevelCanvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(minX, minY, width, height);
    }


    private void keyListenerFirstLevel()
    {
        var firstPEH = new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent keyEvent)
            {
                var kc = keyEvent.getCode();
                if (kc == KeyCode.RIGHT && !paused)
                {
                    player.setVelocityX(player.getMoveSpeed());
                    rightPressed = true;
                }
                if (kc == KeyCode.UP && !paused && !upPressed && !player.isMoving())
                {
                    player.addVelocityY(-player.getMoveSpeed() * 10);
                    upPressed = true;
                    player.setMoving(true);
                }
                if (kc == KeyCode.LEFT && !paused)
                {
                    player.setVelocityX(-player.getMoveSpeed());
                    leftPressed = true;
                }
                if (kc == KeyCode.ESCAPE)
                {
                    gameScene = firstLevelCanvas.getScene();
                    player.setOldVelocityX(player.getVelocityX());
                    player.setOldVelocityY(player.getVelocityY());
                    player.setVelocityX(0.0);
                    player.setVelocityY(0.0);
                    paused = true;
                    leftPressed = false;
                    upPressed = false;
                    rightPressed = false;
                    timer.stop();
                    try
                    {
                        var stage = (Stage) firstLevelCanvas.getScene().getWindow();
                        var fl = new FXMLLoader(getClass().getClassLoader().getResource("Pause.fxml"));
                        var pauseC = new PauseController(GameFirstLevelController.this);
                        fl.setController(pauseC);
                        var ap = (AnchorPane) fl.load();
                        var pauseScene = new Scene(ap);
                        stage.setScene(pauseScene);
                        stage.show();
                        pauseC.keyListenerPause();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };

        var firstREH = new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent keyEvent)
            {
                var kc = keyEvent.getCode();
                if (kc == KeyCode.RIGHT)
                {
                    rightPressed = false;
                }
                if (kc == KeyCode.LEFT)
                {
                    leftPressed = false;
                }
                if (kc == KeyCode.UP)
                {
                    upPressed = false;
                }
            }
        };

        firstLevelCanvas.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, firstPEH);
        firstLevelCanvas.getScene().removeEventHandler(KeyEvent.KEY_RELEASED, firstREH);

        firstLevelCanvas.getScene().addEventHandler(KeyEvent.KEY_PRESSED, firstPEH);
        firstLevelCanvas.getScene().addEventHandler(KeyEvent.KEY_RELEASED, firstREH);
    }

    protected void letsContinue(AnchorPane ap)
    {
        player.setVelocityX(player.getOldVelocityX());
        player.setVelocityY(player.getOldVelocityY());
        player.setOldVelocityX(0.0);
        player.setOldVelocityY(0.0);
        paused = false;
        this.init(ap);
        timer.start();
    }

    private void updatePlayer(double time)
    {
        player.setX(player.getX() + player.getVelocityX() * time);
        player.setY(player.getY() + player.getVelocityY() * time);
    }

    private void update(double time)
    {
        updatePlayer(time);
        draw();

        if (!rightPressed && !leftPressed && !paused)
        {
            if (player.getVelocityX() > 10e-6)
            {
                if (player.getVelocityX() <= 1.0)
                {
                    player.setVelocityX(0.0);
                } else
                {
                    player.addVelocityX(-1.0);
                }
            } else if (player.getVelocityX() <= 10e-6)
            {
                if (player.getVelocityX() > -1.0)
                {
                    player.setVelocityX(0.0);
                } else
                {
                    player.addVelocityX(1.0);
                }
            }
        }
        if (player.isMoving() && !paused)
        {
            if (player.getY() < startLine.getStartY() - player.getHeight())
            {
                player.addVelocityY(0.91);
            } else if (player.getY() >= startLine.getStartY() - player.getHeight())
            {
                player.setY(startLine.getStartY() - player.getHeight());
                player.setVelocityY(0.0);
                player.setMoving(false);
            }
        }
        if ((player.getX() + player.getWidth()) >= borders.get(1).getStartX())
        {
            player.setX(borders.get(1).getStartX() - player.getWidth());
        } else if (player.getX() <= borders.get(0).getStartX())
        {
            player.setX(borders.get(0).getStartX());
        }

    }

    public void run()
    {
        timer = new AnimationTimer()
        {
            @Override
            public void handle(long l)
            {
                var delta = 2 * 10e-4;  //if it were time based, it wouldn't be a constant
                update(delta);
            }
        };
        timer.start();
    }


}
