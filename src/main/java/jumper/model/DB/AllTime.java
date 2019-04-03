package jumper.model.DB;

import javax.persistence.*;
import java.time.Duration;

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
}
