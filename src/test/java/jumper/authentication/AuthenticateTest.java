package jumper.authentication;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticateTest {
    
    private static final String password = "myuser";

    @Test
    void hashPassword() {
        byte[] salt = Authenticate.generateSalt();
        byte[] hashedPassword = Authenticate.hashPassword(password, salt);
        assertNotEquals(password.getBytes(), hashedPassword,
                "Not hashed the password");
    }

}
