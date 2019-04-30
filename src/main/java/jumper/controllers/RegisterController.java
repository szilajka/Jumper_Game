package jumper.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jumper.authentication.Authenticate;
import jumper.model.DB.AllTime;
import jumper.model.DB.Score;
import jumper.model.DB.User;
import jumper.model.Player;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.io.IOException;

import org.tinylog.Logger;

/**
 * {@code Controller} of the {@code Register} view.
 */
public class RegisterController {
    /**
     * The {@link TextField} where the {@code user} writes its username.
     * This username is stored in the database and with this username can the {@code user} login.
     */
    public TextField inputUserName;
    /**
     * The {@link PasswordField} where the {@code user} writes its password.
     * This password will be hashed and the hashed password is stored.
     */
    public PasswordField passwordField;
    /**
     * A {@link Label} for the username if the username is reserved (not unique).
     */
    public Label labelErrorUserName;
    /**
     * A {@link Label} for the password if the password field is empty.
     */
    public Label labelErrorPassword;

    /**
     * Constructor of the class.
     */
    public RegisterController() {
    }

    /**
     * Handles the key press on button Register.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     */
    public void btnRegisterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            Logger.debug("Register button pressed.");
            Register();
        }
    }

    /**
     * Handles the button click on button Register.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     */
    public void btnRegisterClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            Logger.debug("Register button clicked.");
            Register();
        }
    }

    /**
     * Handles the key press on button Back.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     */
    public void btnBackPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            Logger.debug("Back button pressed.");
            GoToWelcomeMenu();
        }
    }

    /**
     * Handles the button click on button Back.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     */
    public void btnBackClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            Logger.debug("Back button clicked.");
            GoToWelcomeMenu();
        }
    }

    /**
     * Handles the Register process.
     * <p>
     * If the {@code username} is reserved, then an error message will be written
     * to the {@code label}.
     * The password and username must not be empty.
     */
    private void Register() {
        try {
            Logger.debug("Authenticate() method called.");
            String userName = inputUserName.getText().trim();
            if (userName.trim().length() == 0) {
                Logger.debug("Username field must not be empty!");
                labelErrorUserName.setText("Username field must not be empty!");
                return;
            } else {
                labelErrorUserName.setText("");
            }
            final String password = passwordField.getText().trim();
            if (password.trim().length() == 0) {
                Logger.debug("Password field must not be empty!");
                labelErrorPassword.setText("Password field must not be empty!");
                return;
            } else {
                labelErrorPassword.setText("");
            }
            if (userName.equals(password)) {
                labelErrorUserName.setText("Password must not be the username.");
                labelErrorPassword.setText("Password must not be the username.");
                return;
            }
            byte[] salt = Authenticate.generateSalt();
            byte[] hashedPassword = Authenticate.hashPassword(password, salt);
            saveUser(userName, salt, hashedPassword);

            labelErrorUserName.setText("");
            labelErrorPassword.setText("");
            labelErrorUserName.setText("Register succeeded");
            Logger.debug("Authenticate() method finished.");
        } catch (PersistenceException ex) {
            Logger.error("Some error during register.", ex);
            Logger.debug("Tried to insert existing key, username: {}",
                inputUserName.getText());
            labelErrorUserName.setText("Try to use another username, this username is reserved.");
        } catch (Exception ex) {
            Logger.error("Some error occured while tried to register.", ex);
            labelErrorUserName.setText("Some error occured while tried to register");
        }
    }

    /**
     * Saves the current {@code user}'s details to the database.
     *
     * @param userName       the username that will be stored in database
     * @param salt           the salt that used to generate the hashed password
     * @param hashedPassword the hashed password that will be stored in database
     */
    private void saveUser(String userName, byte[] salt, byte[] hashedPassword) {
        EntityManager em = MainJFX.getEntityManager();
        try {
            Logger.debug("saveUser() method called.");


            User storeUser = new User();
            storeUser.setUserName(userName);
            storeUser.setHashedPassword(hashedPassword);
            storeUser.setSalt(salt);

            Score storeScore = new Score();
            storeScore.setScore(0);
            storeScore.setLevel(1);
            storeScore.setVelocityY(Player.finalStartVelocityY);
            storeScore.setUserName(storeUser.getUserName());

            AllTime storeAllTime = new AllTime();
            storeAllTime.setElapsedTime(0);
            storeAllTime.setUserName(storeUser);

            em.getTransaction().begin();

            em.persist(storeUser);
            em.persist(storeScore);
            em.persist(storeAllTime);

            em.getTransaction().commit();

            em.detach(storeUser);
            em.detach(storeScore);
            em.detach(storeAllTime);

            Logger.debug("saveUser() method finished.");
        } catch (Exception ex) {
            Logger.error("Some error occurred in saveUser()", ex);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Implements the loading of the Welcome menu.
     */
    private void GoToWelcomeMenu() {
        try {
            Logger.debug("GoToWelcomeMenu() method called.");
            var stage = MainJFX.getPrimaryStage();
            AnchorPane ap = (AnchorPane) FXMLLoader.load(getClass().getClassLoader()
                .getResource("Welcome.fxml"));
            var welcomeScene = stage.getScene();
            welcomeScene.setRoot(ap);
            Logger.debug("GoToWelcomeMenu() method finished.");
        } catch (IOException io) {
            Logger.error("Welcome.fxml not found, closing the application!", io);
            AppExit();
        }
    }

    /**
     * Closing the application.
     * <p>
     * If any serious error occurred, than this method is called and a log message
     * will be written to the current log file.
     */
    private void AppExit() {
        Logger.debug("AppExit() method called.");
        Stage stage = MainJFX.getPrimaryStage();
        stage.close();
    }

}
