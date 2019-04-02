package jumper.Queries;

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

import jumper.model.DB.AllTime;
import jumper.model.DB.Score;
import jumper.model.DB.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

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

}
