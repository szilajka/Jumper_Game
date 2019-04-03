package jumper.queries;

import jumper.model.DB.AllTime;
import jumper.model.DB.Score;
import jumper.model.DB.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class Queries {
    public static User getUserByUserName(EntityManager em, String userName) {
        if (!userName.trim().equals("")) {
            TypedQuery<User> userQuery = em.createQuery("SELECT u FROM User u " +
                    "LEFT JOIN Score s ON u.userName = s.userName " +
                    "LEFT JOIN AllTime at ON at.userName = u.userName " +
                    "WHERE u.userName = :name", User.class).setParameter("name", userName);
            return userQuery.getSingleResult();
        } else {
            return null;
        }
    }

    public static Score getScoreByUserName(EntityManager em, User user) {
        if (user != null) {
            TypedQuery<Score> scoreQuery = em.createQuery("SELECT s FROM Score s " +
                    "LEFT JOIN User u ON s.userName = u.userName " +
                    "WHERE s IS NOT NULL AND s.userName IS NOT NULL AND " +
                    "s.userName.userName IS NOT NULL AND s.userName = :name " +
                    "ORDER BY s.id desc", Score.class).setParameter("name", user.getUserName());
            var scores = scoreQuery.getResultList();
            return scores.isEmpty() ? null : scores.get(0);
        } else {
            return null;
        }
    }

    public static AllTime getAllTimeByUserName(EntityManager em, User user) {
        TypedQuery<AllTime> atQuery = em.createQuery("SELECT at FROM AllTime AS at " +
                "WHERE at.userName = :name " +
                "ORDER BY at.id desc", AllTime.class);
        var allTimes = atQuery.setParameter("name", user).getResultList();
        return allTimes.isEmpty() ? null : allTimes.get(0);
    }

    public static List<Score> getTopTenScoreBoard(EntityManager em) {
        TypedQuery<Score> sbQuery = em.createQuery("SELECT s FROM Score s " +
                "LEFT JOIN User u ON u.userName = s.userName " +
                "LEFT JOIN AllTime at ON at.userName = u.userName " +
                "ORDER BY s.score desc", Score.class)
                .setMaxResults(10);
        return sbQuery.getResultList();
    }

}
