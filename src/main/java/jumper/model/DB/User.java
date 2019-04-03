package jumper.model.DB;

import org.apache.commons.codec.binary.Hex;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "jumper_user", schema = "jumper_app")
public class User implements Serializable {
    @Id
    @Column(name = "name", nullable = false)
    private String userName;

    @Column(name = "hashed_password")
    private String hashedPassword;

    @Column(name = "salt")
    private String salt;

    @OneToMany(targetEntity = Score.class, mappedBy = "userName")
    private List<Score> score;

    @OneToOne(mappedBy = "userName")
    private AllTime allTime;

    public User() {
    }

    public User(String userName, String hashedPassword, String salt, List<Score> score, AllTime allTime) {
        this.userName = userName;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.score = score;
        this.allTime = allTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(byte[] hashedPassword) {
        this.hashedPassword = String.valueOf(Hex.encodeHex(hashedPassword));
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = String.valueOf(Hex.encodeHex(salt));
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<Score> getScore() {
        return score;
    }

    public void setScore(List<Score> score) {
        this.score = score;
    }

    public AllTime getAllTime() {
        return allTime;
    }

    public void setAllTime(AllTime allTime) {
        this.allTime = allTime;
    }
}
