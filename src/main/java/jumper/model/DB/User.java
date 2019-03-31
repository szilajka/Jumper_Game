package jumper.model.DB;

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

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "jumper_user")
public class User {
    @Id
    @Column(name = "name", nullable = false)
    private String userName;

    @Column(name = "hashed_password")
    private byte[] hashedPassword;

    @Column(name = "salt")
    private byte[] salt;

    @OneToMany
    @JoinColumn(name = "id")
    private Collection<Score> score;

    @OneToOne
    @JoinColumn(name = "name")
    private AllTime allTime;

    public User() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte[] getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(byte[] hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public Collection<Score> getScore() {
        return score;
    }

    public void setScore(Collection<Score> score) {
        this.score = score;
    }

    public AllTime getAllTime() {
        return allTime;
    }

    public void setAllTime(AllTime allTime) {
        this.allTime = allTime;
    }
}
