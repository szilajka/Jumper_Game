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
import jumper.helpers.GameEngineHelper;
import jumper.model.DB.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.io.IOException;

import org.apache.commons.codec.DecoderException;
import org.pmw.tinylog.Logger;

/**
 * {@code Controller} of the {@code Login} view.
 */
public class LoginController {
    /**
     * The {@link TextField} where the {@code user} writes its username.
     */
    public TextField inputUserName;
    /**
     * The {@link PasswordField} where the {@code user} writes his password.
     */
    public PasswordField passwordField;
    /**
     * A {@link Label} used to write errors if the username/password is
     * incorrect or the username does not exist in the database.
     */
    public Label labelErrorUserPwd;

    /**
     * Handles the key press on button Login.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     */
    public void btnLoginPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            Logger.debug("Login button pressed.");
            Login();
        }
    }

    /**
     * Handles the button click on button Login.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     */
    public void btnLoginClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            Logger.debug("Login button clicked.");
            Login();
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
     * Handles the Login process.
     * <p>
     * If the {@code user}'s name and password are correct, the {@code user} logs in to the game,
     * else gets some error message on the {@code label}.
     */
    private void Login() {
        try {
            Logger.debug("Login() method called.");
            EntityManager em = MainJFX.getEntityManager();
            final String userName = inputUserName.getText().trim();
            final String password = passwordField.getText().trim();
            if (userName.trim().length() == 0 || password.trim().length() == 0) {
                Logger.debug("Username or password is incorrect!");
                labelErrorUserPwd.setText("Username or password is incorrect!");
                return;
            }
            User findUser = Authenticate.Login(userName, password, em);
            em.close();
            if (findUser != null) {
                Logger.debug("Login() method finished.");
                GameEngineHelper.levelCounter =
                    findUser.getScore().get(findUser.getScore().size() - 1).getLevel();
                Logger.info("Game level {} set to {} user.",
                    GameEngineHelper.levelCounter, findUser.getUserName());
                GoToMainMenu();
            } else {
                Logger.debug("Username or password is incorrect!");
                labelErrorUserPwd.setText("Username or password is incorrect!");
                return;
            }
        } catch (DecoderException e) {
            Logger.error("Illegal character is in the salt.", e);
            labelErrorUserPwd.setText("Username or password is incorrect!");
        } catch (NoResultException ex) {
            Logger.debug("No user found with username: {}", inputUserName.getText());
            labelErrorUserPwd.setText("No user with this username.");
        }
    }

    /**
     * Implements the loading of the MainJFX Menu.
     */
    private void GoToMainMenu() {
        try {
            Logger.debug("GoToMainMenu() method called.");
            var mainMenuController = new MainMenuController();
            FXMLLoader fl = new FXMLLoader(getClass().getClassLoader()
                .getResource("MainMenu.fxml"));
            fl.setController(mainMenuController);
            AnchorPane ap = (AnchorPane) fl.load();
            var stage = MainJFX.getPrimaryStage();
            var mainMenuScene = stage.getScene();
            mainMenuScene.setRoot(ap);
            mainMenuController.setInGameTime();
            Logger.debug("GoToMainMenu() method finished.");
        } catch (IOException io) {
            Logger.error("MainMenu.fxml not found! Closing the application!");
            AppExit();
        }
    }

    /**
     * Implements the loading of Welcome menu.
     * <p>
     * This method is called when the {@code user} successfully logs in.
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
            Logger.error("Welcome.fxml not found!", io);
            AppExit();
        }
    }

    /**
     * Closes the {@code application}.
     * If some error occurs, this method is called.
     */
    private void AppExit() {
        Logger.debug("AppExit() method called.");
        Stage stage = MainJFX.getPrimaryStage();
        stage.close();
    }

}
