package jumper.engine;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Camera;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import jumper.helpers.EnemyType;
import jumper.helpers.GameEngineHelper;
import jumper.model.FallingRectangle;
import jumper.model.Player;

import java.util.List;

import org.tinylog.Logger;
import static jumper.helpers.GameEngineHelper.sixtyFpsSeconds;
import static jumper.helpers.GameEngineHelper.tolerance;

/**
 * This class implements the methods those are used by the engine.
 */
public class EngineMethods {

    /**
     * Generates enemies regarding to the actual level.
     *
     * @param level                the game level
     * @param whichEnemyToGenerate a number that helps to decide what type of {@code enemy} to generate
     * @param player               the {@link Player} object
     * @param enemiesSize          the size of the {@link java.util.List} that contains the living {@code enemies}
     * @param camera               the {@link Camera} object that follows the {@code player}
     * @param enemyDistanceX       the X distance between generated {@code enemies}
     * @param enemyDistanceY       the Y distance between generated {@code enemies}
     * @param latestEnemy          the latest generated {@code enemy}
     * @param levelEndY            the end of the level
     * @return the created enemy
     */

    protected static FallingRectangle generateEnemy(int level, int whichEnemyToGenerate,
                                                    Player player, int enemiesSize, Camera camera,
                                                    final double enemyDistanceX,
                                                    final double enemyDistanceY,
                                                    FallingRectangle latestEnemy,
                                                    final double levelEndY) {
        try {
            Logger.debug("generateEnemy() method called.");
            double enemyPositionX;
            double enemyPositionY;
            double enemyWidth;
            double enemyHeight;
            double enemyFallingSpeed;
            EnemyType enemyType;
            if (level < 3) {
                enemyWidth = FallingRectangle.basicEnemyWidth;
                enemyHeight = FallingRectangle.basicEnemyHeight;
                enemyFallingSpeed = FallingRectangle.basicEnemyVelocitiyY;
                enemyType = EnemyType.BasicEnemy;
            } else if (level < 5) {
                if (whichEnemyToGenerate % 2 == 0) {
                    enemyWidth = FallingRectangle.basicEnemyWidth;
                    enemyHeight = FallingRectangle.basicEnemyHeight;
                    enemyFallingSpeed = FallingRectangle.basicEnemyVelocitiyY;
                    enemyType = EnemyType.BasicEnemy;
                } else {
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
            if (enemiesSize == 0) {
                enemyPositionX = Math.floor(GameEngineHelper.WIDTH / 5);
                enemyPositionY = camera.getLayoutY() - enemyDistanceY;
                if (enemyPositionY >= player.getActualY()
                    && enemyPositionY <= player.getActualY() + player.getHeight()) {
                    enemyPositionY = player.getActualY() + player.getHeight() * 1.2;
                }
                return new FallingRectangle(enemyPositionX, enemyPositionY,
                    enemyWidth, enemyHeight, Color.BLUE, enemyFallingSpeed, enemyType);
            } else {
                double ifDistance = (latestEnemy.getWidth() * 1.2) + enemyDistanceX;
                if (GameEngineHelper.WIDTH -
                    (latestEnemy.getX() + latestEnemy.getWidth()) > ifDistance) {
                    enemyPositionX = latestEnemy.getX() + latestEnemy.getWidth() + enemyDistanceX;
                } else if (latestEnemy.getX() > ifDistance) {
                    enemyPositionX = enemyDistanceX;
                } else {
                    enemyPositionX = 20;
                }

                if (latestEnemy.getY() - enemyDistanceY > levelEndY) {
                    enemyPositionY = latestEnemy.getStartY() - enemyDistanceY;
                    if (enemyPositionY + enemyDistanceY >= player.getActualY()
                        && enemyPositionY - enemyDistanceY
                        <= player.getActualY() + player.getHeight()) {
                        enemyPositionY = player.getActualY() + player.getHeight() * 1.2;
                    }
                } else {
                    enemyPositionY = levelEndY;
                    if (enemyPositionY + enemyDistanceY >= player.getActualY()
                        && enemyPositionY - enemyDistanceY
                        <= player.getActualY() + player.getHeight()) {
                        enemyPositionY = player.getActualY() + player.getHeight() * 1.2;
                    }
                }
                FallingRectangle newEnemy = new FallingRectangle(enemyPositionX, enemyPositionY,
                    enemyWidth, enemyHeight, Color.BLUE, enemyFallingSpeed, enemyType);

                Logger.debug("generateEnemy() method finished.");
                Logger.info("Method returned with enemy: {}, level: {}", newEnemy, level);

                return newEnemy;
            }
        } catch (NullPointerException np) {
            Logger.debug("Some NullPointer exception, latest enemy: {}", latestEnemy);
            double enemyWidth = FallingRectangle.basicEnemyWidth;
            double enemyHeight = FallingRectangle.basicEnemyHeight;
            double enemyFallingSpeed = FallingRectangle.basicEnemyVelocitiyY;
            EnemyType enemyType = EnemyType.BasicEnemy;
            double enemyPositionX = Math.floor(GameEngineHelper.WIDTH / 5);
            double enemyPositionY = camera.getLayoutY() - enemyDistanceY;
            if (enemyPositionY >= player.getActualY()
                && enemyPositionY <= player.getActualY() + player.getHeight()) {
                enemyPositionY = player.getActualY() + player.getHeight() * 1.2;
            }
            return new FallingRectangle(enemyPositionX, enemyPositionY,
                enemyWidth, enemyHeight, Color.BLUE, enemyFallingSpeed, enemyType);
        }
    }

    /**
     * Decides whether an enemy object should be stopped or not.
     *
     * @param fallingRectangle the enemy object that should be examined
     * @param player           the actual {@code player}
     * @return the value whether the enemy should be stopped or not
     */
    protected static boolean stopEnemy(FallingRectangle fallingRectangle, Player player) {
        if (fallingRectangle.getVelocityY() != 0) {
            double radiusX = 100.0 + tolerance;
            double playerHeight = player.getY() + player.getHeight();
            double fallingRectangleWidth = fallingRectangle.getX() + fallingRectangle.getWidth();
            double playerWidth = player.getX() + player.getWidth();

            boolean playerLowerY = playerHeight - fallingRectangle.getY() < -tolerance;
            boolean inPlayersLeftX = Math.abs(player.getX() - (fallingRectangleWidth)) < radiusX;
            boolean inPlayersRightX = Math.abs(playerWidth - fallingRectangle.getX()) < radiusX;

            if (playerLowerY && (inPlayersLeftX || inPlayersRightX)) {
                Logger.info("The enemy object should be stopped: {}", fallingRectangle);
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * Calculates the coordinates of the enemies.
     *
     * @param enemies       the list that contains the living {@code enemies}
     * @param removeEnemies the list that will remove the died {@code enemies}
     * @param player        the actual {@code player} object
     */
    protected static void moveEnemy(List<FallingRectangle> enemies,
                                    List<FallingRectangle> removeEnemies, Player player) {
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).setY(enemies.get(i).getY() +
                (enemies.get(i).getVelocityY() * sixtyFpsSeconds));
            if (EngineMethods.stopEnemy(enemies.get(i), player)) {
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
     * Decides whether the {@code player} has reached the end of the level or not.
     *
     * @param player    the actual {@code player} object
     * @param levelEndY the end of the actual level
     * @return the {@code player} has reached the end of the level or not
     */
    public static boolean isEndGame(Player player, double levelEndY) {
        return player.getActualY() <= levelEndY;
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
     * @param player           the actual {@code player} object
     * @param leftReleased     the value of whether the left key is released or not
     * @param rightReleased    the value of whether the right key is released or not
     * @param borders          the list of the {@code borders}
     */
    protected static void calculatePlayerX(boolean leftCrashed, boolean rightCrashed,
                                           boolean leftCrashedLine, boolean rightCrashedLine,
                                           FallingRectangle enemyLeftPlayer,
                                           FallingRectangle enemyRightPlayer,
                                           Player player, boolean leftReleased, boolean rightReleased,
                                           List<Line> borders) {
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
                player.setX(borders.get(1).getStartX() + borders.get(1).getStrokeWidth() / 2
                    + tolerance);
            }
        }
    }

    /**
     * Calculates the {@code player}'s Y coordinate.
     *
     * @param crashed            whether the {@code player} crashed with something
     * @param standingOnEnemy    whether the {@code player} standing on {@code enemy}
     * @param standingOnLine     whether the {@code player} standing on {@code line}
     * @param enemy              the {@code enemy} that the {@code player} crashed with
     * @param player             the actual {@code player} object
     * @param borders            the list of the {@code borders}
     * @param steppedOnThisEnemy the value of whether the {@code player} stepped on the actual
     *                           {@code enemy or not}
     * @param upReleased         the value of whether the up key is released or not
     * @param steppedEnemies     the number of those {@code enemies} that the {@code player}
     *                           stepped on
     * @param actualVelocityY    the {@code player}'s actual Y velocity
     * @param collapsedTime      the elapsed time starting from the {@code player}
     *                           started jumping or falling
     * @param g                  a constant value, the gravitational acceleration
     */
    protected static void calculatePlayerY(boolean crashed, boolean standingOnEnemy,
                                           boolean standingOnLine, FallingRectangle enemy,
                                           Player player, List<Line> borders,
                                           BooleanProperty steppedOnThisEnemy, boolean upReleased,
                                           IntegerProperty steppedEnemies,
                                           DoubleProperty actualVelocityY,
                                           DoubleProperty collapsedTime, final double g) {
        if (crashed && standingOnEnemy) {
            if (!steppedOnThisEnemy.get()) {
                steppedOnThisEnemy.set(true);
                steppedEnemies.setValue(steppedEnemies.get() + 1);
            }
            player.setStartVelocityY(player.getStartVelocityY()
                + player.getDecreaseStartVelocityY());
            player.setStartY(enemy.getY() - tolerance);
            actualVelocityY.setValue(tolerance);
            player.setFalling(false);
            Logger.info("Player standing on enemy and crashed with enemy. " +
                "New Y velocity: {}", player.getStartVelocityY());
        } else if (crashed && standingOnLine) {
            player.setStartVelocityY(player.getStartVelocityY()
                + player.getDecreaseStartVelocityY());
            player.setStartY(borders.get(0).getStartY() - tolerance);
            actualVelocityY.setValue(tolerance);
            player.setFalling(false);
            steppedOnThisEnemy.set(false);
            Logger.info("Player standing on line and crashed with enemy. " +
                "New Y velocity: {}", player.getStartVelocityY());
        } else if (crashed && player.isJumping()) {
            player.setStartVelocityY(player.getStartVelocityY()
                + player.getDecreaseStartVelocityY());
            collapsedTime.setValue(0.0);
            player.setStartY(player.getActualY());
            double fallingVelocity = g / 2 * Math.pow(collapsedTime.get(), 2);
            actualVelocityY.setValue(tolerance);
            player.setActualY(player.getStartY() + (fallingVelocity));
            player.setFalling(true);
            steppedOnThisEnemy.set(false);
            Logger.info("Player was jumping when crashed with enemy. " +
                "New Y velocity: {}", player.getStartVelocityY());
        } else if (standingOnEnemy && !player.isJumping()) {
            if (!steppedOnThisEnemy.get()) {
                steppedOnThisEnemy.set(true);
                steppedEnemies.setValue(steppedEnemies.get() + 1);
            }
            player.setStartY(enemy.getY() - player.getHeight() - tolerance);
            actualVelocityY.setValue(tolerance);
            if (upReleased) {
                player.setFalling(false);
            } else {
                player.setJumping(true);
            }
        } else if (standingOnLine && !player.isJumping()) {
            player.setVelocityY(0.0);
            actualVelocityY.setValue(tolerance);
            player.setStartY(borders.get(0).getStartY() - player.getHeight() - tolerance);
            player.setFalling(false);
            steppedOnThisEnemy.set(false);
        } else if (player.isJumping() && !player.isFalling()) {
            double jumpingHeight = ((player.getStartVelocityY() * collapsedTime.get())
                + (g / 2 * Math.pow(collapsedTime.get(), 2)));
            actualVelocityY.setValue(player.getStartY() + jumpingHeight - player.getActualY());
            player.setActualY(player.getStartY() + jumpingHeight);
            player.setFalling(false);
            steppedOnThisEnemy.set(false);
        } else if (player.isFalling()) {
            double fallingVelocity = g / 2 * Math.pow(collapsedTime.get(), 2);
            actualVelocityY.setValue(player.getStartY() + fallingVelocity - player.getActualY());
            player.setActualY(player.getStartY() + (fallingVelocity));
            steppedOnThisEnemy.set(false);
        } else if (!standingOnLine && !standingOnEnemy) {
            double fallingVelocity = g / 2 * Math.pow(collapsedTime.get(), 2);
            actualVelocityY.setValue(tolerance);
            player.setActualY(player.getStartY() + (fallingVelocity));
            player.setFalling(true);
            steppedOnThisEnemy.set(false);
        }

        if (player.getActualY() + player.getHeight() > borders.get(0).getStartY() + tolerance) {
            player.setActualY(borders.get(0).getStartY() - player.getHeight() - tolerance);
            player.setStartY(player.getActualY());
            player.setY(player.getActualY());
            player.setJumping(false);
            player.setFalling(false);
            actualVelocityY.setValue(0.0);
        }

        if (player.isJumping() || player.isFalling()) {
            collapsedTime.setValue(collapsedTime.get() + sixtyFpsSeconds);
        } else {
            collapsedTime.setValue(0.0);
        }
    }

}
