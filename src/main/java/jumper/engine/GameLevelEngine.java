package jumper.engine;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import jumper.controllers.GameFirstLevelController;
import jumper.controllers.Main;
import jumper.helpers.EnemyType;
import jumper.helpers.GameEngineHelper;
import jumper.model.DB.AllTime;
import jumper.model.FallingRectangle;
import jumper.model.Player;
import jumper.model.Rect;
import jumper.queries.Queries;
import org.hibernate.annotations.common.util.impl.Log;
import org.tinylog.Logger;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static jumper.helpers.GameEngineHelper.sixtyFpsSeconds;
import static jumper.helpers.GameEngineHelper.tolerance;

/**
 * The {@code game's engine}.
 * This class is responsible to calculate the positions of the player, enemies and the walls.
 */
public class GameLevelEngine {
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
    private double collapsedTime = 0.0;
    /**
     * A constant that is used to calculate the {@code player}'s actual Y coordinate.
     */
    private static final double g = 50.0;
    /**
     * A {@code list} that stores those enemies, which are intended to remove.
     */
    private List<FallingRectangle> removeEnemies;
    /**
     * The {@link GameFirstLevelController} that initialized this engine.
     */
    private GameFirstLevelController gameFirstLevelController;
    /**
     * This {@link Map} stores the event handlers for key presses.
     */
    private Map<EventType<KeyEvent>, EventHandler<KeyEvent>> keyEventHandlerMap;
    /**
     * This is used to calculate whether the {@code player} has crashed with something.
     */
    private double actualVelocityY = 0.0;
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
    private int steppedEnemies = 0;
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
    private boolean steppedOnThisEnemy = false;
    /**
     * If true, the next level starts when the ENTER key pressed.
     */
    private boolean startNextLevel;
    /**
     * If true, the Main Menu loads, when the user presses the ESCAPE key.
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
     * @param gameFirstLevelController the {@link GameFirstLevelController} that initialized this
     *                                 engine.
     * @param levelCounter             the actual level's value
     */
    public GameLevelEngine(GameFirstLevelController gameFirstLevelController, int levelCounter) {
        Logger.debug("GameLevelEngine object is creating.");
        this.removeEnemies = new ArrayList<>();
        this.keyEventHandlerMap = new HashMap<>();
        this.gameFirstLevelController = gameFirstLevelController;
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

        Main.getPrimaryStage().getScene().setCamera(camera);

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
            steppedEnemies = 0;
            elapsedTime = 0;
            startTime = System.nanoTime();
            Logger.debug("resetObjects() method finished.");
        } catch (Exception ex) {
            Logger.error("Something bad happened while resetting objects, " +
                    "go to Main Menu.", ex);
            gameFirstLevelController.errorGoToMainMenu();
        }
    }

    /**
     * Generates enemies regarding to the actual level.
     *
     * @param level the game level
     * @return the created enemy
     */
    private FallingRectangle generateEnemy(int level) {
        Logger.debug("generateEnemy() method called.");
        double enemyPositionX;
        double enemyPositionY;
        double enemyWidth;
        double enemyHeight;
        double enemyFallingSpeed;
        EnemyType enemyType;
        generatedEnemies++;
        if (level < 3) {
            enemyWidth = FallingRectangle.basicEnemyWidth;
            enemyHeight = FallingRectangle.basicEnemyHeight;
            enemyFallingSpeed = FallingRectangle.basicEnemyVelocitiyY;
            enemyType = EnemyType.BasicEnemy;
        } else if (level < 5) {
            if (whichEnemyToGenerate % 2 == 0) {
                whichEnemyToGenerate = 1;
                enemyWidth = FallingRectangle.basicEnemyWidth;
                enemyHeight = FallingRectangle.basicEnemyHeight;
                enemyFallingSpeed = FallingRectangle.basicEnemyVelocitiyY;
                enemyType = EnemyType.BasicEnemy;
            } else {
                whichEnemyToGenerate = 0;
                enemyWidth = FallingRectangle.spikeEnemyWidth;
                enemyHeight = FallingRectangle.spikeEnemyHeight;
                enemyFallingSpeed = FallingRectangle.spikeEnemyVelocityY;
                enemyType = EnemyType.SpikeEnemy;
            }
        } else {
            enemyWidth = FallingRectangle.spikeEnemyWidth;
            enemyHeight = FallingRectangle.spikeEnemyHeight;
            enemyFallingSpeed = FallingRectangle.spikeEnemyVelocityY;
            enemyType = EnemyType.SpikeEnemy;
        }
        if (enemies.size() == 0) {
            enemyPositionX = Math.floor(GameEngineHelper.WIDTH / 5);
            enemyPositionY = camera.getLayoutY() - enemyDistanceY;
            return new FallingRectangle(enemyPositionX, enemyPositionY,
                    enemyWidth, enemyHeight, Color.BLUE, enemyFallingSpeed, enemyType);
        } else {
            FallingRectangle latestEnemy = enemies.get(enemies.size() - 1);
            double ifDistance = (latestEnemy.getWidth() * 1.2) + enemyDistanceX;
            if (GameEngineHelper.WIDTH - (latestEnemy.getX() + latestEnemy.getWidth()) > ifDistance) {
                enemyPositionX = latestEnemy.getX() + latestEnemy.getWidth() + enemyDistanceX;
            } else if (latestEnemy.getX() > ifDistance) {
                enemyPositionX = enemyDistanceX;
            } else {
                enemyPositionX = 20;
            }

            if (latestEnemy.getY() - enemyDistanceY > levelEndY) {
                enemyPositionY = latestEnemy.getStartY() - enemyDistanceY;
            } else {
                enemyPositionY = levelEndY;
            }
            FallingRectangle newEnemy = new FallingRectangle(enemyPositionX, enemyPositionY,
                    enemyWidth, enemyHeight, Color.BLUE, enemyFallingSpeed, enemyType);

            Logger.debug("generateEnemy() method finished.");
            Logger.info("Method returned with enemy: {}, level: {}", newEnemy, level);

            return newEnemy;
        }
    }

    /**
     * Decides whether an enemy object should be stopped or not.
     *
     * @param fallingRectangle the enemy object that should be examined
     * @return the value whether the enemy should be stopped or not
     */
    private boolean stopEnemy(FallingRectangle fallingRectangle) {
        double radiusX = 100.0 + tolerance;
        double playerHeight = player.getY() + player.getHeight();
        double fallingRectangleWidth = fallingRectangle.getX() + fallingRectangle.getWidth();
        double playerWidth = player.getX() + player.getWidth();

        boolean playerLowerY = playerHeight - fallingRectangle.getY() < -tolerance;
        boolean inPlayersLeftX = Math.abs(player.getX() - (fallingRectangleWidth)) < radiusX;
        boolean inPlayersRightX = Math.abs(playerWidth - fallingRectangle.getX()) < radiusX;

        if (playerLowerY && (inPlayersLeftX || inPlayersRightX)) {
            if (fallingRectangle.getVelocityY() != 0) {
                Logger.info("The enemy object should be stopped: {}", fallingRectangle);
            }
            return true;
        }
        return false;
    }

    /**
     * Calculates the coordinates of the enemies.
     */
    private void moveEnemy() {
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).setY(enemies.get(i).getY() +
                    (enemies.get(i).getVelocityY() * sixtyFpsSeconds));
            if (stopEnemy(enemies.get(i))) {
                enemies.get(i).setVelocityY(0.0);
            }

            if (Intersection.upIntersection(player, enemies.get(i))) {
                removeEnemies.add(enemies.get(i));
            }

            for (int j = 0; j < enemies.size(); j++) {
                if (i != j && Intersection.upIntersection(enemies.get(i), enemies.get(j))) {
                    removeEnemies.add(enemies.get(i));
                    break;
                }
            }

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
        gameFirstLevelController.gameOver(elapsedTime);
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
        actualVelocityY = Math.abs(actualVelocityY);
        for (int i = 0; i < enemies.size(); i++) {
            if (Intersection.upIntersection(player, enemies.get(i), actualVelocityY)) {
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
                    enemies.get(i), actualVelocityY)
                /*&& player.getActualY() >= player.getStartY()*/) {
                enemyUnderPlayer = enemies.get(i);
                standingOnEnemy = true;
                if (upReleased) {
                    player.setJumping(false);
                }
            } else if ((player.getActualY() + player.getHeight())
                    < (borders.get(0).getStartY() + actualVelocityY)
                    && (player.getActualY() + player.getHeight())
                    >= (borders.get(0).getStartY() - actualVelocityY)
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

        calculatePlayerY(crashed, standingOnEnemy, standingOnLine, enemyUnderPlayer);

        calculatePlayerX(leftCrashed, rightCrashed, leftCrashedLine, rightCrashedLine,
                enemyLeftPlayer, enemyRightPlayer);

        if (isEndGame()) {
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
        double points = GameEngineHelper.calculatePoints(generatedEnemies, steppedEnemies,
                elapsedTime);

        Logger.info("Level {} finished with {} points, player Y velocity: {}",
                levelCounter, points, player.getStartVelocityY());

        gameFirstLevelController.endLevel((int) points, elapsedTime,
                Math.toIntExact(Double.valueOf(player.getStartVelocityY()).longValue()));
        elapsedTime = 0.0;
        Logger.debug("endGame() method finished.");
    }

    /**
     * Calculates the {@code player}'s X coordinates.
     *
     * @param leftCrashed      whether the {@code player}'s left side crashed
     * @param rightCrashed     whether the {@code player}'s right side crashed
     * @param leftCrashedLine  whether the {@code player}'s left side crashed with the left wall
     * @param rightCrashedLine whether the {@code player}'s right side crashed with the right wall
     * @param enemyLeftPlayer  the enemy that crashed with the {@code player}'s left side
     * @param enemyRightPlayer the enemy that crashed with the {@code player}'s right side
     */
    private void calculatePlayerX(boolean leftCrashed, boolean rightCrashed,
                                  boolean leftCrashedLine, boolean rightCrashedLine,
                                  FallingRectangle enemyLeftPlayer,
                                  FallingRectangle enemyRightPlayer) {
        if (!leftCrashed && !rightCrashed &&
                ((!leftReleased && rightReleased) ||
                        (leftReleased && !rightReleased))) {
            player.setX(player.getX() + (player.getVelocityX() * sixtyFpsSeconds));
        } else if (leftCrashed && !rightCrashed && rightReleased) {
            player.setVelocityX(0.0);
            if (enemyLeftPlayer != null) {
                Logger.info("Player's left side crashed with enemy.");
                player.setX(enemyLeftPlayer.getX() + enemyLeftPlayer.getWidth()
                        + tolerance);
            } else {
                Logger.info("Player's left side crashed with line.");
                player.setX(borders.get(1).getStartX() + borders.get(1).getStrokeWidth() / 2
                        + tolerance);
            }
        } else if (!leftCrashed && rightCrashed && leftReleased) {
            player.setVelocityX(0.0);
            if (enemyRightPlayer != null) {
                Logger.info("Player's right side crashed with enemy.");
                player.setX(enemyRightPlayer.getX() - player.getWidth() - tolerance);
            } else {
                Logger.info("Player's right side crashed with line.");
                player.setX(borders.get(2).getStartX() - borders.get(2).getStrokeWidth() / 2
                        - player.getWidth() - tolerance);
            }
        } else if (!leftCrashed && rightCrashed && !leftReleased) {
            Logger.info("Player's right side crashed.");
            player.setX(player.getX() + (player.getVelocityX() * sixtyFpsSeconds));
        } else if (leftCrashed && !rightCrashed && !rightReleased) {
            Logger.info("Player's left side crashed.");
            player.setX(player.getX() + (player.getVelocityX() * sixtyFpsSeconds));
        } else if (leftCrashed && rightCrashed) {
            player.setVelocityX(0.0);
            if (enemyLeftPlayer != null) {
                if (leftCrashedLine) {
                    player.setX(borders.get(1).getStartX() + borders.get(1).getStrokeWidth() / 2
                            + tolerance);
                } else if (rightCrashedLine) {
                    player.setX(borders.get(2).getStartX() - borders.get(2).getStrokeWidth() / 2
                            - player.getWidth() - tolerance);
                } else {
                    player.setX(enemyLeftPlayer.getX() + enemyLeftPlayer.getWidth() + tolerance);
                }
            } else {
                Logger.info("Player stuck between left wall and enemy.");
                player.setX(borders.get(1).getStartX() + borders.get(1).getStrokeWidth()
                        + tolerance);
            }
        }
    }

    /**
     * Calculates the {@code player}'s Y coordinate.
     *
     * @param crashed         whether the {@code player} crashed with something
     * @param standingOnEnemy whether the {@code player} standing on {@code enemy}
     * @param standingOnLine  whether the {@code player} standing on {@code line}
     * @param enemy           the {@code enemy} that the {@code player} crashed with
     */
    private void calculatePlayerY(boolean crashed, boolean standingOnEnemy, boolean standingOnLine,
                                  FallingRectangle enemy) {
        if (crashed && standingOnEnemy) {
            if (!steppedOnThisEnemy) {
                steppedOnThisEnemy = true;
                steppedEnemies++;
            }
            player.setStartVelocityY(player.getStartVelocityY()
                    + player.getDecreaseStartVelocityY());
            player.setStartY(enemy.getY() - tolerance);
            actualVelocityY = tolerance;
            player.setFalling(false);
            Logger.info("Player standing on enemy and crashed with enemy. " +
                    "New Y velocity: {}", player.getStartVelocityY());
        } else if (crashed && standingOnLine) {
            player.setStartVelocityY(player.getStartVelocityY()
                    + player.getDecreaseStartVelocityY());
            player.setStartY(borders.get(0).getStartY() - tolerance);
            actualVelocityY = tolerance;
            player.setFalling(false);
            steppedOnThisEnemy = false;
            Logger.info("Player standing on line and crashed with enemy. " +
                    "New Y velocity: {}", player.getStartVelocityY());
        } else if (crashed && player.isJumping()) {
            player.setStartVelocityY(player.getStartVelocityY()
                    + player.getDecreaseStartVelocityY());
            collapsedTime = 0.0;
            player.setStartY(player.getActualY());
            double fallingVelocity = g / 2 * Math.pow(collapsedTime, 2);
            actualVelocityY = tolerance;
            player.setActualY(player.getStartY() + (fallingVelocity));
            player.setFalling(true);
            steppedOnThisEnemy = false;
            Logger.info("Player was jumping when crashed with enemy. " +
                    "New Y velocity: {}", player.getStartVelocityY());
        } else if (standingOnEnemy && !player.isJumping()) {
            if (!steppedOnThisEnemy) {
                steppedOnThisEnemy = true;
                steppedEnemies++;
            }
            player.setStartY(enemy.getY() - player.getHeight() - tolerance);
            actualVelocityY = tolerance;
            if (upReleased) {
                player.setFalling(false);
            } else {
                player.setJumping(true);
            }
        } else if (standingOnLine && !player.isJumping()) {
            player.setVelocityY(0.0);
            actualVelocityY = tolerance;
            player.setStartY(borders.get(0).getStartY() - player.getHeight() - tolerance);
            player.setFalling(false);
            steppedOnThisEnemy = false;
        } else if (player.isJumping() && !player.isFalling()) {
            double jumpingHeight = ((player.getStartVelocityY() * collapsedTime)
                    + (g / 2 * Math.pow(collapsedTime, 2)));
            actualVelocityY = player.getStartY() + jumpingHeight - player.getActualY();
            player.setActualY(player.getStartY() + jumpingHeight);
            player.setFalling(false);
            steppedOnThisEnemy = false;
        } else if (player.isFalling()) {
            double fallingVelocity = g / 2 * Math.pow(collapsedTime, 2);
            actualVelocityY = player.getStartY() + fallingVelocity - player.getActualY();
            player.setActualY(player.getStartY() + (fallingVelocity));
            steppedOnThisEnemy = false;
        } else if (!standingOnLine && !standingOnEnemy) {
            double fallingVelocity = g / 2 * Math.pow(collapsedTime, 2);
            actualVelocityY = tolerance;
            player.setActualY(player.getStartY() + (fallingVelocity));
            player.setFalling(true);
            steppedOnThisEnemy = false;
        }

        if (player.getActualY() + player.getHeight() > borders.get(0).getStartY() + tolerance) {
            player.setActualY(borders.get(0).getStartY() - player.getHeight() - tolerance);
            player.setStartY(player.getActualY());
            player.setY(player.getActualY());
            player.setJumping(false);
            player.setFalling(false);
            actualVelocityY = 0.0;
        }

        if (player.isJumping() || player.isFalling()) {
            collapsedTime += sixtyFpsSeconds;
        } else {
            collapsedTime = 0.0;
        }
    }

    /**
     * Draws the current frame to the {@link Canvas}
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
     * A helper method to draw an {@link Image} to the {@code canvas}
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
            moveEnemy();
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
            enemies.add(generateEnemy(levelCounter));
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
                    gameFirstLevelController.errorGoToMainMenu();
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
                    EntityManager em = Main.getEntityManager();
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
                    gameFirstLevelController.escPressed();
                }
            }
            if (keyCode == KeyCode.ENTER && startNextLevel) {
                Logger.info("Enter pressed. Continuing with next level: {}.",
                        levelCounter + 1);
                gameFirstLevelController.nextLevel();
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

        var stage = Main.getPrimaryStage();
        stage.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandlerMap.get(KeyEvent.KEY_PRESSED));
        stage.addEventHandler(KeyEvent.KEY_RELEASED, keyEventHandlerMap.get(KeyEvent.KEY_RELEASED));
        Logger.debug("keyListener() method finished.");
    }

    /**
     * Removes the key listener from the engine.
     */
    public void removeKeyListener() {
        var stage = Main.getPrimaryStage();
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
            gameFirstLevelController.initGameEngineLevel(ap);
            gameFirstLevelController.runGameLevelEngine();
        } catch (Exception ex) {
            Logger.error("Can not continue the game, due to error.", ex);
            gameFirstLevelController.errorGoToMainMenu();
        }
    }

    /**
     * Decides whether the {@code player} has reached the end of the level or not.
     *
     * @return the {@code player} has reached the end of the level or not
     */
    public boolean isEndGame() {
        return player.getActualY() <= levelEndY;
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
