package jumper.model.DB;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "all_time", schema = "jumper_app")
public class AllTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int id;

    @OneToOne
    @JoinColumn(name = "user_name", referencedColumnName = "name")
    public User userName;

    @Column(name = "elapsed_time")
    private int elapsedTime;

    public AllTime() {
    }

    public AllTime(int id, User userName, int elapsedTime) {
        this.id = id;
        this.userName = userName;
        this.elapsedTime = elapsedTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUserName() {
        return userName;
    }

    public void setUserName(User userName) {
        this.userName = userName;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

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
