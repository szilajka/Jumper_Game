package jumper.model.DB;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.apache.commons.codec.binary.Hex;

/**
 * This class is the model of the {@code User} database table.
 */
@Entity
@Table(name = "jumper_user", schema = "jumper_app")
public class User implements Serializable {
    /**
     * The {@code username} of the current {@code User}.
     *
     * This is the identifier of the {@code User} class.
     */
    @Id
    @Column(name = "name", nullable = false)
    private String userName;
    /**
     * The {@code user}'s {@code hashed password}.
     *
     * This password is encrypted.
     */
    @Column(name = "hashed_password")
    private String hashedPassword;

    /**
     * The {@code salt} that is used in the hashing process.
     */
    @Column(name = "salt")
    private String salt;

    /**
     * The {@link List} of the {@link Score} objects that belongs to the current {@code user}.
     */
    @OneToMany(targetEntity = Score.class, mappedBy = "userName")
    private List<Score> score;

    /**
     * The {@link AllTime} object that belongs to the current {@code user}.
     */
    @OneToOne(mappedBy = "userName")
    private AllTime allTime;

    /**
     * An empty constructor of the class.
     */
    public User() {
    }

    /**
     * A constructor of the class.
     *
     * @param userName       the {@code username} to be set
     * @param hashedPassword the {@code hashed password} to be set
     * @param salt           the {@code salt} that is used to generate the hashed password
     * @param score          the {@code score list} to be set to the {@code user}
     * @param allTime        the {@code AllTime} object to be set
     */
    public User(String userName, String hashedPassword, String salt, List<Score> score, AllTime allTime) {
        this.userName = userName;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.score = score;
        this.allTime = allTime;
    }

    /**
     * Gets the {@code username} of the {@code User}.
     *
     * @return the {@code username} of the {@code User}
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the {@code username} of the {@code User}.
     *
     * @param userName the {@code username} to be set to the {@code User}.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the {@code hashed password} of the {@code User}.
     *
     * @return the {@code hashed password} of the {@code User}.
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

    /**
     * Sets the {@code hashed password} of the {@code User}.
     *
     * @param hashedPassword the {@code byte array} of the {@code hashed password}
     *                       to be set to the {@code User}.
     */
    public void setHashedPassword(byte[] hashedPassword) {
        this.hashedPassword = String.valueOf(Hex.encodeHex(hashedPassword));
    }

    /**
     * Sets the {@code hashed password} of the {@code User}.
     *
     * @param hashedPassword the {@code String} value of the {@code hashed password}
     *                       to be set to the {@code User}.
     */
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    /**
     * Gets the {@code salt} that is used to generate the {@code User's hashed password}.
     *
     * @return the {@code salt} that is used to generate the {@code User's hashed password}
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Sets the {@code salt} that is used to generate the {@code User's hashed password}.
     *
     * @param salt the {@code byte array} form of the {@code salt} to be set
     */
    public void setSalt(byte[] salt) {
        this.salt = String.valueOf(Hex.encodeHex(salt));
    }

    /**
     * Sets the {@code salt} that is used to generate the {@code User's hashed password}.
     *
     * @param salt the {@code String} value of the {@code salt} to be set
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * Gets the {@code score list} of the current {@code user}.
     *
     * @return the {@code score list} of the current {@code user}
     */
    public List<Score> getScore() {
        return score;
    }

    /**
     * Sets the {@code score list} of the current {@code user}.
     *
     * @param score the {@code score list} to be set to the current {@code user}.
     */
    public void setScore(List<Score> score) {
        this.score = score;
    }

    /**
     * Gets the {@code All Time} object that belongs to the current {@code user}.
     *
     * @return the {@code All Time} object that belongs to the current {@code user}
     */
    public AllTime getAllTime() {
        return allTime;
    }

    /**
     * Sets the {@code All Time} object that belongs to the current {@code user}.
     *
     * @param allTime the {@code all time} object to be set to the current {@code user}
     */
    public void setAllTime(AllTime allTime) {
        this.allTime = allTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(userName, user.userName) &&
            Objects.equals(hashedPassword, user.hashedPassword) &&
            Objects.equals(salt, user.salt) &&
            Objects.equals(score, user.score) &&
            Objects.equals(allTime, user.allTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, hashedPassword, salt, score, allTime);
    }

    @Override
    public String toString() {
        return "User{" +
            "userName='" + userName + '\'' +
            ", hashedPassword='" + hashedPassword + '\'' +
            ", salt='" + salt + '\'' +
            ", score=" + score +
            ", allTime=" + allTime +
            '}';
    }
}
