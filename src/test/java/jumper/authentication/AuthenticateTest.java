package jumper.authentication;

import jumper.model.DB.User;
import org.apache.commons.codec.DecoderException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticateTest {
    private static String username;
    private static String password;
    private static EntityManagerFactory emf;

    @BeforeAll
    static void setup() {
        username = "testuser";
        password = "testuser";
        emf = Persistence.createEntityManagerFactory("jumper");
    }

    @Test
    void hashPassword() {
        byte[] salt = Authenticate.generateSalt();
        byte[] hashedPassword = Authenticate.hashPassword(password, salt);
        assertNotEquals(password.getBytes(), hashedPassword,
                "Not hashed the password");
    }

    @Test
    void Login() throws DecoderException {
        EntityManager em = emf.createEntityManager();
        assertNull(Authenticate.getLoggedInUser(), "no user should be logged in.");
        assertThrows(DecoderException.class, () -> {
            Authenticate.Login("nouser", "nouser", em);
        }, "nouser password should not be encrypted.");
        User foundUser = Authenticate.Login(username, password, em);
        assertNotNull(foundUser, "testuser should not be null");
        assertEquals(username, foundUser.getUserName(), "testuser username should be testuser");
        assertNotEquals(password, foundUser.getHashedPassword(), "stored password is not hashed");
        assertNotNull(foundUser.getHashedPassword(), "testuser doesn't have hashed password");
        assertNotNull(foundUser.getSalt(), "testuser doesn't have salt.");

        assertNull(Authenticate.Login(null, null, em), "null objects should not return.");
        assertNull(Authenticate.Login("almafa", "almafa", em), "user should not be in db.");
        assertNull(Authenticate.Login(username, "almafa", em), "testuser password should not be almafa.");
        assertNotNull(Authenticate.getLoggedInUser(), "testuser should be logged in.");

    }

}
