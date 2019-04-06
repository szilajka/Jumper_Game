package jumper.engine;

import javafx.scene.Camera;
import javafx.scene.ParallelCamera;
import javafx.scene.shape.Line;
import jumper.helpers.EnemyType;
import jumper.model.FallingRectangle;
import jumper.model.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static jumper.helpers.GameEngineHelper.sixtyFpsSeconds;
import static org.junit.jupiter.api.Assertions.*;

class EngineMethodsTest {

    private static Player player;
    private static double levelEndY;
    private static FallingRectangle fr;
    private static FallingRectangle frS;
    private static FallingRectangle frRemove;
    private static FallingRectangle frStop;
    private static Camera camera;
    private static List<FallingRectangle> enemies;
    private static List<FallingRectangle> remove;
    private static List<Line> borders;

    @BeforeAll
    static void setup() {
        levelEndY = 500;
        player = new Player(100, 100, 100, 100);
        fr = new FallingRectangle(300, 1000, 100, 20, EnemyType.BasicEnemy);
        frS = new FallingRectangle(300, 300, 100, 20, EnemyType.SpikeEnemy);
        frRemove = new FallingRectangle(200, 900, 100, 20, EnemyType.BasicEnemy);
        frStop = new FallingRectangle(300, 1000, 100, 20, EnemyType.BasicEnemy);
        camera = new ParallelCamera();
        enemies = new ArrayList<>();
        remove = new ArrayList<>();
        borders = new ArrayList<>();
        Line baseLine = new Line(0.0, 2000, 800,
                2000);
        Line leftLine = new Line(0.0, 0.0, 0.0, 2000);
        Line rightLine = new Line(800, 0.0, 800,
                2000);

        baseLine.setStrokeWidth(3);
        leftLine.setStrokeWidth(100);
        rightLine.setStrokeWidth(100);


        borders.addAll(Arrays.asList(baseLine, leftLine, rightLine));
    }

    @Test
    void generateEnemyLevel1() {
        player.setX(100.0);
        player.setY(800.0);
        player.setActualY(800.0);
        camera.setLayoutY(500.0);
        frS = EngineMethods.generateEnemy(1, 0, player, 0, camera,
                150, 200, null, 500);
        assertEquals(EnemyType.BasicEnemy, frS.getEnemyType(), "level 1 type should be BasicEnemy");
        assertNotEquals(player.getY(), frS.getStartY(), "enemy should not be generated in player.");
        player.setActualY(290);
        player.setActualY(290);
        frS = EngineMethods.generateEnemy(1, 0, player, 0, camera,
                150, 200, null, 500);
        assertNotEquals(player.getY(), frS.getStartY(), "Enemy should not be generated in player," +
                "it should be lower.");
    }

    @Test
    void generateEnemyLevel3() {
        assertNotNull(EngineMethods.generateEnemy(3, 0, player, 1,
                camera, 150, 200, null, 500),
                "Null pointer latest enemy, should not return null");

        camera.setLayoutY(1000.0);
        fr.setX(100.0);
        player.setY(810);
        player.setActualY(810);

        frS = EngineMethods.generateEnemy(3, 0, player, 1,
                camera, 150, 200, fr, 500);
        assertNotEquals(player.getActualY(), frS.getStartY(), "generated enemy should not be in player.");
        assertEquals(EnemyType.BasicEnemy, frS.getEnemyType(), "Enemy should be Basic Enemy");
        assertEquals(FallingRectangle.basicEnemyWidth, frS.getWidth(), "enemy width should be " +
                "basic enemy width");
        assertEquals(FallingRectangle.basicEnemyHeight, frS.getHeight(), "enemy height should be " +
                "basic enemy height");


        fr.setX(600.0);
        frS = EngineMethods.generateEnemy(4, 1, player, 1,
                camera, 150, 200, fr, 500);
        assertEquals(EnemyType.SpikeEnemy, frS.getEnemyType(), "Enemy should be spike enemy");
        assertEquals(150.0, frS.getX(), "enemy X should be equal to enemyDistanceX");
        assertEquals(FallingRectangle.spikeEnemyWidth, frS.getWidth(), "enemy width should be " +
                "spike enemy width");
        assertEquals(FallingRectangle.spikeEnemyHeight, frS.getHeight(), "enemy height should be " +
                "spike enemy height");
    }

    @Test
    void generateEnemyLevel5() {
        fr.setY(600.0);
        player.setY(600.0);
        player.setActualY(600.0);
        frS = EngineMethods.generateEnemy(5, 0, player, 3,
                camera, 150, 200, fr, 500);
        assertNotEquals(player.getActualY(), frS.getStartY(), "level 5, levelEndY triggered," +
                "enemy should not be generated into player");
        assertEquals(EnemyType.SpikeEnemy, frS.getEnemyType(), "level 5, enemy type should be " +
                "Spike Enemy.");
    }

    @Test
    void stopEnemy() {
        fr.setX(300.0);
        fr.setY(300.0);
        player.setX(199.6);
        player.setY(199);
        player.setActualY(199);
        assertTrue(EngineMethods.stopEnemy(fr, player), "Enemy should stop, pX: 199.6, eX: 300.0");
        player.setX(500.1);
        assertTrue(EngineMethods.stopEnemy(fr, player), "Enemy should stop, pX: 500.1, eX: 300.0");
        player.setActualY(321.0);
        player.setY(321.0);
        assertFalse(EngineMethods.stopEnemy(fr, player), "Enemy should not stop, pY: 321, eY: 300.0");
        player.setX(300.0);
        assertFalse(EngineMethods.stopEnemy(fr, player), "Enemy should not stop, px: 300.0, " +
                "pY: 321, eY: 300.0");
        fr.setVelocityY(0.0);
        assertTrue(EngineMethods.stopEnemy(fr, player), "Enemy should stop, velocityY: 0.0");
    }

    @Test
    void emptyEnemies() {
        enemies.clear();
        remove.clear();
        EngineMethods.moveEnemy(enemies, remove, player);
        assertEquals(0, remove.size(), "Empty enemy list should not trigger add methods.");
        enemies.clear();
        remove.clear();
    }

    @Test
    void noStop() {
        enemies.clear();
        remove.clear();
        frS.setX(100.0);
        frS.setY(1000.0);
        player.setX(210.0);
        player.setY(990);
        enemies.add(frS);
        EngineMethods.moveEnemy(enemies, remove, player);
        assertNotEquals(0, enemies.get(0).getVelocityY(), "First enemy should not be stopped.");
        enemies.clear();
        remove.clear();
    }

    @Test
    void stopMovingEnemies() {
        enemies.clear();
        remove.clear();
        frStop.setX(100.0);
        frStop.setY(1000.0);
        player.setX(210.0);
        player.setY(890);
        enemies.add(frStop);
        EngineMethods.moveEnemy(enemies, remove, player);
        assertEquals(0, enemies.get(0).getVelocityY(), "First enemy should be stopped.");
        enemies.clear();
        remove.clear();
    }

    @Test
    void removeCrashedEnemy() {
        enemies.clear();
        remove.clear();
        frStop.setX(100.0);
        frStop.setY(1000.0);
        player.setX(210.0);
        player.setY(890.0);
        enemies.add(frRemove);
        EngineMethods.moveEnemy(enemies, remove, player);
        assertEquals(1, remove.size(), "Removable enemy should be added to remove list.");
        enemies.clear();
        remove.clear();
    }

    @Test
    void breakCollapsingEnemies() {
        enemies.clear();
        remove.clear();
        player.setX(600.0);
        player.setY(1200.0);
        frStop.setX(100);
        frStop.setY(800.0);
        frRemove.setX(120);
        frRemove.setY(790);
        enemies.add(frStop);
        enemies.add(frRemove);
        EngineMethods.moveEnemy(enemies, remove, player);
        assertEquals(1, remove.size(), "First enemy should be removed.");
        assertNotEquals(0, enemies.get(1).getVelocityY(), "Second enemy should falling.");
        enemies.clear();
        remove.clear();
    }

    @Test
    void isEndGame() {
        player.setX(100.0);
        player.setY(levelEndY);
        player.setActualY(levelEndY);
        assertTrue(EngineMethods.isEndGame(player, levelEndY), "levelEndY value should be equal to levelEndY value.");
        player.setY(499.9);
        player.setActualY(499.9);
        assertTrue(EngineMethods.isEndGame(player, levelEndY), "player.actualY should be smaller than levelEndY");
        player.setY(500.1);
        player.setActualY(500.1);
        assertFalse(EngineMethods.isEndGame(player, levelEndY), "player.actualY should be higher than levelEndY");
    }

    @Test
    void calculatePlayerXWithoutCrash() {
        player.setVelocityX(50.0);
        player.setX(100);
        player.setY(1000);
        player.setActualY(1000);
        player.setVelocityY(-50.0);
        double pX = player.getX();
        EngineMethods.calculatePlayerX(false, false, false, false,
                null, null, player, false, true, null);
        assertNotEquals(pX, player.getX(), "Player should be moved to left");
        pX = player.getX();
        EngineMethods.calculatePlayerX(false, false, false, false,
                null, null, player, true, false, null);
        assertNotEquals(pX, player.getX(), "Player should be moved to right");
    }

    @Test
    void calculatePlayerXWithLeftCrash() {
        player.setVelocityX(50.0);
        player.setX(100);
        player.setY(1000);
        player.setActualY(1000);
        player.setVelocityY(-50.0);
        EngineMethods.calculatePlayerX(true, false, false, false,
                null, null, player, false, true, borders);
        assertEquals(borders.get(1).getStartX() + borders.get(1).getStrokeWidth() / 2 + 0.5,
                player.getX(), "Player X should be the left line + tolerance");
        player.setX(300);
        fr.setX(200);
        EngineMethods.calculatePlayerX(true, false, true, false,
                fr, null, player, false, true, null);
        assertEquals(fr.getX() + fr.getWidth() + 0.5,
                player.getX(), "Player X should be enemy width + tolerance");
    }

    @Test
    void calculatePlayerXWithRightCrash() {
        player.setVelocityX(50.0);
        player.setX(600);
        player.setY(1000);
        player.setActualY(1000);
        player.setVelocityY(-50.0);
        EngineMethods.calculatePlayerX(false, true, false, false,
                null, null, player, true, false, borders);
        assertEquals(borders.get(2).getStartX() - borders.get(2).getStrokeWidth() / 2 -
                        player.getWidth() - 0.5, player.getX(),
                "Player width should be the right line - tolerance");
        player.setX(300);
        fr.setX(400);
        EngineMethods.calculatePlayerX(false, true, false, false,
                null, fr, player, true, false, null);
        assertEquals(fr.getX() - 0.5,
                player.getX() + player.getWidth(), "Player width should be " +
                        "enemy X - tolerance");
    }

    @Test
    void calculatePlayerXWithCrashesReleases() {
        player.setX(300);
        player.setVelocityX(-50);
        double sixtyVX = -50 * sixtyFpsSeconds;
        EngineMethods.calculatePlayerX(false, true, false,
                false, null, null, player, false,
                true, null);
        assertEquals(300 + sixtyVX, player.getX(), "Player X should be moved to left.");
        player.setX(300);
        player.setVelocityX(50);
        sixtyVX = 50 * sixtyFpsSeconds;
        EngineMethods.calculatePlayerX(true, false, false,
                false, null, null, player, true,
                false, null);
        assertEquals(300 + sixtyVX, player.getX(), "Player X should be moved to right.");
    }

    @Test
    void calculatePlayerXTwoCrashes() {
        player.setX(500);
        fr.setX(300);
        EngineMethods.calculatePlayerX(true, true, true,
                false, fr, null, player,
                false, false, borders);
        assertEquals(borders.get(1).getStartX() + borders.get(1).getStrokeWidth() / 2 + 0.5,
                player.getX(), "Player X should be the left line + tolerance.");

        EngineMethods.calculatePlayerX(true, true, false,
                true, fr, null, player,
                false, false, borders);
        assertEquals(borders.get(2).getStartX() - borders.get(2).getStrokeWidth() / 2 -
                        player.getWidth() - 0.5, player.getX(),
                "Player width should be the right line - tolerance");
        EngineMethods.calculatePlayerX(true, true, false,
                false, fr, null, player,
                false, false, borders);
        assertEquals(fr.getX() + fr.getWidth() + 0.5, player.getX(),
                "Player x should be enemy width + tolerance.");
        EngineMethods.calculatePlayerX(true, true, false,
                false, null, fr, player,
                false, false, borders);
        assertEquals(borders.get(1).getStartX() + borders.get(1).getStrokeWidth() / 2 + 0.5,
                player.getX(), "Player X should be left wall + tolerance");
    }

    /*@Test
    void calculatePlayerY() {
    }*/

    @AfterAll
    static void tearDown() {
        player = null;
        fr = null;
        frS = null;
        frRemove = null;
        frStop = null;
        camera = null;
        enemies = null;
        remove = null;
    }
}
