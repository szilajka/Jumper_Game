package jumper.engine;

import javafx.scene.Camera;
import javafx.scene.ParallelCamera;
import jumper.helpers.EnemyType;
import jumper.model.FallingRectangle;
import jumper.model.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EngineMethodsTest {

    private static Player player;
    private static double levelEndY;
    private static FallingRectangle fr;
    private static FallingRectangle frS;

    @BeforeAll
    static void setup() {
        levelEndY = 500;
        player = new Player(100, 100, 100, 100);
        fr = new FallingRectangle(300, 300, 100, 20, EnemyType.BasicEnemy);
        frS = new FallingRectangle(300, 300, 100, 20, EnemyType.SpikeEnemy);
    }

    @Test
    void generateEnemy() {
        player.setX(100.0);
        player.setY(800.0);
        player.setActualY(800.0);
        Camera camera = new ParallelCamera();
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

        assertNotNull(EngineMethods.generateEnemy(3, 0, player, 1,
                camera, 150, 200, null, 500),
                "Null pointer latest enemy, should not return null");

        camera.setLayoutY(1000.0);
        fr = new FallingRectangle(100.0, 1000.0, 100, 20, EnemyType.BasicEnemy);
        player.setY(810);
        player.setActualY(810);

        frS = EngineMethods.generateEnemy(3, 0, player, 1,
                camera, 150, 200, fr, 500);
        assertNotEquals(player.getActualY(), frS.getStartY(), "level 3, eG: 0, eS: 1, " +
                "generated enemy should not be in player.");
        assertEquals(EnemyType.BasicEnemy, frS.getEnemyType(), "level 3, eG: 0, " +
                "Enemy should be Basic Enemy");
        assertEquals(FallingRectangle.basicEnemyWidth, frS.getWidth(), "enemy width should be " +
                "basic enemy width");
        assertEquals(FallingRectangle.basicEnemyHeight, frS.getHeight(), "enemy height should be " +
                "basic enemy height");


        fr.setX(600.0);
        frS = EngineMethods.generateEnemy(3, 1, player, 1,
                camera, 150, 200, fr, 500);
        assertEquals(EnemyType.SpikeEnemy, frS.getEnemyType(), "Enemy should be spike enemy");
        assertEquals(150.0, frS.getX(), "enemy X should be equal to enemyDistanceX");
        assertEquals(FallingRectangle.spikeEnemyWidth, frS.getWidth(), "enemy width should be " +
                "spike enemy width");
        assertEquals(FallingRectangle.spikeEnemyHeight, frS.getHeight(), "enemy height should be " +
                "spike enemy height");

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

    /*@Test
    void moveEnemy() {
    }*/

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

    /*@Test
    void calculatePlayerX() {
    }

    @Test
    void calculatePlayerY() {
    }*/

    @AfterAll
    static void tearDown() {
        player = null;
    }
}
