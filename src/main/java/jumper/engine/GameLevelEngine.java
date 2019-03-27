package jumper.engine;


import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Camera;
import javafx.scene.ParallelCamera;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import jumper.controllers.GameFirstLevelController;
import jumper.controllers.Main;
import jumper.helpers.SceneHelper;
import jumper.model.FallingRectangle;
import jumper.model.Player;
import jumper.model.Rect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static jumper.helpers.MathHelper.sixtyFpsSeconds;
import static jumper.helpers.MathHelper.tolerance;

public class GameLevelEngine {
    private static final Logger logger = LogManager.getLogger("GameLevelEngine");

    private double enemyFallingSpeed;
    private final double levelEndY = 500.0;
    private final double enemyDistanceY = 200.0;
    private final double enemyDistanceX = 150.0;
    private static Player player;
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

    public GameLevelEngine(double enemyFallingSpeed, Player player, List<FallingRectangle> enemies,
                           Camera camera, List<Line> borders, Canvas canvas,
                           GameFirstLevelController gameFirstLevelController) {
        this.enemyFallingSpeed = enemyFallingSpeed;
        this.player = player;
        this.enemies = enemies;
        this.camera = camera;
        this.borders = borders;
        this.canvas = canvas;
        this.removeEnemies = new ArrayList<>();
        this.keyEventHandlerMap = new HashMap<>();
        this.gameFirstLevelController = gameFirstLevelController;
    }

    public GameLevelEngine(double enemyFallingSpeed,
                           GameFirstLevelController gameFirstLevelController) {
        this.enemyFallingSpeed = enemyFallingSpeed;
        this.removeEnemies = new ArrayList<>();
        this.keyEventHandlerMap = new HashMap<>();
        this.gameFirstLevelController = gameFirstLevelController;
    }

    private void initObjects(AnchorPane ap) {
        if (canvas == null) {
            canvas = new Canvas(SceneHelper.WIDTH, SceneHelper.HEIGHT * 5);
        }

        ap.getChildren().add(canvas);

        if (player == null) {
            double positionX = (SceneHelper.WIDTH / 2) - (Player.playerWidth / 2);
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

        camera.setLayoutY(player.getY() + player.getHeight() - (SceneHelper.HEIGHT * 0.8));
        if (enemies == null) {
            enemies = Collections.synchronizedList(new ArrayList<>());
        }
        if (borders == null) {
            borders = new ArrayList<>(3);
            borders.addAll(Arrays.asList(
                    new Line(0.0, canvas.getHeight(), canvas.getWidth(), canvas.getHeight()),
                    new Line(0.0, 0.0, 0.0, canvas.getHeight()),
                    new Line(canvas.getWidth(), 0.0, canvas.getWidth(), canvas.getHeight())));
        }
    }

    private FallingRectangle generateEnemy() {
        double enemyPositionX;
        double enemyPositionY;
        if (enemies.size() == 0) {
            enemyPositionX = Math.floor(SceneHelper.WIDTH / 5);
            enemyPositionY = camera.getLayoutY() - enemyDistanceY;
            return new FallingRectangle(enemyPositionX, enemyPositionY,
                    FallingRectangle.basicEnemyWidth, FallingRectangle.basicEnemyHeight,
                    Color.BLUE, enemyFallingSpeed);
        } else {
            FallingRectangle latestEnemy = enemies.get(enemies.size() - 1);
            double ifDistance = (latestEnemy.getWidth() * 1.2) + enemyDistanceX;
            if (SceneHelper.WIDTH - (latestEnemy.getX() + latestEnemy.getWidth()) > ifDistance) {
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
                    FallingRectangle.basicEnemyWidth, FallingRectangle.basicEnemyHeight,
                    Color.BLUE, enemyFallingSpeed);
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
            } else if (Math.abs(player.getX() - borders.get(1).getStartX()
                    + borders.get(1).getStrokeWidth()) < actualVelocityX) {
                leftCrashed = true;
                leftCrashedLine = true;
            }
            if (Intersection.rightIntersection(player, enemies.get(i), actualVelocityX)) {
                rightCrashed = true;
                enemyRightPlayer = enemies.get(i);
            } else if (Math.abs(player.getX() + player.getWidth() - borders.get(2).getStartX())
                    < actualVelocityX) {
                rightCrashed = true;
                rightCrashedLine = true;
            }
        }

        calculatePlayerY(crashed, standingOnEnemy, standingOnLine, enemyUnderPlayer);

        calculatePlayerX(leftCrashed, rightCrashed, leftCrashedLine, rightCrashedLine,
                enemyLeftPlayer, enemyRightPlayer);
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
                player.setX(borders.get(1).getStartX() + borders.get(1).getStrokeWidth()
                        + tolerance);
            }
        } else if (!leftCrashed && rightCrashed && leftReleased) {
            player.setVelocityX(0.0);
            if (enemyRightPlayer != null) {
                player.setX(enemyRightPlayer.getX() - player.getWidth() - tolerance);
            } else {
                player.setX(borders.get(2).getStartX() - player.getWidth() - tolerance);
            }
        } else if (!leftCrashed && rightCrashed && !leftReleased) {
            player.setX(player.getX() + (player.getVelocityX() * sixtyFpsSeconds));
        } else if (leftCrashed && !rightCrashed && !rightReleased) {
            player.setX(player.getX() + (player.getVelocityX() * sixtyFpsSeconds));
        } else if (leftCrashed && rightCrashed) {
            player.setVelocityX(0.0);
            if (enemyLeftPlayer != null) {
                if (leftCrashedLine) {
                    player.setX(borders.get(1).getStartX() + borders.get(1).getStrokeWidth()
                            + tolerance);
                } else if (rightCrashedLine) {
                    player.setX(borders.get(2).getStartX() - player.getWidth() - tolerance);
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
        } else if (crashed && player.isJumping()) {
            collapsedTime = 0.0;
            player.setStartY(player.getActualY());
            double fallingVelocity = g / 2 * Math.pow(collapsedTime, 2);
            actualVelocityY = tolerance;
            player.setActualY(player.getStartY() + (fallingVelocity));
            player.setFalling(true);
        } else if (standingOnEnemy && !player.isJumping()) {
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
        } else if (player.isJumping() && !player.isFalling()) {
            double jumpingHeight = ((player.getStartVelocityY() * collapsedTime)
                    + (g / 2 * Math.pow(collapsedTime, 2)));
            actualVelocityY = player.getStartY() + jumpingHeight - player.getActualY();
            player.setActualY(player.getStartY() + jumpingHeight);
            player.setFalling(false);
        } else if (player.isFalling()) {
            double fallingVelocity = g / 2 * Math.pow(collapsedTime, 2);
            actualVelocityY = player.getStartY() + fallingVelocity - player.getActualY();
            player.setActualY(player.getStartY() + (fallingVelocity));
        } else if (!standingOnLine && !standingOnEnemy) {
            double fallingVelocity = g / 2 * Math.pow(collapsedTime, 2);
            actualVelocityY = tolerance;
            player.setActualY(player.getStartY() + (fallingVelocity));
            player.setFalling(true);
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
            for (int i = 0; i < borders.size(); i++) {
                if (i == 0 && shouldDrawIt(camera, borders.get(i).getStartY(),
                        borders.get(i).getStrokeWidth(), SceneHelper.HEIGHT)) {
                    drawLine(borders.get(i), gc);
                } else if (i != 0) {
                    drawLine(borders.get(i), gc);
                }
            }

            for (int i = 0; i < enemies.size(); i++) {
                drawRect(enemies.get(i), gc, SceneHelper.HEIGHT);
            }

            drawRect(player, gc, SceneHelper.HEIGHT);
            camera.setLayoutY(player.getY() + player.getHeight() - (SceneHelper.HEIGHT * 0.8));
        }
    }

    private void drawLine(Line line, GraphicsContext gc) {
        gc.setStroke(line.getStroke());
        gc.setLineWidth(line.getStrokeWidth());
        gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
    }

    private void drawRect(Rect rect, GraphicsContext gc, double sceneHeight) {
        if (shouldDrawIt(camera, rect.getY(), rect.getHeight(), sceneHeight)) {
            gc.setFill(rect.getColor());
            gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
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
            enemies.add(generateEnemy());
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
                player.setOldVelocityX(player.getVelocityX());
                player.setOldVelocityY(player.getVelocityY());
                player.setVelocityX(0.0);
                player.setVelocityY(0.0);
                paused = true;
                leftReleased = true;
                rightReleased = true;
                gameFirstLevelController.escPressed();
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


}
