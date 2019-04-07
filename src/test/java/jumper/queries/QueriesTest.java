package jumper.queries;

import jumper.model.DB.AllTime;
import jumper.model.DB.Score;
import jumper.model.DB.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QueriesTest {

    private static EntityManagerFactory emf;
    private EntityManager em;

    @BeforeAll
    static void setup() {
        emf = Persistence.createEntityManagerFactory("jumper");
    }

    @Test
    void getUserByUserName() {
        em = emf.createEntityManager();
        User foundUser = Queries.getUserByUserName(em, "testuser");
        assertNotNull(foundUser, "testuser should not be null.");
        assertEquals("testuser", foundUser.getUserName(), "testuser username should be testuser.");
        User nullUser = Queries.getUserByUserName(em, "");
        assertNull(nullUser, "No username should be null.");
        User anotherNullUser = Queries.getUserByUserName(em, "notrealuser");
        assertNull(anotherNullUser, "notrealuser username should be null.");
        em.close();
    }

    @Test
    void getScoreByUserName() {
        em = emf.createEntityManager();
        User foundUser = Queries.getUserByUserName(em, "testuser");
        Score foundScore = Queries.getScoreByUserName(em, foundUser);
        assertNotNull(foundScore, "testuser should have score.");
        assertEquals(1234, foundScore.getScore(), "testuser score should be 1234.");
        assertEquals(10, foundScore.getLevel(), "testuser level should be 10.");
        Score nullSCore = Queries.getScoreByUserName(em, null);
        assertNull(nullSCore, "null should not have any score.");
        em.close();
    }

    @Test
    void getAllTimeByUserName() {
        em = emf.createEntityManager();
        User foundUser = Queries.getUserByUserName(em, "testuser");
        AllTime foundAllTime = Queries.getAllTimeByUserName(em, foundUser);
        assertEquals(37230, foundAllTime.getElapsedTime(), "testuser alltime should be 37230.");
        AllTime nullAllTime = Queries.getAllTimeByUserName(em, null);
        assertNull(nullAllTime, "null should not have any allTime.");
        em.close();
    }

    @Test
    void getTopTenScoreBoard() {
        em = emf.createEntityManager();
        List<Score> foundSL = Queries.getTopTenScoreBoard(em);
        assertNotNull(foundSL);
        assertNotEquals(0, foundSL.size(), "Scoreboard size should be greater, than 0.");
        em.close();
    }

    @AfterAll
    static void tearDown() {
        emf.close();
    }
}
