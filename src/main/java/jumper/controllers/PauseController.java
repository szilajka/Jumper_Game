package jumper.controllers;

import javafx.event.EventHandler;
import javafx.event.EventType;
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

public class PauseController {
    public Button btnContinue;
    public Button btnExit;
    public Button btnMenu;

    private static final Logger logger = LogManager.getLogger("PauseController");
    private GameFirstLevelController gameFirstLC;
    private boolean paused = true;
    private Map<EventType<KeyEvent>, EventHandler<KeyEvent>> keyEventHandlerMap;

    //region Constructor

    /**
     * The constructor of this class.
     * We give the game level controller as parameter, so later we can continue the game from where we paused.
     *
     * @param gameFirstLevelController The {@link GameFirstLevelController} that paused the game.
     */
    public PauseController(GameFirstLevelController gameFirstLevelController) {
        logger.debug("PauseController constructor started.");
        this.gameFirstLC = gameFirstLevelController;
        this.keyEventHandlerMap = new HashMap<>();
        logger.debug("PauseController constructor finished.");
    }

    //endregion Constructor

    //region Key Listener

    /**
     * Removes the key listener.
     *
     * Removes the key listener in this {@code scene} from the {@code stage}.
     */
    private void removeKeyListener() {
        var stage = Main.getPrimaryStage();
        if (keyEventHandlerMap.get(KeyEvent.KEY_PRESSED) != null) {
            stage.removeEventHandler(KeyEvent.KEY_PRESSED,
                    keyEventHandlerMap.get(KeyEvent.KEY_PRESSED));
        }
    }

    /**
     * Binds a key listener to the actual {@code scene} of the {@code stage}.
     *
     * This method adds an {@link EventHandler} to the {@link Scene}.
     * If you press {@code ESC}, then it continues the game.
     */
    protected void keyListenerPause() {
        var pauseEH = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                var kc = keyEvent.getCode();
                if (kc == KeyCode.ESCAPE && !paused) {
                    var stage = Main.getPrimaryStage();
                    continueFirstLevel();
                    stage.removeEventHandler(KeyEvent.KEY_PRESSED, this);

                } else if (kc == KeyCode.ESCAPE) {
                    gameFirstLC.getEngine().removeKeyListener();
                    paused = false;
                }
            }
        };

        removeKeyListener();

        keyEventHandlerMap.put(KeyEvent.KEY_PRESSED, pauseEH);

        var stage = Main.getPrimaryStage();
        stage.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandlerMap.get(KeyEvent.KEY_PRESSED));
    }

    //endregion Key Listener

    //region Button Actions

    /**
     * This method implements the continue button's behaviour when it is pressed.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     * @throws IOException If the fxml file is not found.
     */
    public void OnBtnContinuePressed(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            logger.debug("BtnContinue pressed.");
            continueFirstLevel();
        }
    }

    /**
     * This method implements the continue button's behaviour when it is clicked.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     * @throws IOException If the fxml file is not found.
     */
    public void OnBtnContinueClicked(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            logger.debug("BtnContinue clicked.");
            continueFirstLevel();
        }
    }

    /**
     * This method implements the exit button's behaviour when it is pressed.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     */
    public void OnBtnExitPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            logger.debug("BtnExit pressed.");
            AppExit();
        }
    }

    /**
     * This method implements the exit button's behaviour when it is clicked.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     */
    public void OnBtnExitClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            logger.debug("BtnExit clicked.");
            AppExit();
        }
    }

    /**
     * This method implements the menu button's behaviour when it is pressed.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     * @throws IOException If the fxml file is not found.
     */
    public void OnBtnMenuPressed(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            logger.debug("BtnMenu pressed.");
            loadMainMenu();
        }
    }

    /**
     * This method implements the menu button's behaviour when it is clicked.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     * @throws IOException If the fxml file is not found.
     */
    public void OnBtnMenuClicked(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            logger.debug("BtnMenu clicked.");
            loadMainMenu();
        }
    }

    //endregion Button Actions

    //region Implementation of Button Actions

    /**
     * Implements the exit button's behaviour.
     *
     * Closes the application.
     */
    private void AppExit() {
        logger.debug("AppExit() method called.");
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    /**
     * Implements the menu button's behaviour.
     *
     * Loads the main menu, if something goes wrong, closes the application.
     *
     * @throws IOException If the fxml file is not found.
     */
    private void loadMainMenu() {
        try {
            logger.debug("loadMainMenu() method called.");
            var stage = Main.getPrimaryStage();
            var fl = new FXMLLoader(getClass().getClassLoader().getResource("MainMenu.fxml"));
            var mainController = new MainMenuController();
            fl.setController(mainController);
            var ap = (AnchorPane) fl.load();
            var mainScene = btnMenu.getScene();
            mainScene.setRoot(ap);
            removeKeyListener();
        } catch (IOException io) {
            logger.error("MainMenu.fxml has not founded, closing the application.", io);
            AppExit();
        } catch (Exception ex) {
            logger.error("Some error occured during loading the main menu, closing the application.", ex);
            AppExit();
        }
    }

    /**
     * Implements the continue button's behaviour.
     *
     * Loads the game level, if something goes wrong, tries to load the main menu.
     * If something very bad happens, closes the application.
     *
     * @throws IOException If the fxml file is not found.
     */
    private void continueFirstLevel() {
        try {
            logger.debug("continueFirstLevel() method called.");
            var stage = Main.getPrimaryStage();
            var fl = new FXMLLoader(getClass().getClassLoader().getResource("GameFirstLevel.fxml"));
            fl.setController(gameFirstLC);
            var ap = (AnchorPane) fl.load();
            var gameScene = btnContinue.getScene();
            gameScene.setRoot(ap);
            removeKeyListener();
            gameFirstLC.getEngine().letsContinue(ap);
        } catch (IOException io) {
            logger.error("GameFirstLevel.fxml has not founded, closing the application.", io);
            loadMainMenu();
        } catch (Exception ex) {
            logger.error("Some error occured during loading the main menu, closing the application.", ex);
            AppExit();
        }
    }

    //endregion Implementation of Button Actions

}
