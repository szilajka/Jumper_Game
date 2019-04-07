package jumper.model.DB;

import javax.persistence.*;
import java.util.Objects;

/**
 * This class is the model of the {@code All time} database table.
 */
@Entity
@Table(name = "all_time")
public class AllTime {
    /**
     * The id of the stored or created {@code AllTime} object.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "at_id")
    public int id;

    /**
     * The username of the {@link User} that this time belongs to.
     */
    @OneToOne
    @JoinColumn(name = "user_name", referencedColumnName = "u_name")
    public User userName;

    /**
     * The stored time that the {@link User} spent in this game.
     */
    @Column(name = "elapsed_time")
    private int elapsedTime;

    /**
     * An empty constructor of the class.
     */
    public AllTime() {
    }

    /**
     * A constructor of the class.
     *
     * @param id          the {@code id} that identify the current {@code AllTime} object
     * @param userName    the username of the {@link User} that this {@code AllTime} belongs to
     * @param elapsedTime the time that the {@link User} spent in this game
     */
    public AllTime(int id, User userName, int elapsedTime) {
        this.id = id;
        this.userName = userName;
        this.elapsedTime = elapsedTime;
    }

    /**
     * Gets the {@code AllTime}'s identifier.
     *
     * @return the id of the current {@code AllTime} object
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the {@code AllTime}'s identifier.
     *
     * @param id the id to be set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the {@link User} that belongs to this {@code AllTime} object.
     *
     * @return the {@link User} that belongs to this {@code AllTime} object
     */
    public User getUserName() {
        return userName;
    }

    /**
     * Sets the {@link User} that belongs to this {@code AllTime} object.
     *
     * @param userName the {@link User} that belongs to this {@code AllTime} object
     */
    public void setUserName(User userName) {
        this.userName = userName;
    }

    /**
     * Gets the time that the {@link User} spent in this game.
     *
     * @return the time that the {@link User} spent in this game
     */
    public int getElapsedTime() {
        return elapsedTime;
    }

    /**
     * Sets the time that the {@link User} spent in this game.
     *
     * @param elapsedTime the time that the {@link User} spent in this game
     */
    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AllTime allTime = (AllTime) o;
        return id == allTime.id &&
            elapsedTime == allTime.elapsedTime &&
            Objects.equals(userName, allTime.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, elapsedTime);
    }

    @Override
    public String toString() {
        return "AllTime{" +
            "id=" + id +
            ", userName=" + userName +
            ", elapsedTime=" + elapsedTime +
            '}';
    }
}
