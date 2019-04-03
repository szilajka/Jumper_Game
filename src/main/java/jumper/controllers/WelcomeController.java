package jumper.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.tinylog.Logger;

import java.io.IOException;

/**
 * {@code Controller} of the {@code Welcome} view.
 */
public class WelcomeController {
    /**
     * An empty constructor of the class.
     */
    public WelcomeController() {
    }

    /**
     * Handles the key press on button Login.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     */
    public void btnLoginPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            GoToLoginMenu();
        }
    }

    /**
     * Handles the button click on button Login.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     */
    public void btnLoginClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            GoToLoginMenu();
        }
    }

    /**
     * Handles the key press on button Register.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     */
    public void btnRegisterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            GoToRegisterMenu();
        }
    }

    /**
     * Handles the button click on button Register.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     */
    public void btnRegisterClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            GoToRegisterMenu();
        }
    }

    /**
     * Handles the key press on button Exit.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     */
    public void btnExitPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            AppExit();
        }
    }
    /**
     * Handles the button click on button Exit.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     */
    public void btnExitClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            AppExit();
        }
    }

    /**
     * Implements the loading of the Login menu.
     */
    private void GoToLoginMenu() {
        try {
            Logger.debug("GoToLoginMenu() method called.");
            var stage = Main.getPrimaryStage();
            AnchorPane ap = (AnchorPane) FXMLLoader.load(getClass().getClassLoader()
                    .getResource("Login.fxml"));
            var loginScreen = stage.getScene();
            loginScreen.setRoot(ap);
            Logger.debug("GoToLoginMenu() method finished.");
        } catch (IOException io) {
            Logger.error("Login.fxml not found, closing the application!", io);
            AppExit();
        }
    }

    /**
     * Implements the loading of the Register menu.
     */
    private void GoToRegisterMenu() {
        try {
            Logger.debug("GoToRegisterMenu() method called.");
            var stage = Main.getPrimaryStage();
            AnchorPane ap = (AnchorPane) FXMLLoader.load(getClass().getClassLoader()
                    .getResource("Register.fxml"));
            var registerScene = stage.getScene();
            registerScene.setRoot(ap);
            Logger.debug("GoToRegisterMenu() method finished.");
        } catch (IOException io) {
            Logger.error("Authenticate.fxml not found!", io);
            AppExit();
        }
    }

    /**
     * Closing the application.
     */
    private void AppExit() {
        Logger.debug("AppExit() method called.");
        Stage stage = Main.getPrimaryStage();
        stage.close();
    }
}
