package jumper.controllers;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
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
                labelErrorUserName.setText("UserName must not be empty!");
                return;
            }
            final String password = passwordField.getText().trim();
            if (password.trim().length() == 0) {
                labelErrorPassword.setText("Password field must not be empty!");
                return;
            }
            byte[] salt = Authenticate.generateSalt();
            byte[] hashedPassword = Authenticate.hashPassword(password, salt);
            EntityManager em = Main.getEntityManager();
            em.getTransaction().begin();
            User storeUser = new User();
            storeUser.setUserName(userName);
            storeUser.setHashedPassword(hashedPassword);
            storeUser.setSalt(salt);
            em.persist(storeUser);
            em.getTransaction().commit();
            em.detach(storeUser);
            em.close();
            labelErrorUserName.setText("");
            labelErrorPassword.setText("");
        } catch (Exception ex) {
            logger.error("Some error occured while tried to register.", ex);
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
