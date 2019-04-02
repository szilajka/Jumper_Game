package jumper.engine;

/*-
 * #%L
 * jumper_game
 * %%
 * Copyright (C) 2019 Szilárd Németi
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import jumper.controllers.GameFirstLevelController;
import jumper.controllers.Main;
import jumper.helpers.EnemyType;
import jumper.helpers.GameEngineHelper;
import jumper.model.FallingRectangle;
import jumper.model.Player;
import jumper.model.Rect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static jumper.helpers.GameEngineHelper.sixtyFpsSeconds;
import static jumper.helpers.GameEngineHelper.tolerance;

public class GameLevelEngine {
    private static final Logger logger = LogManager.getLogger("GameLevelEngine");

    private final double levelEndY = 500.0;
    private final double enemyDistanceY = 200.0;
    private final double enemyDistanceX = 150.0;
    private Player player;
    private List<FallingRectangle> enemies;
    private Camera camera;
    private List<Line> borders;
    private Canvas canvas;
    private boolean jumping, paused;
    private double collapsedTime = 0.0;
    private static final double g = 50.0;
    private List<FallingRectangle> removeEnemies;
    private GameFirstLevelController gameFirstLevelController;
    private Map<EventType<KeyEvent>, EventHandler<KeyEvent>> keyEventHandlerMap;
    private double actualVelocityY = 0.0;
    private double actualVelocityX = 0.0;
    private boolean leftReleased = true;
    private boolean rightReleased = true;
    private boolean upReleased = true;
    private int whichEnemyToGenerate = 0;
    private int generatedEnemies = 0;
    private int steppedEnemies = 0;
    private int levelCounter;
    private double elapsedTime = 0.0;
    private double startTime = 0;
    private boolean steppedOnThisEnemy = false;
    private boolean startNextLevel;
    private boolean gameOverGoToMain = false;

    public Player getPlayer() {
        return this.player;
    }

    public boolean isJumping() {
        return jumping;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public boolean isLeftReleased() {
        return leftReleased;
    }

    public void setLeftReleased(boolean leftReleased) {
        this.leftReleased = leftReleased;
    }

    public boolean isRightReleased() {
        return rightReleased;
    }

    public void setRightReleased(boolean rightReleased) {
        this.rightReleased = rightReleased;
    }

    public boolean isUpReleased() {
        return upReleased;
    }

    public void setUpReleased(boolean upReleased) {
        this.upReleased = upReleased;
    }

    public void setLevelCounter(int levelCounter) {
        this.levelCounter = levelCounter;
    }

    public GameLevelEngine(Player player, List<FallingRectangle> enemies,
                           Camera camera, List<Line> borders, Canvas canvas,
                           GameFirstLevelController gameFirstLevelController, int levelCounter) {
        this.player = player;
        this.enemies = enemies;
        this.camera = camera;
        this.borders = borders;
        this.canvas = canvas;
        this.removeEnemies = new ArrayList<>();
        this.keyEventHandlerMap = new HashMap<>();
        this.gameFirstLevelController = gameFirstLevelController;
        this.levelCounter = levelCounter;
    }

    public GameLevelEngine(GameFirstLevelController gameFirstLevelController, int levelCounter) {
        this.removeEnemies = new ArrayList<>();
        this.keyEventHandlerMap = new HashMap<>();
        this.gameFirstLevelController = gameFirstLevelController;
        this.levelCounter = levelCounter;
    }

    private void initObjects(AnchorPane ap) {
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

        }
    }

    public void resetLevel() {
        resetObjects();
    }

    private void resetObjects() {
        try {
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
        } catch (Exception ex) {
            logger.error("Something bad happened while resetting objects.", ex);
            gameFirstLevelController.errorGoToMainMenu();
        }

    }

    private FallingRectangle generateEnemy(int level) {
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

            return new FallingRectangle(enemyPositionX, enemyPositionY,
                    enemyWidth, enemyHeight, Color.BLUE, enemyFallingSpeed, enemyType);
        }
    }

    private boolean stopEnemy(FallingRectangle fallingRectangle) {
        double radiusX = 100.0 + tolerance;
        double playerHeight = player.getY() + player.getHeight();
        double fallingRectangleWidth = fallingRectangle.getX() + fallingRectangle.getWidth();
        double playerWidth = player.getX() + player.getWidth();

        boolean playerLowerY = playerHeight - fallingRectangle.getY() < -tolerance;
        boolean inPlayersLeftX = Math.abs(player.getX() - (fallingRectangleWidth)) < radiusX;
        boolean inPlayersRightX = Math.abs(playerWidth - fallingRectangle.getX()) < radiusX;

        if (playerLowerY && (inPlayersLeftX || inPlayersRightX)) {
            return true;
        }

        return false;
    }

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

    private void gameOver() {
        gameFirstLevelController.gameOver();
    }

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
                        || player.getStartVelocityY() <= player.getDecreaseStartVelocityY()) {
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

    private void endGame() {
        double endTime = System.nanoTime();
        elapsedTime += (endTime - startTime);
        double points = GameEngineHelper.calculatePoints(generatedEnemies, steppedEnemies,
                elapsedTime);
        elapsedTime = 0.0;
        gameFirstLevelController.endLevel((int) points, elapsedTime);
    }

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
                player.setX(enemyLeftPlayer.getX() + enemyLeftPlayer.getWidth()
                        + tolerance);
            } else {
                player.setX(borders.get(1).getStartX() + borders.get(1).getStrokeWidth() / 2
                        + tolerance);
            }
        } else if (!leftCrashed && rightCrashed && leftReleased) {
            player.setVelocityX(0.0);
            if (enemyRightPlayer != null) {
                player.setX(enemyRightPlayer.getX() - player.getWidth() - tolerance);
            } else {
                player.setX(borders.get(2).getStartX() - borders.get(2).getStrokeWidth() / 2
                        - player.getWidth() - tolerance);
            }
        } else if (!leftCrashed && rightCrashed && !leftReleased) {
            player.setX(player.getX() + (player.getVelocityX() * sixtyFpsSeconds));
        } else if (leftCrashed && !rightCrashed && !rightReleased) {
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
                player.setX(borders.get(1).getStartX() + borders.get(1).getStrokeWidth()
                        + tolerance);
            }
        }
    }

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
        } else if (crashed && standingOnLine) {
            player.setStartVelocityY(player.getStartVelocityY()
                    + player.getDecreaseStartVelocityY());
            player.setStartY(borders.get(0).getStartY() - tolerance);
            actualVelocityY = tolerance;
            player.setFalling(false);
            steppedOnThisEnemy = false;
        } else if (crashed && player.isJumping()) {
            collapsedTime = 0.0;
            player.setStartY(player.getActualY());
            double fallingVelocity = g / 2 * Math.pow(collapsedTime, 2);
            actualVelocityY = tolerance;
            player.setActualY(player.getStartY() + (fallingVelocity));
            player.setFalling(true);
            steppedOnThisEnemy = false;
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

    private void draw() {
        if (canvas != null) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            for (int i = 0; i < enemies.size(); i++) {
                drawRect(enemies.get(i), gc, GameEngineHelper.HEIGHT);
            }

            drawRect(player, gc, GameEngineHelper.HEIGHT);
            camera.setLayoutY(player.getY() + player.getHeight() - (GameEngineHelper.HEIGHT * 0.8));
        }
    }

    private void drawRect(Rect rect, GraphicsContext gc, double sceneHeight) {
        if (shouldDrawIt(camera, rect.getY(), rect.getHeight(), sceneHeight)) {
            gc.setFill(rect.getColor());
            gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        }
    }

    private void drawImage(Canvas canvas) {
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

    }

    private boolean shouldDrawIt(Camera cam, double drawY, double drawHeight, double sceneHeight) {
        return ((drawY + drawHeight) >= cam.getLayoutY())
                && (drawY <= (cam.getLayoutY() + sceneHeight));
    }

    public void init(AnchorPane ap) {
        initObjects(ap);
    }

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

    public void generateEnemyTask() {
        synchronized (enemies) {
            enemies.add(generateEnemy(levelCounter));
        }
    }

    public void keyListener() {
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
                    gameFirstLevelController.errorGoToMainMenu();
                } else {
                    double pauseTime = System.nanoTime();
                    elapsedTime += (pauseTime - startTime);
                    player.setOldVelocityX(player.getVelocityX());
                    player.setOldVelocityY(player.getVelocityY());
                    player.setVelocityX(0.0);
                    player.setVelocityY(0.0);
                    paused = true;
                    leftReleased = true;
                    rightReleased = true;
                    gameFirstLevelController.escPressed();
                }
            }
            if (keyCode == KeyCode.ENTER && startNextLevel) {
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

    }

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

    public void letsContinue(AnchorPane ap) {
        try {
            startTime = System.nanoTime();
            player.setVelocityX(player.getOldVelocityX());
            player.setVelocityY(player.getOldVelocityY());
            player.setOldVelocityX(0.0);
            player.setOldVelocityY(0.0);
            paused = false;
            gameFirstLevelController.initGameEngineLevel(ap);
            gameFirstLevelController.runGameLevelEngine();
        } catch (Exception ex) {
            logger.error("Can not continue the game, due to error.", ex);
            gameFirstLevelController.errorGoToMainMenu();
        }
    }

    public boolean isEndGame() {
        return player.getActualY() <= levelEndY;
    }

    public void drawGameOverText() {
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

    public void drawNextLevelText() {
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
