package jumper.authentication;

import jumper.model.DB.User;
import jumper.queries.Queries;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.EntityManager;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.pmw.tinylog.Logger;

/**
 * Authenticates the {@code user}.
 * <p>
 * Helps in the register and login methods.
 */
public class Authenticate {
    /**
     * Iteration number used in generating the hashed password.
     */
    private final static int iterations = 65536;
    /**
     * The key length that is used to generate the hashed password.
     */
    private final static int keyLength = 128;
    /**
     * The authenticated {@code user}.
     */
    private static User loggedInUser;

    /**
     * Generates the salt to the hashed password.
     *
     * @return the generated salt
     */
    public static byte[] generateSalt() {
        Logger.debug("generateSalt() method called.");
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Generates the hashed password from the given password.
     *
     * @param password the value to be hashed
     * @param salt     the salt that is used with the hashing algorithm
     * @return the hashed password
     */
    public static byte[] hashPassword(final String password, final byte[] salt) {
        try {
            Logger.debug("hashPassword() method called.");
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            SecretKey key = skf.generateSecret(spec);
            return key.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            Logger.error("NoSuchAlgorithmException happened, throwing,", e);
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            Logger.error("InvalidKeySpecException happened, throwing,", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the {@code user} from database that wants to login.
     *
     * @param userName the {@code user}'s username that is stored in the database
     * @param password the {@code user}'s password
     * @param em       the {@link EntityManager} that connects to database
     * @return If the {@code user} is not found in the database, null, else the found {@code user}.
     * @throws DecoderException if the hashed password contains illegal characters
     */
    public static User Login(final String userName, final String password, EntityManager em)
        throws DecoderException {
        try {
            Logger.debug("Login() method called.");
            User foundUser = Queries.getUserByUserName(em, userName);
            if (foundUser == null) {
                Logger.debug("Returning null value, the {} user not exists.", userName);
                return null;
            }

            byte[] hashedPwd = hashPassword(password, Hex.decodeHex(foundUser.getSalt().toCharArray()));
            String stringHashedPwd = String.valueOf(Hex.encodeHex(hashedPwd));
            if (foundUser.getHashedPassword().equals(stringHashedPwd)) {
                Logger.debug("{} user has been found.", userName);
                loggedInUser = foundUser;
                return foundUser;
            } else {
                Logger.debug("Returning null value, " +
                    "the {} user's password not matching the real password", userName);
                return null;
            }
        } catch (NullPointerException np) {
            Logger.debug("username or password is null.");
            return null;
        }
    }

    /**
     * Gets the logged in {@code user}.
     * <p>
     * This method is used to get the actually logged in {@code user}, usually this
     * is a parameter to the queries.
     *
     * @return the logged in {@code user}
     */
    public static User getLoggedInUser() {
        return loggedInUser;
    }
}
