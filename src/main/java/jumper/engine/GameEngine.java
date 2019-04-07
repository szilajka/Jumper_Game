package jumper.engine;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Camera;
import javafx.scene.ParallelCamera;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.util.Duration;
import jumper.authentication.Authenticate;
import jumper.controllers.GameLevelController;
import jumper.controllers.MainJFX;
import jumper.helpers.EnemyType;
import jumper.helpers.GameEngineHelper;
import jumper.model.DB.AllTime;
import jumper.model.FallingRectangle;
import jumper.model.Player;
import jumper.model.Rect;
import jumper.queries.Queries;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.pmw.tinylog.Logger;
import static jumper.helpers.GameEngineHelper.sixtyFpsSeconds;

/**
 * The {@code game's engine}.
 * This class is responsible to calculate the positions of the player, enemies and the walls.
 */
public class GameEngine {
    /**
     * A constant to declare the end of each level.
     */
    private final double levelEndY = 500.0;
    /**
     * A constant that is used while generating the enemy.
     */
    private final double enemyDistanceY = 200.0;
    /**
     * A constant that is used while generating the enemy.
     */
    private final double enemyDistanceX = 150.0;
    /**
     * The {@link Player} object.
     * This is the {@code user}.
     */
    private Player player;
    /**
     * A {@code list} that stores the enemies.
     */
    private List<FallingRectangle> enemies;
    /**
     * The {@link Camera} used to track the player.
     */
    private Camera camera;
    /**
     * A {@code list} that stores the borders (walls and the start line).
     */
    private List<Line> borders;
    /**
     * The {@link Canvas} which the engine draws.
     */
    private Canvas canvas;
    /**
     * A {@code boolean} that indicates whether the player is jumping or not.
     */
    private boolean jumping;
    /**
     * A {@code boolean} that indicates whether the game is paused or not.
     */
    private boolean paused;
    /**
     * This is used to calculate the {@code player}'s actual Y coordinate.
     */
    private SimpleDoubleProperty collapsedTime = new SimpleDoubleProperty(0.0);
    /**
     * A constant that is used to calculate the {@code player}'s actual Y coordinate.
     */
    private static final double g = 50.0;
    /**
     * A {@code list} that stores those enemies, which are intended to remove.
     */
    private List<FallingRectangle> removeEnemies;
    /**
     * The {@link GameLevelController} that initialized this engine.
     */
    private GameLevelController gameLevelController;
    /**
     * This {@link Map} stores the event handlers for key presses.
     */
    private Map<EventType<KeyEvent>, EventHandler<KeyEvent>> keyEventHandlerMap;
    /**
     * This is used to calculate whether the {@code player} has crashed with something.
     */
    private SimpleDoubleProperty actualVelocityY = new SimpleDoubleProperty(0.0);
    /**
     * This is used to calculate whether the {@code player} has crashed with something.
     */
    private double actualVelocityX = 0.0;
    /**
     * The user released the left key or not.
     */
    private boolean leftReleased = true;
    /**
     * The user released the right key or not.
     */
    private boolean rightReleased = true;
    /**
     * The user released the up key or not.
     */
    private boolean upReleased = true;
    /**
     * This is used to decide whether a {@link EnemyType#BasicEnemy}
     * or a {@link EnemyType#SpikeEnemy} to generate.
     */
    private int whichEnemyToGenerate = 0;
    /**
     * The number of the generated enemies.
     */
    private int generatedEnemies = 0;
    /**
     * The number of the {@code enemies} that the {@code player} stepped on.
     */
    private SimpleIntegerProperty steppedEnemies = new SimpleIntegerProperty(0);
    /**
     * The level counter.
     */
    private int levelCounter;
    /**
     * This stores the elapsed nanoseconds.
     */
    private double elapsedTime = 0.0;
    /**
     * This stores the time when the game is started or continued.
     */
    private double startTime = 0;
    /**
     * Used to decide whether a player is jumped to a new enemy or to an old one.
     */
    private SimpleBooleanProperty steppedOnThisEnemy = new SimpleBooleanProperty(false);
    /**
     * If true, the next level starts when the ENTER key pressed.
     */
    private boolean startNextLevel;
    /**
     * If true, the MainJFX Menu loads, when the user presses the ESCAPE key.
     */
    private boolean gameOverGoToMain = false;

    /**
     * Gets the actual {@code player} object.
     *
     * @return the {@code player}
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * If the {@code player} is jumping or not.
     *
     * @return if jumping, true, else false
     */
    public boolean isJumping() {
        return jumping;
    }

    /**
     * Sets the jumping value.
     *
     * @param jumping the value to be set.
     */
    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    /**
     * Whether the left key is released or not.
     *
     * @return if the left key is released, true, else false
     */
    public boolean isLeftReleased() {
        return leftReleased;
    }

    /**
     * Sets the left released value.
     *
     * @param leftReleased the value to be set.
     */
    public void setLeftReleased(boolean leftReleased) {
        this.leftReleased = leftReleased;
    }

    /**
     * Whether the right key is released or not.
     *
     * @return if the right key is released, true, else false
     */
    public boolean isRightReleased() {
        return rightReleased;
    }

    /**
     * Sets the right released value.
     *
     * @param rightReleased the value to be set.
     */
    public void setRightReleased(boolean rightReleased) {
        this.rightReleased = rightReleased;
    }

    /**
     * Whether the up key is released or not.
     *
     * @return if the up key is released, true, else false
     */
    public boolean isUpReleased() {
        return upReleased;
    }

    /**
     * Sets the up released value.
     *
     * @param upReleased the value to be set.
     */
    public void setUpReleased(boolean upReleased) {
        this.upReleased = upReleased;
    }

    /**
     * Sets the level counter value.
     *
     * @param levelCounter the value to be set
     */
    public void setLevelCounter(int levelCounter) {
        this.levelCounter = levelCounter;
    }

    /**
     * Constructor of the class.
     *
     * @param gameLevelController the {@link GameLevelController} that initialized this
     *                            engine.
     * @param levelCounter        the actual level's value
     */
    public GameEngine(GameLevelController gameLevelController, int levelCounter) {
        Logger.debug("GameEngine object is creating.");
        this.removeEnemies = new ArrayList<>();
        this.keyEventHandlerMap = new HashMap<>();
        this.gameLevelController = gameLevelController;
        this.levelCounter = levelCounter;
    }

    /**
     * Initializes the objects of the class.
     *
     * @param ap        the {@link AnchorPane} that will contains the {@link Canvas}
     * @param velocityY the {@code player}'s actual Y velocity
     */
    private void initObjects(AnchorPane ap, int velocityY) {
        Logger.info("initObjects() method called with the player actual Y velocity: {}",
            velocityY);
        startNextLevel = false;
        if (canvas == null) {
            canvas = new Canvas(GameEngineHelper.WIDTH, GameEngineHelper.HEIGHT * 5);
        }

        Canvas secondCanvas = new Canvas(GameEngineHelper.WIDTH, GameEngineHelper.HEIGHT * 5);
        drawImage(secondCanvas);
        ap.getChildren().add(secondCanvas);

        ap.getChildren().add(canvas);

        if (player == null) {
            double positionX = (GameEngineHelper.WIDTH / 2) - (Player.playerWidth / 2);
            double positionY = canvas.getHeight() - Player.playerHeight;
            player = new Player(positionX, positionY, Player.playerWidth, Player.playerHeight,
                Color.GREEN);
            player.setActualY(player.getY());
            player.setStartY(player.getY());
        }
        if (camera == null) {
            camera = new ParallelCamera();
        }

        MainJFX.getPrimaryStage().getScene().setCamera(camera);

        camera.setLayoutY(player.getY() + player.getHeight() - (GameEngineHelper.HEIGHT * 0.8));
        if (enemies == null) {
            enemies = Collections.synchronizedList(new ArrayList<>());
        }
        if (borders == null) {
            borders = new ArrayList<>(3);
            Line baseLine = new Line(0.0, canvas.getHeight(), canvas.getWidth(),
                canvas.getHeight());
            Line leftLine = new Line(0.0, 0.0, 0.0, canvas.getHeight());
            Line rightLine = new Line(canvas.getWidth(), 0.0, canvas.getWidth(),
                canvas.getHeight());

            baseLine.setStrokeWidth(3);
            leftLine.setStrokeWidth(100);
            rightLine.setStrokeWidth(100);


            borders.addAll(Arrays.asList(baseLine, leftLine, rightLine));
            player.setStartVelocityY(velocityY);
        }
        Logger.debug("initObjects() method finished.");
    }

    /**
     * A public method that is reached from other controllers to reset the game level.
     */
    public void resetLevel() {
        Logger.debug("resetLevel() method called.");
        resetObjects();
    }

    /**
     * Resets the objects of the class.
     * <p>
     * This method is called when the {@code player} completes a level, but
     * before loading the next level.
     */
    private void resetObjects() {
        try {
            Logger.debug("resetObjects() method called.");
            double positionX = (GameEngineHelper.WIDTH / 2) - (Player.playerWidth / 2);
            double positionY = canvas.getHeight() - Player.playerHeight;
            player.setStartY(positionY);
            player.setX(positionX);
            player.setActualY(positionY);
            player.setVelocityX(0.0);
            player.setVelocityY(0.0);
            player.setCrashedSpike(0);
            leftReleased = true;
            rightReleased = true;
            upReleased = true;
            player.setJumping(false);
            player.setFalling(false);
            enemies.clear();
            startNextLevel = false;
            canvas.getGraphicsContext2D().clearRect(0, 0,
                canvas.getWidth(), canvas.getHeight());
            generatedEnemies = 0;
            steppedEnemies.set(0);
            elapsedTime = 0;
            startTime = System.nanoTime();
            Logger.debug("resetObjects() method finished.");
        } catch (Exception ex) {
            Logger.error("Something bad happened while resetting objects, " +
                "go to MainJFX Menu.", ex);
            gameLevelController.errorGoToMainMenu();
        }
    }

    /**
     * Implements the end of the game if the user is dead.
     * <p>
     * If the {@code user} dies in the game, this method will be called.
     */
    private void gameOver() {
        Logger.debug("gameOver() method called.");
        double endTime = System.nanoTime();
        elapsedTime += (endTime - startTime);
        elapsedTime = TimeUnit.NANOSECONDS.toSeconds(Double.valueOf(elapsedTime).longValue());
        gameLevelController.gameOver(elapsedTime);
        elapsedTime = 0.0;
        Logger.debug("gameOver() method finished.");
    }

    /**
     * Calculates the {@code player}'s coordinates.
     */
    private void movePlayer() {
        boolean crashed = false, standingOnEnemy = false, standingOnLine = false,
            leftCrashed = false, leftCrashedLine = false,
            rightCrashed = false, rightCrashedLine = false;
        FallingRectangle enemyUnderPlayer = null, enemyLeftPlayer = null, enemyRightPlayer = null;
        player.setY(player.getActualY());
        actualVelocityX = Math.abs(player.getVelocityX() * sixtyFpsSeconds);
        actualVelocityY.set(Math.abs(actualVelocityY.get()));
        for (int i = 0; i < enemies.size(); i++) {
            if (Intersection.upIntersection(player, enemies.get(i), actualVelocityY.get())) {
                crashed = true;
                if (enemies.get(i).getEnemyType() == EnemyType.SpikeEnemy) {
                    player.setCrashedSpike(player.getCrashedSpike() + 1);
                }
                if (player.getCrashedSpike() == 3
                    || player.getStartVelocityY() >= -player.getDecreaseStartVelocityY()) {
                    gameOver();
                }
                enemies.remove(i);
                i--;
                continue;
            } else if (Intersection.bottomIntersection(player,
                enemies.get(i), actualVelocityY.get())
                /*&& player.getActualY() >= player.getStartY()*/) {
                enemyUnderPlayer = enemies.get(i);
                standingOnEnemy = true;
                if (upReleased) {
                    player.setJumping(false);
                }
            } else if ((player.getActualY() + player.getHeight())
                < (borders.get(0).getStartY() + actualVelocityY.get())
                && (player.getActualY() + player.getHeight())
                >= (borders.get(0).getStartY() - actualVelocityY.get())
                && player.getActualY() > player.getStartY()) {
                standingOnLine = true;
                if (upReleased) {
                    player.setJumping(false);
                }
            }

            if (Intersection.leftIntersection(player, enemies.get(i), actualVelocityX)) {
                leftCrashed = true;
                enemyLeftPlayer = enemies.get(i);
            } else if (player.getX() <= borders.get(1).getStrokeWidth() / 2) {
                leftCrashed = true;
                leftCrashedLine = true;
            }
            if (Intersection.rightIntersection(player, enemies.get(i), actualVelocityX)) {
                rightCrashed = true;
                enemyRightPlayer = enemies.get(i);
            } else if (player.getX() + player.getWidth() >=
                borders.get(2).getStartX() - borders.get(2).getStrokeWidth() / 2) {
                rightCrashed = true;
                rightCrashedLine = true;
            }
        }

        EngineMethods.calculatePlayerY(crashed, standingOnEnemy, standingOnLine, enemyUnderPlayer,
            player, borders, steppedOnThisEnemy, upReleased, steppedEnemies, actualVelocityY,
            collapsedTime, g);

        EngineMethods.calculatePlayerX(leftCrashed, rightCrashed, leftCrashedLine, rightCrashedLine,
            enemyLeftPlayer, enemyRightPlayer, player, leftReleased, rightReleased, borders);

        if (EngineMethods.isEndGame(player, levelEndY)) {
            endGame();
        }
    }

    /**
     * Implements the end of the level actions, before the next level loads.
     */
    private void endGame() {
        Logger.debug("endGame() method called.");
        double endTime = System.nanoTime();
        elapsedTime += (endTime - startTime);
        elapsedTime = TimeUnit.NANOSECONDS.toSeconds(Double.valueOf(elapsedTime).longValue());
        double points = GameEngineHelper.calculatePoints(generatedEnemies, steppedEnemies.get(),
            elapsedTime);

        Logger.info("Level {} finished with {} points, player Y velocity: {}",
            levelCounter, points, player.getStartVelocityY());

        gameLevelController.endLevel((int) points, elapsedTime,
            Math.toIntExact(Double.valueOf(player.getStartVelocityY()).longValue()));
        elapsedTime = 0.0;
        Logger.debug("endGame() method finished.");
    }


    /**
     * Draws the current frame to the {@link Canvas}.
     */
    private void draw() {
        if (canvas != null) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            for (int i = 0; i < enemies.size(); i++) {
                drawRect(enemies.get(i), gc);
            }

            drawRect(player, gc);
            camera.setLayoutY(player.getY() + player.getHeight() - (GameEngineHelper.HEIGHT * 0.8));
        }
    }

    /**
     * A helper method to draw {@link Rect} objects.
     *
     * @param rect the {@code rect} to draw
     * @param gc   the {@link GraphicsContext} that draws to {@code canvas}
     */
    private void drawRect(Rect rect, GraphicsContext gc) {
        if (shouldDrawIt(camera, rect.getY(), rect.getHeight())) {
            gc.setFill(rect.getColor());
            gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        }
    }

    /**
     * A helper method to draw an {@link Image} to the {@code canvas}.
     *
     * @param canvas the {@code canvas} that will be drawn on
     */
    private void drawImage(Canvas canvas) {
        Logger.debug("drawImage() method called.");
        var gc = canvas.getGraphicsContext2D();
        Image bgImg = new Image(getClass().getClassLoader()
            .getResource("textures/brick.png").toExternalForm());
        Image wallLeft = new Image(getClass().getClassLoader()
            .getResource("textures/wall_left.png").toExternalForm());
        Image wallRight = new Image(getClass().getClassLoader()
            .getResource("textures/wall_right.png").toExternalForm());
        int picHeight = (int) bgImg.getHeight();
        int howMany = (int) (canvas.getHeight() / picHeight);
        for (int i = 1; i <= howMany; i++) {
            double startY = canvas.getHeight() - (i * picHeight);
            if (startY >= levelEndY) {
                gc.drawImage(bgImg, 0, startY);
                gc.drawImage(wallLeft, 0, startY);
                gc.drawImage(wallRight, GameEngineHelper.WIDTH - 100, startY);
            } else if (startY < levelEndY) {
                int height = (int) (picHeight - (levelEndY - startY));
                gc.drawImage(bgImg, 0, startY, bgImg.getWidth(), bgImg.getHeight(),
                    0, levelEndY, bgImg.getWidth(), height);
                gc.setFill(Color.LIGHTBLUE);
                gc.fillRect(0, 0, 800, levelEndY);
            }
        }
        Logger.debug("drawImage() method finished.");
    }

    /**
     * A helper method to decide whether an object should be drawn or not.
     *
     * @param cam        the {@link Camera} that follows the {@code player}
     * @param drawY      the object's upper left Y coordinate
     * @param drawHeight the object's height
     * @return whether the object should be drawn or not
     */
    private boolean shouldDrawIt(Camera cam, double drawY, double drawHeight) {
        return ((drawY + drawHeight) >= cam.getLayoutY())
            && (drawY <= (cam.getLayoutY() + GameEngineHelper.HEIGHT));
    }

    /**
     * Initializes the objects of the class.
     * <p>
     * A public method that is available to call from other classes.
     *
     * @param ap        the {@link AnchorPane} that will contains the {@link Canvas}
     * @param velocityY the {@code player}'s actual Y velocity
     */
    public void init(AnchorPane ap, int velocityY) {
        initObjects(ap, velocityY);
    }

    /**
     * Initializes the start time and calculates once the enemies and the players coordinates
     * and draws them.
     * <p>
     * This method should be used with a {@link Timer} that calls every X seconds this method
     * to calculate the coordinates of the objects and draw them to the {@code canvas}.
     */
    public void runTask() {
        if (startTime == 0.0) {
            startTime = System.nanoTime();
        }
        synchronized (enemies) {
            EngineMethods.moveEnemy(enemies, removeEnemies, player);
            movePlayer();
            draw();
            for (int i = 0; i < removeEnemies.size(); i++) {
                enemies.remove(removeEnemies.get(i));
            }
            removeEnemies.clear();
        }
    }

    /**
     * Generates an enemy.
     * <p>
     * This method should be used with a {@link Timer} that calls every X seconds this method
     * to generate an enemy.
     */
    public void generateEnemyTask() {
        synchronized (enemies) {
            int enemiesSize = enemies.size();
            if (enemiesSize > 0) {
                enemies.add(EngineMethods.generateEnemy(levelCounter,
                    whichEnemyToGenerate, player, enemiesSize, camera, enemyDistanceX,
                    enemyDistanceY, enemies.get(enemies.size() - 1), levelEndY));
            } else {
                enemies.add(EngineMethods.generateEnemy(levelCounter,
                    whichEnemyToGenerate, player, enemiesSize, camera, enemyDistanceX,
                    enemyDistanceY, null, levelEndY));
            }
            generatedEnemies++;
            if (whichEnemyToGenerate % 2 == 0) {
                whichEnemyToGenerate = 1;
            } else {
                whichEnemyToGenerate = 0;
            }
        }

    }

    /**
     * Adds key listener to the engine.
     */
    public void keyListener() {
        Logger.debug("keyListener() method called.");
        EventHandler<KeyEvent> keyPressEventHandler = keyEvent -> {
            KeyCode keyCode = keyEvent.getCode();
            if ((keyCode == KeyCode.UP || keyCode == KeyCode.W)
                && !player.isJumping() && !paused) {
                upReleased = false;
                player.setJumping(true);
                player.setVelocityY(player.getStartVelocityY());
                player.setStartY(player.getY());
            } else if ((keyCode == KeyCode.UP || keyCode == KeyCode.W)
                && player.isJumping() && !paused) {
                Logger.info("Player is jumping.");
                upReleased = true;
            }
            if ((keyCode == KeyCode.LEFT || keyCode == KeyCode.A) && !paused) {
                leftReleased = false;
                player.setVelocityX(-player.getMaxVelocityX());
            }
            if ((keyCode == KeyCode.RIGHT || keyCode == KeyCode.D) && !paused) {
                rightReleased = false;
                player.setVelocityX(player.getMaxVelocityX());
            }

            if (keyCode == KeyCode.ESCAPE && !paused) {
                if (gameOverGoToMain) {
                    Logger.info("Game over, going to main menu.");
                    gameLevelController.errorGoToMainMenu();
                } else {
                    Logger.info("The game is paused.");
                    double pauseTime = System.nanoTime();
                    elapsedTime += (pauseTime - startTime);
                    elapsedTime = TimeUnit.NANOSECONDS
                        .toSeconds(Double.valueOf(elapsedTime).longValue());
                    player.setOldVelocityX(player.getVelocityX());
                    player.setOldVelocityY(player.getVelocityY());
                    player.setVelocityX(0.0);
                    player.setVelocityY(0.0);
                    paused = true;
                    leftReleased = true;
                    rightReleased = true;
                    //------------------------------------------Entity manager start---------
                    Logger.debug("Saving the elapsed time to database.");
                    EntityManager em = MainJFX.getEntityManager();
                    AllTime allTime = Queries.getAllTimeByUserName(em,
                        Authenticate.getLoggedInUser());
                    int elapsedSecs = Math.toIntExact(Double.valueOf(elapsedTime).longValue());
                    allTime.setElapsedTime(allTime.getElapsedTime() + elapsedSecs);
                    em.getTransaction().begin();
                    em.persist(allTime);
                    em.getTransaction().commit();
                    em.detach(allTime);
                    em.close();
                    Logger.debug("Transaction finished.");
                    //------------------------------------------Entity manager end---------
                    elapsedTime = 0;
                    gameLevelController.escPressed();
                }
            }
            if (keyCode == KeyCode.ENTER && startNextLevel) {
                Logger.info("Enter pressed. Continuing with next level: {}.",
                    levelCounter + 1);
                gameLevelController.nextLevel();
            }
        };

        EventHandler<KeyEvent> keyReleaseEventHandler = keyEvent -> {
            KeyCode keyCode = keyEvent.getCode();
            if (keyCode == KeyCode.UP || keyCode == KeyCode.W) {
                upReleased = true;
            }
            if (keyCode == KeyCode.LEFT || keyCode == KeyCode.A) {
                leftReleased = true;
            }
            if (keyCode == KeyCode.RIGHT || keyCode == KeyCode.D) {
                rightReleased = true;
            }
        };

        removeKeyListener();

        keyEventHandlerMap.put(KeyEvent.KEY_PRESSED, keyPressEventHandler);
        keyEventHandlerMap.put(KeyEvent.KEY_RELEASED, keyReleaseEventHandler);

        var stage = MainJFX.getPrimaryStage();
        stage.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandlerMap.get(KeyEvent.KEY_PRESSED));
        stage.addEventHandler(KeyEvent.KEY_RELEASED, keyEventHandlerMap.get(KeyEvent.KEY_RELEASED));
        Logger.debug("keyListener() method finished.");
    }

    /**
     * Removes the key listener from the engine.
     */
    public void removeKeyListener() {
        var stage = MainJFX.getPrimaryStage();
        if (keyEventHandlerMap.get(KeyEvent.KEY_PRESSED) != null) {
            stage.removeEventHandler(KeyEvent.KEY_PRESSED,
                keyEventHandlerMap.get(KeyEvent.KEY_PRESSED));
        }
        if (keyEventHandlerMap.get(KeyEvent.KEY_RELEASED) != null) {
            stage.removeEventHandler(KeyEvent.KEY_RELEASED,
                keyEventHandlerMap.get(KeyEvent.KEY_RELEASED));
        }
    }

    /**
     * Continues the game from paused state.
     *
     * @param ap the {@link AnchorPane} that will contains the {@link Canvas}
     */
    public void letsContinue(AnchorPane ap) {
        try {
            Logger.info("The game continues.");
            startTime = System.nanoTime();
            player.setVelocityX(player.getOldVelocityX());
            player.setVelocityY(player.getOldVelocityY());
            player.setOldVelocityX(0.0);
            player.setOldVelocityY(0.0);
            paused = false;
            gameLevelController.initGameEngineLevel(ap);
            gameLevelController.runGameLevelEngine();
        } catch (Exception ex) {
            Logger.error("Can not continue the game, due to error.", ex);
            gameLevelController.errorGoToMainMenu();
        }
    }


    /**
     * Draws the game over text to the screen.
     */
    public void drawGameOverText() {
        Logger.info("Game over.");
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFont(Font.font(100));
        gc.setStroke(Color.RED);
        final String completed = "Game over!";
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        final SimpleIntegerProperty i = new SimpleIntegerProperty(0);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(10), event -> {
            if (i.get() > completed.length()) {
                timeline.stop();
                gameOverGoToMain = true;
            } else {
                gc.strokeText(completed.substring(0, i.get()), 200, camera.getLayoutY() + 100);
                i.set(i.get() + 1);
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    /**
     * Draws the next level text to the screen.
     */
    public void drawNextLevelText() {
        Logger.info("Continue to next level: {}", levelCounter + 1);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFont(Font.font(100));
        gc.setStroke(Color.GREEN);
        final String completed = "Level " + levelCounter + "\ncompleted!" +
            "\nPress enter\nto continue!";
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        final SimpleIntegerProperty i = new SimpleIntegerProperty(0);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(10), event -> {
            if (i.get() > completed.length()) {
                timeline.stop();
                startNextLevel = true;
            } else {
                gc.strokeText(completed.substring(0, i.get()), 200, camera.getLayoutY() + 100);
                i.set(i.get() + 1);
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

}
