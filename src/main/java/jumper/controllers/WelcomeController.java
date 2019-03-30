package jumper.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class WelcomeController {
    private static final Logger logger = LogManager.getLogger("WelcomeController");

    public void btnLoginPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            GoToLoginMenu();
        }
    }

    public void btnLoginClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            GoToLoginMenu();
        }
    }

    public void btnRegisterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            GoToRegisterMenu();
        }
    }

    public void btnRegisterClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            GoToRegisterMenu();
        }
    }

    public void btnExitPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            AppExit();
        }
    }

    public void btnExitClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            AppExit();
        }
    }

    private void GoToLoginMenu() {
        try {
            logger.debug("GoToLoginMenu() method called.");
            var stage = Main.getPrimaryStage();
            AnchorPane ap = (AnchorPane) FXMLLoader.load(getClass().getClassLoader()
                    .getResource("Login.fxml"));
            var loginScreen = stage.getScene();
            loginScreen.setRoot(ap);
        } catch (IOException io) {
            logger.error("Login.fxml not found!", io);
            AppExit();
        }
    }

    private void GoToRegisterMenu() {
        try {
            logger.debug("GoToRegisterMenu() method called.");
            var stage = Main.getPrimaryStage();
            AnchorPane ap = (AnchorPane) FXMLLoader.load(getClass().getClassLoader()
                    .getResource("Register.fxml"));
            var registerScene = stage.getScene();
            registerScene.setRoot(ap);
        } catch (IOException io) {
            logger.error("Register.fxml not found!", io);
            AppExit();
        }
    }

    private void AppExit() {
        logger.debug("AppExit() method called.");
        Stage stage = Main.getPrimaryStage();
        stage.close();
    }
}
