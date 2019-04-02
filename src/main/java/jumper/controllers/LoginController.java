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
import jumper.model.DB.User;
import org.apache.commons.codec.DecoderException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.NoResultException;
import java.io.IOException;

public class LoginController {
    private static final Logger logger = LogManager.getLogger("LoginController");
    public PasswordField passwordField;
    public TextField inputUserName;
    public Label labelErrorUserPwd;

    public void btnLoginPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            Login();
        }
    }

    public void btnLoginClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            Login();
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

    private void Login() {
        try {
            logger.debug("Login() method called.");
            final String userName = inputUserName.getText().trim();
            final String password = passwordField.getText().trim();
            if (userName.trim().length() == 0 || password.trim().length() == 0) {
                labelErrorUserPwd.setText("Username or password is incorrect!");
                return;
            }
            User findUser = Authenticate.Login(userName, password);
            if (findUser != null) {
                GoToMainMenu();
            } else {
                labelErrorUserPwd.setText("Username or password is incorrect!");
                return;
            }
        } catch (DecoderException e) {
            logger.error("Illegal character is in the salt.", e);
            labelErrorUserPwd.setText("Username or password is incorrect!");
        }
        catch (NoResultException ex){
            logger.debug("No user found with username: {}", inputUserName.getText());
            labelErrorUserPwd.setText("No user with this username.");
        }
    }

    private void GoToMainMenu() {
        try {
            logger.debug("GoToMainMenu() method called.");
            var mainMenuController = new MainMenuController();
            FXMLLoader fl = new FXMLLoader(getClass().getClassLoader()
                    .getResource("MainMenu.fxml"));
            fl.setController(mainMenuController);
            AnchorPane ap = (AnchorPane) fl.load();
            var stage = Main.getPrimaryStage();
            var mainMenuScene = stage.getScene();
            mainMenuScene.setRoot(ap);
        } catch (IOException io) {
            logger.error("MainMenu.fxml not found! Closing the application!");
            AppExit();
        }
    }

    private void GoToWelcomeMenu() {
        try {
            logger.debug("GoToWelcomeMenu() method called.");
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
