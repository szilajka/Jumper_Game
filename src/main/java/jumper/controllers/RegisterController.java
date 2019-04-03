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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.io.IOException;

public class RegisterController {
    private static final Logger logger = LogManager.getLogger("RegisterController");
    public TextField inputUserName;
    public PasswordField passwordField;
    public Label labelErrorUserName;
    public Label labelErrorPassword;

    public void btnRegisterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            Register();
        }
    }

    public void btnRegisterClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            Register();
        }
    }

    public void btnBackPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            GoToWelcomeMenu();
        }
    }

    public void btnBackClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            GoToWelcomeMenu();
        }
    }

    private void Register() {
        try {
            logger.debug("Authenticate() method called.");
            String userName = inputUserName.getText().trim();
            if (userName.trim().length() == 0) {
                labelErrorUserName.setText("Username field must not be empty!");
                return;
            } else {
                labelErrorUserName.setText("");
            }
            final String password = passwordField.getText().trim();
            if (password.trim().length() == 0) {
                labelErrorPassword.setText("Password field must not be empty!");
                return;
            } else {
                labelErrorPassword.setText("");
            }
            byte[] salt = Authenticate.generateSalt();
            byte[] hashedPassword = Authenticate.hashPassword(password, salt);
            EntityManager em = Main.getEntityManager();

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

            em.getTransaction().commit(); //ez egy nagy szar!!!! - by zig

            em.detach(storeUser);
            em.detach(storeScore);
            em.detach(storeAllTime);

            em.close();

            labelErrorUserName.setText("");
            labelErrorPassword.setText("");
            labelErrorUserName.setText("Register succeeded");
        } catch (PersistenceException ex) {
            logger.error("Some error during register.", ex);
            logger.debug("Tried to insert existing key, username: {}", inputUserName.getText());
            labelErrorUserName.setText("Try to use another username, this username is reserved.");
        } catch (Exception ex) {
            logger.error("Some error occured while tried to register.", ex);
            labelErrorUserName.setText("Some error occured while tried to register");
        }
    }

    private void GoToWelcomeMenu() {
        try {
            var stage = Main.getPrimaryStage();
            AnchorPane ap = (AnchorPane) FXMLLoader.load(getClass().getClassLoader()
                    .getResource("Welcome.fxml"));
            var welcomeScene = stage.getScene();
            welcomeScene.setRoot(ap);
        } catch (IOException io) {
            logger.error("Welcome.fxml not found!", io);
            AppExit();
        }
    }

    private void AppExit() {
        logger.debug("AppExit() method called.");
        Stage stage = Main.getPrimaryStage();
        stage.close();
    }

}
