package jumper.authentication;

import jumper.Queries.Queries;
import jumper.controllers.Main;
import jumper.model.DB.User;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.EntityManager;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

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
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return res;
        } catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithException happened, throwing,", e);
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            logger.error("InvalidKeySpecException happened, throwing,", e);
            throw new RuntimeException(e);
        }
    }

    public static User Login(final String userName, final String password) throws DecoderException {
        EntityManager em = Main.getEntityManager();
        User foundUser = Queries.getUserByUserName(em, userName);
        if (foundUser == null) {
            return null;
        }

        byte[] hashedPwd = hashPassword(password, Hex.decodeHex(foundUser.getSalt().toCharArray()));
        String stringHashedPwd = String.valueOf(Hex.encodeHex(hashedPwd));
        if(foundUser.getHashedPassword().equals(stringHashedPwd)){
            /*if(foundUser.getScore() == null){
                foundUser.setScore(new ArrayList());
            }*/
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
