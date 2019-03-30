package jumper.controllers;

import javafx.application.Application;
import javafx.fxml.FXML;
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

public class RegisterController {
    private static final Logger logger = LogManager.getLogger("RegisterController");

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
        logger.debug("Register() method called.");
        //TODO: Add encrypt and store it in db.
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
