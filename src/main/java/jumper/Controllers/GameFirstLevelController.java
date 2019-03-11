package jumper.Controllers;

import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Camera;
import javafx.scene.ParallelCamera;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameFirstLevelController extends AbstractController
{
    private static final Logger logger = LogManager.getLogger("GameFirstLevelController");
    private Camera cam;
    private Canvas firstLevelCanvas;
    protected boolean paused = false;
    private Player player;
    private List<FallingRectangle> enemy;
    private AnimationTimer timer;
    private Line startLine;
    private boolean rightPressed = false, leftPressed = false, upPressed = false;
    private List<Line> borders;
    private double extractStartLineWidth;
    private Map<String, ChangeListener<Number>> changeListenerMap;
    private Map<EventType<KeyEvent>, EventHandler<KeyEvent>> keyEventHandlerMap;

    //region Constructor

    public GameFirstLevelController()
    {
        logger.debug("GameFirstLevelController constructor called.");
        changeListenerMap = new HashMap<>();
        keyEventHandlerMap = new HashMap<>();
        cam = new ParallelCamera();
    }

    //endregion Constructor

    //region Initialization

    public void init(AnchorPane ap)
    {
        logger.debug("init() method called.");
        initUI(ap);
        //TODO: add falling objects
        initObjects();
        keyListenerFirstLevel();
        paused = false;
        run();
    }


    private void initObjects()
    {
        try
        {
            logger.debug("initObjects() method called.");
            startLine = new Line(0, (firstLevelCanvas.getHeight() - firstLevelCanvas.getHeight() / 50), firstLevelCanvas.getWidth(),
                    (firstLevelCanvas.getHeight() - firstLevelCanvas.getHeight() / 50));
            startLine.setStrokeWidth(3.0);
            startLine.setStroke(Color.BLACK);
            extractStartLineWidth = startLine.getStrokeWidth() / 2;
            borders = new ArrayList<>(2);
            borders.add(new Line(extractStartLineWidth, 0,
                    extractStartLineWidth, startLine.getStartY() - extractStartLineWidth));
            borders.add(new Line(startLine.getEndX() - extractStartLineWidth, 0,
                    startLine.getEndX() - extractStartLineWidth, startLine.getEndY() - extractStartLineWidth));
            for (var line : borders)
            {
                line.setStrokeWidth(3.0);
                line.setStroke(Color.BLACK);
            }
            if (player == null)
            {
                var stage = Main.getPrimaryStage();
                double width = stage.getWidth() / 10, height = width;
                var x = firstLevelCanvas.getWidth() / 2 - (width / 2);
                var y = startLine.getStartY() - height;
                player = new Player(x, y, width, height);
            }
            player.setColor(Color.BLUE);
            enemy = new ArrayList<>();
            logger.debug("initObjects() method finished. player {}, enemies size: {}", player, enemy.size());
        } catch (Exception ex)
        {
            logger.error("Exception occured in initObjects.", ex);
            errorGoToMainMenu();
        }
    }

    private void initUI(AnchorPane ap)
    {
        try
        {
            logger.debug("initUI() method called.");
            Stage stage = Main.getPrimaryStage();
            Scene scene = stage.getScene();
            firstLevelCanvas = new Canvas(scene.getWidth(), scene.getHeight() * 10);
            ap.getChildren().add(firstLevelCanvas);
            scene.setCamera(cam);
            logger.debug("initUI method finished.");
        } catch (Exception ex)
        {
            logger.error("Error occured in initUI.", ex);
            errorGoToMainMenu();
        }
    }

    //endregion Initialization

    //region Drawing
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

    //endregion Drawing

    //region Key Listener

    protected void removeKeyListener()
    {
        var stage = Main.getPrimaryStage();
        if (keyEventHandlerMap.get(KeyEvent.KEY_PRESSED) != null)
        {
            stage.removeEventHandler(KeyEvent.KEY_PRESSED, keyEventHandlerMap.get(KeyEvent.KEY_PRESSED));
        }
        if (keyEventHandlerMap.get(KeyEvent.KEY_RELEASED) != null)
        {
            stage.removeEventHandler(KeyEvent.KEY_RELEASED, keyEventHandlerMap.get(KeyEvent.KEY_RELEASED));
        }
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
                if (kc == KeyCode.ESCAPE && !paused)
                {
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
                        var stage = Main.getPrimaryStage();
                        var fl = new FXMLLoader(getClass().getClassLoader().getResource("Pause.fxml"));
                        var pauseC = new PauseController(GameFirstLevelController.this);
                        fl.setController(pauseC);
                        var ap = (AnchorPane) fl.load();
                        Scene pauseScene = firstLevelCanvas.getScene();
                        ParallelCamera newCam = new ParallelCamera();
                        newCam.setLayoutY(0);
                        pauseScene.setCamera(newCam);
                        pauseScene.setRoot(ap);
                        setNewAndStageXY(ap, stage);
                        pauseC.resizeOnLoad(oldStageX, oldStageY, changeNewX, changeNewY);
                        pauseC.addResizeListener();
                        pauseC.keyListenerPause();
                        removeKeyListener();
                        removeResizeListener();
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


        removeKeyListener();

        keyEventHandlerMap.put(KeyEvent.KEY_PRESSED, firstPEH);
        keyEventHandlerMap.put(KeyEvent.KEY_RELEASED, firstREH);

        var stage = Main.getPrimaryStage();
        stage.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandlerMap.get(KeyEvent.KEY_PRESSED));
        stage.addEventHandler(KeyEvent.KEY_RELEASED, keyEventHandlerMap.get(KeyEvent.KEY_RELEASED));
    }

    //endregion Key Listener

    //region Resize Methods

    @Override
    protected void resizeOnLoad(Number oldValueX, Number oldValueY, Number newValueX, Number newValueY)
    {
        resizeXAndWidth(oldValueX, newValueX);
        resizeYAndHeight(oldValueY, newValueY);
    }

    @Override
    protected void addResizeListener()
    {
        resize();
    }

    private void removeResizeListener()
    {
        var stage = Main.getPrimaryStage();
        if (changeListenerMap.get("width") != null)
        {
            stage.widthProperty().removeListener(changeListenerMap.get("width"));
        }
        if (changeListenerMap.get("height") != null)
        {
            stage.heightProperty().removeListener(changeListenerMap.get("height"));
        }
    }

    private void resize()
    {
        var widthResize = new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue)
            {
                resizeXAndWidth(oldValue, newValue);
            }
        };

        var heightResize = new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue)
            {
                resizeYAndHeight(oldValue, newValue);
            }
        };


        var stage = Main.getPrimaryStage();

        changeListenerMap.put("width", widthResize);
        changeListenerMap.put("height", heightResize);

        removeResizeListener();

        stage.widthProperty().addListener(changeListenerMap.get("width"));
        stage.heightProperty().addListener(changeListenerMap.get("height"));

    }

    private void resizeXAndWidth(Number oldValue, Number newValue)
    {
        double ratio = newValue.doubleValue() / oldValue.doubleValue();

        firstLevelCanvas.setLayoutX(0);
        firstLevelCanvas.setWidth(firstLevelCanvas.getWidth() * ratio);

        startLine.setStartX(0);
        startLine.setEndX(firstLevelCanvas.getWidth());

        borders.get(0).setStartX(extractStartLineWidth);
        borders.get(0).setEndX(extractStartLineWidth);

        borders.get(1).setStartX(startLine.getEndX() - extractStartLineWidth);
        borders.get(1).setEndX(startLine.getEndX() - extractStartLineWidth);

        player.setX(player.getX() * ratio);
        player.setWidth(player.getWidth() * ratio);

        for (var rect : enemy)
        {
            rect.setX(rect.getX() * ratio);
            rect.setWidth(rect.getWidth() * ratio);
        }
    }

    private void resizeYAndHeight(Number oldValue, Number newValue)
    {
        double ratio = newValue.doubleValue() / oldValue.doubleValue();

        firstLevelCanvas.setLayoutY(0);
        firstLevelCanvas.setHeight(firstLevelCanvas.getHeight() * ratio);

        startLine.setStartY((firstLevelCanvas.getHeight() - firstLevelCanvas.getHeight() / 50));
        startLine.setEndY((firstLevelCanvas.getHeight() - firstLevelCanvas.getHeight() / 50));

        borders.get(0).setStartY(0);
        borders.get(0).setEndY(startLine.getStartY() - extractStartLineWidth);

        borders.get(1).setStartY(0);
        borders.get(1).setEndY(startLine.getEndY() - extractStartLineWidth);

        player.setY(player.getY() * ratio);
        player.setHeight(player.getHeight() * ratio);

        for (var rect : enemy)
        {
            rect.setY(rect.getY() * ratio);
            rect.setHeight(rect.getHeight() * ratio);
        }

    }

    //endregion Resize Methods

    //region Update

    protected void letsContinue(AnchorPane ap)
    {
        try
        {
            player.setVelocityX(player.getOldVelocityX());
            player.setVelocityY(player.getOldVelocityY());
            player.setOldVelocityX(0.0);
            player.setOldVelocityY(0.0);
            paused = false;
            this.init(ap);
            timer.start();
        } catch (Exception ex)
        {
            logger.error("Can not continue the game, due to error.", ex);
            errorGoToMainMenu();
        }
    }

    private void errorGoToMainMenu()
    {
        try
        {
            var stage = Main.getPrimaryStage();
            var fl = new FXMLLoader(getClass().getClassLoader().getResource("MainMenu.fxml"));
            var mainController = new MainMenuController();
            fl.setController(mainController);
            var ap = (AnchorPane) fl.load();
            var mainScene = Main.getPrimaryStage().getScene();
            mainScene.setRoot(ap);
            setNewAndStageXY(ap, stage);
            removeKeyListener();
            removeResizeListener();
            mainController.resizeOnLoad(oldStageX, oldStageY, changeNewX, changeNewY);
            mainController.addResizeListener();
        } catch (IOException io)
        {
            logger.error("MainMenu.fxml not founded, closing application.", io);
            removeKeyListener();
            removeResizeListener();
            Main.getPrimaryStage().close();
        } catch (Exception ex)
        {
            logger.error("Something bad happened, closing application.", ex);
            removeKeyListener();
            removeResizeListener();
            Main.getPrimaryStage().close();
        }
    }

    private void updatePlayer(double time)
    {
        player.setX(player.getX() + player.getVelocityX() * time);
        player.setY(player.getY() + player.getVelocityY() * time);
        var scene = firstLevelCanvas.getScene();
        //Camera follows the player, it is necessary that the canvas must be higher than the scene itself.
        cam.setLayoutY(player.getY() - (scene.getHeight() * 0.8) + player.getHeight());
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
            } else if (player.getLayoutBounds().intersects(startLine.getLayoutBounds()))
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

    //endregion Update

    //region Run

    public void run()
    {
        logger.debug("run() method called.");
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

    //endregion Run

}
