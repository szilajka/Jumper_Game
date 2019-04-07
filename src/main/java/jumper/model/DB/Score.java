package jumper.model.DB;

import javax.persistence.*;
import java.util.Objects;

/**
 * This class is the model of the {@code Score} database table.
 */
@Entity
@Table(name = "score", schema = "jumper_app")
public class Score {
    /**
     * The id of the stored or created {@code Score} object.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;
    /**
     * The level that the {@link User} is actually playing.
     */
    @Column(name = "level", nullable = false)
    private int level;
    /**
     * The score that the {@link User} achieved.
     */
    @Column(name = "score", nullable = false)
    private int score;

    /**
     * The {@link jumper.model.Player} object's actual Y velocity.
     */
    @Column(name = "velocity_y")
    private int velocityY;

    /**
     * The username of the logged in {@link User}.
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * An empty constructor of the class.
     */
    public Score() {
    }

    /**
     * A constructor of the class.
     *
     * @param id        the {@code id} that identify the current {@code Score} object
     * @param level     the {@code level} number where the {@link User} continue the game
     * @param score     the achieved {@code score} by the {@link User}
     * @param velocityY the {@link jumper.model.Player}'s actual Y velocity
     * @param userName  the {@code username} of the current {@link User}
     */
    public Score(int id, int level, int score, int velocityY, String userName) {
        this.id = id;
        this.level = level;
        this.score = score;
        this.velocityY = velocityY;
        this.userName = userName;
    }

    /**
     * Gets the {@code Score}'s identifier.
     *
     * @return the id of the current {@code Score}
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the {@code Score}'s identifier.
     *
     * @param id the {@code id} value to be set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the {@code Score} object's current level number.
     *
     * @return the {@code Score} object's current level number
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the {@code Score} object's current level number.
     *
     * @param level the {@code level} number to be set
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Gets the {@link User}'s score.
     *
     * @return the {@link User}'s score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the {@link User}'s score.
     *
     * @param score the {@code score} to be set
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Gets the {@link jumper.model.Player}'s {@code Y velocity}.
     *
     * @return the {@link jumper.model.Player}'s {@code Y velocity}
     */
    public int getVelocityY() {
        return velocityY;
    }

    /**
     * Sets the {@link jumper.model.Player}'s {@code Y velocity}.
     *
     * @param velocityY the {@link jumper.model.Player}'s {@code Y velocity} value to be set
     */
    public void setVelocityY(int velocityY) {
        this.velocityY = velocityY;
    }

    /**
     * Gets the {@link User}'s username.
     *
     * @return the {@link User}'s username.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the {@link User}'s username.
     *
     * @param userName the {@code username} to be set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Score score1 = (Score) o;
        return id == score1.id &&
            level == score1.level &&
            score == score1.score &&
            velocityY == score1.velocityY &&
            Objects.equals(userName, score1.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, level, score, velocityY, userName);
    }

    @Override
    public String toString() {
        return "Score{" +
            "id=" + id +
            ", level=" + level +
            ", score=" + score +
            ", velocityY=" + velocityY +
            ", userName='" + userName + '\'' +
            '}';
    }
}
