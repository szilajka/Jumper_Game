package jumper.engine;

import jumper.helpers.EnemyType;
import jumper.model.FallingRectangle;
import jumper.model.Rect;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntersectionTest {

    private static Rect leftRect;
    private static Rect rightRect;
    private static Rect leftRectTolerance;
    private static Rect rightRectTolerance;
    private static Rect upperRect;
    private static Rect bottomRect;
    private static Rect upperRectTolerance;
    private static Rect bottomRectTolerance;
    private static double velocityX;
    private static double velocityY;

    @BeforeAll
    static void setup() {
        leftRectTolerance = new FallingRectangle(100.1, 200, 100, 20, EnemyType.BasicEnemy);
        rightRectTolerance = new FallingRectangle(200.3, 210, 100, 20, EnemyType.BasicEnemy);
        bottomRectTolerance = new FallingRectangle(50, 500.3, 100, 20, EnemyType.BasicEnemy);
        upperRectTolerance = new FallingRectangle(100, 479.9, 100, 20, EnemyType.BasicEnemy);
        leftRect = new FallingRectangle(90, 200, 100, 20, EnemyType.BasicEnemy);
        rightRect = new FallingRectangle(200, 210, 100, 20, EnemyType.BasicEnemy);
        bottomRect = new FallingRectangle(50, 500, 100, 20, EnemyType.BasicEnemy);
        upperRect = new FallingRectangle(100, 470, 100, 20, EnemyType.BasicEnemy);
        velocityX = 20.0;
        velocityY = 20.0;
    }

    @Test
    void leftIntersection() {
        //Only tolerance tests
        assertFalse(Intersection.leftIntersection(rightRect, leftRect),
            "Not tolerance instances should be false, left T.");
        assertTrue(Intersection.leftIntersection(rightRectTolerance, leftRectTolerance),
            "Tolerance instances should be true, left T.");
    }

    @Test
    void leftIntersectionVelocityX() {
        //Only velocityX tests
        assertTrue(Intersection.leftIntersection(rightRect, leftRect, velocityX),
            "Not tolerance instances should be true, left VX.");
        assertTrue(Intersection.leftIntersection(rightRectTolerance, leftRectTolerance, velocityX),
            "Tolerance instances should be true, left VX.");
        assertTrue(Intersection.leftIntersection(rightRectTolerance, leftRectTolerance, 0.0),
            "Tolerance instances should be true, left 0.0.");
        assertFalse(Intersection.leftIntersection(rightRect, leftRect, 0.0),
            "Not tolerance instances should be false, left 0.0.");
    }

    @Test
    void rightIntersection() {
        //Only tolerance tests
        assertFalse(Intersection.rightIntersection(leftRect, rightRect),
            "Not tolerance instances should be false, right T.");
        assertTrue(Intersection.rightIntersection(leftRectTolerance, rightRectTolerance),
            "Tolerance instances should be true, right T.");
    }

    @Test
    void rightIntersectionVelocityX() {
        //Only velocityX tests
        assertTrue(Intersection.rightIntersection(leftRect, rightRect, velocityX),
            "Not tolerance instances should be true, right VX.");
        assertTrue(Intersection.rightIntersection(leftRectTolerance, rightRectTolerance, velocityX),
            "Tolerance instances should be true, right VX.");
        assertTrue(Intersection.rightIntersection(leftRectTolerance, rightRectTolerance, 0.0),
            "Tolerance instances should be true, right 0.0.");
        assertFalse(Intersection.leftIntersection(leftRect, rightRect, 0.0),
            "Not tolerance instances should be false, right 0.0.");
    }

    @Test
    void upIntersection() {
        //Only tolerance tests
        assertFalse(Intersection.upIntersection(bottomRect, upperRect),
            "Not tolerance instances should be false, up T.");
        assertTrue(Intersection.upIntersection(bottomRectTolerance, upperRectTolerance),
            "Tolerance instances should be true, up T.");
    }

    @Test
    void upIntersectionVelocityY() {
        //Only velocityX tests
        assertTrue(Intersection.upIntersection(bottomRect, upperRect, velocityY),
            "Not tolerance instances should be true, up VX.");
        assertTrue(Intersection.upIntersection(bottomRectTolerance, upperRectTolerance, velocityY),
            "Tolerance instances should be true, up VX.");
        assertTrue(Intersection.upIntersection(bottomRectTolerance, upperRectTolerance, 0.0),
            "Tolerance instances should be true, up 0.0.");
        assertFalse(Intersection.upIntersection(bottomRect, upperRect, 0.0),
            "Not tolerance instances should be false, up 0.0.");
    }

    @Test
    void bottomIntersection() {
        //Only tolerance tests
        assertFalse(Intersection.bottomIntersection(upperRect, bottomRect),
            "Not tolerance instances should be false, bottom T.");
        assertTrue(Intersection.bottomIntersection(upperRectTolerance, bottomRectTolerance),
            "Tolerance instances should be true, bottom T.");
    }

    @Test
    void bottomIntersectionVelocityY() {
        //Only velocityX tests
        assertTrue(Intersection.bottomIntersection(upperRect, bottomRect, velocityY),
            "Not tolerance instances should be true, bottom VX.");
        assertTrue(Intersection.bottomIntersection(upperRectTolerance, bottomRectTolerance, velocityY),
            "Tolerance instances should be true, bottom VX.");
        assertTrue(Intersection.bottomIntersection(upperRectTolerance, bottomRectTolerance, 0.0),
            "Tolerance instances should be true, bottom 0.0.");
        assertFalse(Intersection.upIntersection(upperRect, bottomRect, 0.0),
            "Not tolerance instances should be false, bottom 0.0.");
    }

    @AfterAll
    static void tearDown() {
        leftRect = null;
        rightRect = null;
        velocityX = 0.0;
        velocityY = 0.0;

    }
}
