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

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainMenuController {
    public Button btnStart;
    public Button btnExit;
    public Button btnOptions;

    private Map<String, ChangeListener<Number>> changeListenerMap;
    private static final Logger logger = LogManager.getLogger("MainMenuController");

    //region Constructors

    /**
     * Constructor of the class.
     * Creates a new {@code MainMenuController} instance.
     */
    public MainMenuController() {
        logger.debug("MainMenuController constructor called.");
        changeListenerMap = new HashMap<>();
    }

    //endregion Constructors

    //region Button Actions

    /**
     * Handles the button click on button Start.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     * @throws IOException If the FXML file is not existing.
     */
    public void onBtnStartClicked(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            logger.debug("BtnStart clicked.");
            FirstLevelStart();
        }
    }

    /**
     * Handles the key press on button Start.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     * @throws IOException If the FXML file is not existing.
     */
    public void onBtnStartPressed(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            logger.debug("BtnStart pressed.");
            FirstLevelStart();
        }
    }

    /**
     * Handles the button click on button Exit.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     */
    public void onBtnExitClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            logger.debug("BtnExit clicked.");
            MenuExit();
        }
    }

    /**
     * Handles the key press on button Exit.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     */
    public void onBtnExitPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            logger.debug("BtnExit pressed.");
            MenuExit();
        }
    }

    //endregion Button Actions

    //region Implementations of Button Actions

    /**
     * Implements the loading of the first level.
     *
     */
    private void FirstLevelStart() {
        try {
            logger.debug("FirstLevelStart method called.");
            Stage stage = Main.getPrimaryStage();

            var fl = new FXMLLoader(getClass().getClassLoader()
                    .getResource("GameFirstLevel.fxml"));
            var gameFirstLC = new GameFirstLevelController();
            fl.setController(gameFirstLC);
            var root = (AnchorPane) fl.load();

            Scene gameScene = stage.getScene();
            gameScene.setRoot(root);
            gameFirstLC.initGameEngineLevel(root);
            gameFirstLC.runGameLevelEngine();
            logger.debug("FirstLevelStart method has finished.");
        } catch (IOException io) {
            logger.error("GameFirstLevel.fxml has not found, closing application.", io);
            MenuExit();
        } catch (Exception ex) {
            logger.error("Some error occured, closing application.", ex);
            MenuExit();
        }
    }

    /**
     * Implements exiting the application.
     */
    private void MenuExit() {
        logger.debug("MenuExit() method called.");
        Stage stage = Main.getPrimaryStage();
        stage.close();
    }

    //endregion Implementations of Button Actions

}
