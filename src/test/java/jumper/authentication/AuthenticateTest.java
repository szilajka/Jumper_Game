package jumper.authentication;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.codec.DecoderException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AuthenticateTest {
    private static String password;
    private static EntityManagerFactory emf;

    @BeforeAll
    static void setup() {
        password = "alma";
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
        assertNull(Authenticate.Login(null, null, em), "null objects should not return.");
        assertNull(Authenticate.Login("almafa", "almafa", em), "user should not be in db.");
        em.close();
    }

    @AfterAll
    static void tearDown(){
        emf.close();
    }

}
