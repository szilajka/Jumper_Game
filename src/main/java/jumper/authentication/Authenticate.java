package jumper.authentication;

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

import jumper.controllers.Main;
import jumper.model.DB.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.EntityManager;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class Authenticate {
    private static final Logger logger = LogManager.getLogger("Authenticate");
    private final static int iterations = 65536;
    private final static int keyLength = 128;
    private static User loggedInUser;

    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public static byte[] hashPassword(final String password, final byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            byte[] res = skf.generateSecret(spec).getEncoded();

            return res;
            //store res in db.
            //store salt in db.
        } catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithException happened, throwing,", e);
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            logger.error("InvalidKeySpecException happened, throwing,", e);
            throw new RuntimeException(e);
        }
    }

    public static User Login(final String userName, final String password) {
        EntityManager em = Main.getEntityManager();
        User searchUser = new User();
        searchUser.setUserName(userName);
        User foundUser = em.find(User.class, searchUser);
        if (foundUser == null) {
            return null;
        }

        if(foundUser.getHashedPassword() == hashPassword(password, foundUser.getSalt())){
            loggedInUser = foundUser;
            return foundUser;
        }
        else{
            return null;
        }
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }
}
