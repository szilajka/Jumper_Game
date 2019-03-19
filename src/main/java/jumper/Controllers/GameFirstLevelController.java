package jumper.Controllers;

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
import jumper.Helpers.MathH;
import jumper.Model.FallingRectangle;
import jumper.Model.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class GameFirstLevelController extends AbstractController
{
    private static final Logger logger = LogManager.getLogger("GameFirstLevelController");
    private Camera cam;
    private Canvas firstLevelCanvas;
    protected boolean paused = false;
    private Player player;
    private List<FallingRectangle> enemy;
    private Line startLine;
    private boolean rightPressed = false, leftPressed = false, upPressed = false;
    private List<Line> borders;
    private double extractStartLineWidth;
    private Map<String, ChangeListener<Number>> changeListenerMap;
    private Map<EventType<KeyEvent>, EventHandler<KeyEvent>> keyEventHandlerMap;
    private Timer tr, trGenerate;
    private TimerTask ts, tsGenerate;
    private double ratioX = 1.0, ratioY = 1.0;
    private final double constG = 9.81;

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
            if (enemy == null)
            {
                enemy = Collections.synchronizedList(new ArrayList<FallingRectangle>());
            }
            logger.debug("initObjects() method finished. player {}, enemies size: {}", player, enemy.size());
        }
        catch (Exception ex)
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
        }
        catch (Exception ex)
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
        var sceneHeight = Main.getPrimaryStage().getScene().getHeight();
        for (var rect : enemy)
        {
            if (shouldDrawIt(cam, rect.getY(), rect.getHeight(), sceneHeight))
            {
                drawRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), rect.getColor());
            }
        }
        drawRect(player.getX(), player.getY(), player.getWidth(), player.getHeight(), player.getColor());
        if (shouldDrawIt(cam, startLine.getStartY(), startLine.getStrokeWidth(), sceneHeight))
        {
            drawLine(startLine);
        }
        for (var line : borders)
        {
            drawLine(line);
        }
    }

    private boolean shouldDrawIt(Camera cam, double drawY, double drawHeight, double sceneHeight)
    {
        return ((drawY + drawHeight) >= cam.getLayoutY()) && (drawY <= (cam.getLayoutY() + sceneHeight));
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
                    player.setVelocityX(player.getMoveSpeed() * 10);
                    rightPressed = true;
                }
                if (kc == KeyCode.UP && !paused && !upPressed && !player.isMoving())
                {
                    player.setVelocityY(-Math.sqrt(2 * constG * player.getHeight() * 3.5) * 10);
                    //player.setVelocityY(-Main.getPrimaryStage().getScene().getHeight());
                    upPressed = true;
                    player.setMoving(true);
                }
                if (kc == KeyCode.LEFT && !paused)
                {
                    player.setVelocityX(-player.getMoveSpeed() * 10);
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
                    tr.cancel();
                    trGenerate.cancel();
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
                    }
                    catch (IOException e)
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
                    if (!paused)
                    {
                        player.setOldVelocityX(0.0);
                    }
                }
                if (kc == KeyCode.LEFT)
                {
                    leftPressed = false;
                    if (!paused)
                    {
                        player.setOldVelocityX(0.0);
                    }
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
        ratioX = newValue.doubleValue() / oldValue.doubleValue();

        firstLevelCanvas.setLayoutX(0);
        firstLevelCanvas.setWidth(firstLevelCanvas.getWidth() * ratioX);

        startLine.setStartX(0);
        startLine.setEndX(firstLevelCanvas.getWidth());

        borders.get(0).setStartX(extractStartLineWidth);
        borders.get(0).setEndX(extractStartLineWidth);

        borders.get(1).setStartX(startLine.getEndX() - extractStartLineWidth);
        borders.get(1).setEndX(startLine.getEndX() - extractStartLineWidth);

        player.setX(player.getX() * ratioX);
        player.setWidth(player.getWidth() * ratioX);

        for (var rect : enemy)
        {
            rect.setX(rect.getX() * ratioX);
            rect.setWidth(rect.getWidth() * ratioX);
        }
    }

    private void resizeYAndHeight(Number oldValue, Number newValue)
    {
        ratioY = newValue.doubleValue() / oldValue.doubleValue();

        firstLevelCanvas.setLayoutY(0);
        firstLevelCanvas.setHeight(firstLevelCanvas.getHeight() * ratioY);

        startLine.setStartY((firstLevelCanvas.getHeight() - firstLevelCanvas.getHeight() / 50));
        startLine.setEndY((firstLevelCanvas.getHeight() - firstLevelCanvas.getHeight() / 50));

        borders.get(0).setStartY(0);
        borders.get(0).setEndY(startLine.getStartY() - extractStartLineWidth);

        borders.get(1).setStartY(0);
        borders.get(1).setEndY(startLine.getEndY() - extractStartLineWidth);

        player.setY(MathH.round(player.getY() * ratioY, 3));
        player.setHeight(player.getHeight() * ratioY);

        for (var rect : enemy)
        {
            rect.setY(MathH.round(rect.getY() * ratioY, 3));
            rect.setHeight(rect.getHeight() * ratioY);
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
        }
        catch (Exception ex)
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
        }
        catch (IOException io)
        {
            logger.error("MainMenu.fxml not founded, closing application.", io);
            removeKeyListener();
            removeResizeListener();
            Main.getPrimaryStage().close();
        }
        catch (Exception ex)
        {
            logger.error("Something bad happened, closing application.", ex);
            removeKeyListener();
            removeResizeListener();
            Main.getPrimaryStage().close();
        }
    }

    private void updatePlayer(double time)
    {
        boolean crashedLeft = false, crashedRight = false, standingOn = false, crashedUp = false;
        Iterator<FallingRectangle> frIt = enemy.iterator();
        Map<String, FallingRectangle> crashedRect = new HashMap<>();
        double sceneHeight = Main.getPrimaryStage().getScene().getHeight();
        while (frIt.hasNext())
        {
            var rect = frIt.next();
            if (FallingRectangle.shouldStopFallingRectangle(player, rect))
            {
                rect.setVelocityY(0.0);
                rect.setStopped(true);
            }
            if (FallingRectangle.shouldDestroyFallingRectangle(rect, startLine))
            {
                frIt.remove();
            }
            if (enemy.stream().anyMatch(f -> f != rect && f.getY() < rect.getY() && FallingRectangle.shouldDestroyFallingRectangle(f, rect)))
            {
                frIt.remove();
            }
            if (rect.getY() > (cam.getLayoutY() + (sceneHeight * 1.2)))
            {
                frIt.remove();
            }

            if (rect.leftIntersects(player))
            {
                crashedRight = true;
                crashedRect.put("right", rect);
            }
            if (rect.rightIntersects(player))
            {
                crashedLeft = true;
                crashedRect.put("left", rect);
            }
            if (rect.getSomethingIsStandingOn(player))
            {
                standingOn = true;
                crashedRect.put("down", rect);
            }
            if (rect.getIsStandingOnSomething(player))
            {
                frIt.remove();
                crashedUp = true;
            }
        }


        if ((!crashedLeft && !crashedRight)
                || (crashedLeft && player.getVelocityX() > 0 && !crashedRight)
                || (crashedRight && player.getVelocityX() < 0 && !crashedLeft))
        {
            if (player.getOldVelocityX() != 0.0)
            {
                if ((crashedLeft && player.getOldVelocityX() > 0.0)
                        || (crashedRight && player.getOldVelocityX() < 0.0)
                        || (!crashedLeft && !crashedRight))
                {
                    player.setVelocityX(player.getOldVelocityX());
                }

                player.setOldVelocityX(0.0);
            }
            player.setX(player.getX() + player.getVelocityX() * time);
        }
        else if (crashedLeft && player.getVelocityX() <= 0.0 && !crashedRight)
        {
            var rect = crashedRect.get("left");
            if (rect != null && player.getOldVelocityX() == 0.0)
            {
                player.setX(rect.getX() + rect.getWidth());
                if (leftPressed || rightPressed)
                {
                    player.setOldVelocityX(player.getVelocityX());
                }
                player.setVelocityX(0.0);
            }
        }
        else if (crashedRight && player.getVelocityX() >= 0 && !crashedLeft)
        {
            var rect = crashedRect.get("right");
            if (rect != null && player.getOldVelocityX() == 0.0)
            {
                player.setX(rect.getX() - player.getWidth());
                if (leftPressed || rightPressed)
                {
                    player.setOldVelocityX(player.getVelocityX());
                }
                player.setVelocityX(0.0);
            }
        }

        if (!crashedLeft && !crashedRight && !crashedUp && !standingOn && player.getVelocityY() == 0.0 && !player.isStandingOnStartLine()
                && !player.isMoving())
        {
            player.addVelocityY(constG);
            player.setMoving(true);
        }
//        else if ((!standingOn && !crashedUp)
//                || (standingOn && player.getVelocityY() < 0 && !crashedUp))
//        {
//            // player.setY(MathH.round(player.getY() + player.getVelocityY() * time, 3));
//        }
        else if (crashedUp && player.getVelocityY() < 0.0)
        {
            player.setVelocityY(0.0);
        }
        else if (standingOn && player.getVelocityY() >= 0.0)
        {
            player.setVelocityY(0.0);
            player.setMoving(false);
        }

        player.setY(MathH.round(player.getY() + player.getVelocityY() * time, 3));

        var scene = firstLevelCanvas.getScene();
        //Camera follows the player, it is necessary that the canvas must be higher than the scene itself.
        cam.setLayoutY(player.getY() - (scene.getHeight() * 0.8) + player.getHeight());
    }

    private void updateEnemy(double time)
    {
        Iterator<FallingRectangle> frIt = enemy.iterator();
        for (var rect : enemy)
        //while(frIt.hasNext())
        {
            //var rect = frIt.next();
            if (!rect.isStopped())
            {
                rect.setY(MathH.round(rect.getY() + rect.getVelocityY() * time, 3));
            }
        }
    }

    private void update(double time)
    {
        updatePlayer(time);
        updateEnemy(time);
        draw();

        if (!rightPressed && !leftPressed && !paused)
        {
            if (player.getVelocityX() > 10e-6)
            {
                if (player.getVelocityX() <= 1.0)
                {
                    player.setVelocityX(0.0);
                }
                else
                {
                    if (!rightPressed)
                    {
                        player.addVelocityX(-50.0);
                    }
                }
            }
            else if (player.getVelocityX() <= 10e-6)
            {
                if (player.getVelocityX() > -1.0)
                {
                    player.setVelocityX(0.0);
                }
                else
                {
                    if (!leftPressed)
                    {
                        player.addVelocityX(50.0);
                    }
                }
            }
        }
        if (player.isMoving() && !paused)
        {
            if (player.getY() + player.getHeight() < startLine.getStartY() + 0.5)
            {
                player.addVelocityY(constG);
                player.setStandingOnStartLine(false);
            }
            else if (player.getY() + player.getHeight() >= startLine.getStartY() + 0.5)
            {
                player.setY(MathH.round(startLine.getStartY() - player.getHeight(), 3));
                player.setVelocityY(0.0);
                player.setMoving(false);
                player.setStandingOnStartLine(true);
            }
        }
        if ((player.getX() + player.getWidth()) >= borders.get(1).getStartX())
        {
            player.setX(borders.get(1).getStartX() - player.getWidth());
        }
        else if (player.getX() <= borders.get(0).getStartX())
        {
            player.setX(borders.get(0).getStartX());
        }

    }

    //endregion Update

    //region Run

    public void run()
    {
        logger.debug("run() method called.");
        ts = new TimerTask()
        {
            @Override
            public synchronized void run()
            {
                var delta = 2 * 10e-4;  //if it were time based, it wouldn't be a constant
                update(delta);
            }
        };

        tsGenerate = new TimerTask()
        {
            @Override
            public synchronized void run()
            {
                FallingRectangle lastRect;
                if (enemy.size() != 0)
                {
                    lastRect = enemy.get(enemy.size() - 1);
                }
                else
                {
                    lastRect = null;
                }
                var newRect = FallingRectangle.generateFallingRectangle(player, lastRect, cam);
                if (newRect != null)
                {
                    enemy.add(newRect);
                }
            }
        };

        tr = new Timer();
        tr.schedule(ts, 0, 17);
        trGenerate = new Timer();
        trGenerate.schedule(tsGenerate, 0, 4000);
        var stage = Main.getPrimaryStage();
        stage.setOnCloseRequest(windowEvent -> {
            tr.cancel();
            trGenerate.cancel();
            logger.debug("Application closes from Game.");
            stage.close();
        });
    }

    //endregion Run

}
