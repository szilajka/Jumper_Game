package jumper.model.DB;

import javax.persistence.*;

@Entity
@Table(name = "score", schema = "jumper_app")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "level", nullable = false)
    private int level;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "velocity_y")
    private int velocityY;

    @Column(name = "user_name")
    private String userName;

    public Score() {
    }

    public Score(int id, int level, int score, int velocityY, String userName) {
        this.id = id;
        this.level = level;
        this.score = score;
        this.velocityY = velocityY;
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(int velocityY) {
        this.velocityY = velocityY;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
