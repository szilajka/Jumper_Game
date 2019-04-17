package jumper.queries;

import jumper.model.DB.AllTime;
import jumper.model.DB.Score;
import jumper.model.DB.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

import org.tinylog.Logger;

/**
 * This class contains queries to the game.
 */
public class Queries {
    /**
     * Finds the {@link User} by the given username.
     *
     * @param em       an {@link EntityManager} that connects to the database
     * @param userName the username that will be searched
     * @return the user if it is found, else null
     */
    public static User getUserByUserName(EntityManager em, String userName) {
        try {
            if (!userName.trim().equals("")) {
                TypedQuery<User> userQuery = em.createQuery("SELECT u FROM User u " +
                    "LEFT JOIN Score s ON u.userName = s.userName " +
                    "LEFT JOIN AllTime at ON at.userName = u.userName " +
                    "WHERE u.userName = :name", User.class).setParameter("name", userName);
                return userQuery.getSingleResult();
            } else {
                throw new NoResultException();
            }
        } catch (NoResultException noRes) {
            Logger.debug("No user found with {} username.", userName);
            return null;
        }
    }

    /**
     * Finds the {@link Score} by the given {@link User}.
     *
     * @param em   an {@link EntityManager} that connects to the database
     * @param user the {@code user} that will be searched
     * @return the {@code score} if it is found, else null
     */
    public static Score getScoreByUserName(EntityManager em, User user) {
        try {
            if (user != null) {
                TypedQuery<Score> scoreQuery = em.createQuery("SELECT s FROM Score s " +
                    "LEFT JOIN User u ON s.userName = u.userName " +
                    "WHERE s IS NOT NULL AND s.userName IS NOT NULL AND " +
                    "s.userName.userName IS NOT NULL AND s.userName = :name " +
                    "ORDER BY s.id desc", Score.class).setParameter("name", user.getUserName());
                var scores = scoreQuery.getResultList();
                return scores.isEmpty() ? null : scores.get(0);
            } else {
                throw new NullPointerException();
            }
        } catch (NoResultException noRes) {
            Logger.debug("No score found with {} username.", user.getUserName());
            return null;
        } catch (NullPointerException np) {
            Logger.debug("User is null.");
            return null;
        }

    }

    /**
     * Finds the {@link AllTime} by the given {@link User}.
     *
     * @param em   an {@link EntityManager} that connects to the database
     * @param user the {@code user} that will be searched
     * @return the {@code all time} if it is found, else null
     */
    public static AllTime getAllTimeByUserName(EntityManager em, User user) {
        try {
            if (user != null) {
                TypedQuery<AllTime> atQuery = em.createQuery("SELECT at FROM AllTime AS at " +
                    "WHERE at.userName = :name " +
                    "ORDER BY at.id desc", AllTime.class);
                var allTimes = atQuery.setParameter("name", user).getResultList();
                return allTimes.isEmpty() ? null : allTimes.get(0);
            } else {
                throw new NullPointerException();
            }
        } catch (NoResultException noRes) {
            Logger.debug("No score found with {} username.", user.getUserName());
            return null;
        } catch (NullPointerException np) {
            Logger.debug("User is null.");
            return null;
        }
    }

    /**
     * Finds the top 10 {@link Score}s.
     *
     * @param em an {@link EntityManager} that connects to the database
     * @return the {@code scores} if they are found, else null
     */
    public static List<Score> getTopTenScoreBoard(EntityManager em) {
        try {
            TypedQuery<Score> sbQuery = em.createQuery("SELECT s FROM Score s " +
                "LEFT JOIN User u ON u.userName = s.userName " +
                "LEFT JOIN AllTime at ON at.userName = u.userName " +
                "ORDER BY s.score desc", Score.class)
                .setMaxResults(10);
            return sbQuery.getResultList();
        } catch (NoResultException noRes) {
            Logger.debug("No Score is stored in database.");
            return null;
        }
    }

}
