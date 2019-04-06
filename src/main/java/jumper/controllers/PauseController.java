package jumper.controllers;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jumper.authentication.Authenticate;
import jumper.helpers.TimeHelper;
import jumper.model.DB.AllTime;
import jumper.queries.Queries;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.tinylog.Logger;

/**
 * {@code Controller} of the {@code Pause} view.
 */
public class PauseController {
    /**
     * The Continue button.
     */
    public Button btnContinue;
    /**
     * The Exit button.
     */
    public Button btnExit;
    /**
     * The Menu button.
     */
    public Button btnMenu;
    /**
     * Label for text: {@code 'Time in game:'}
     */
    public Label lblTime;
    /**
     * Label for the time that the user spent in this game.
     */
    public Label lblTimeQuery;
    /**
     * Instance of the caller {@link GameLevelController}.
     */
    private GameLevelController gameFirstLC;
    /**
     * A {@code boolean} used to really load this view.
     */
    private boolean paused = true;
    /**
     * This {@link Map} stores the {@link EventHandler}s that responds to key presses.
     */
    private Map<EventType<KeyEvent>, EventHandler<KeyEvent>> keyEventHandlerMap;

    /**
     * The constructor of this class.
     * We give the game level controller as parameter, so later we can continue the game
     * from where we paused.
     *
     * @param gameLevelController The {@link GameLevelController} that paused the game.
     */
    public PauseController(GameLevelController gameLevelController) {
        Logger.debug("PauseController constructor started.");
        this.gameFirstLC = gameLevelController;
        this.keyEventHandlerMap = new HashMap<>();
        Logger.debug("PauseController constructor finished.");
    }

    /**
     * Removes the key listener.
     * <p>
     * Removes the key listener in this {@code scene} from the {@code stage}.
     */
    private void removeKeyListener() {
        Logger.debug("removeKeyListener() method called.");
        var stage = MainJFX.getPrimaryStage();
        if (keyEventHandlerMap.get(KeyEvent.KEY_PRESSED) != null) {
            stage.removeEventHandler(KeyEvent.KEY_PRESSED,
                keyEventHandlerMap.get(KeyEvent.KEY_PRESSED));
        }
        Logger.debug("removeKeyListener() method finished.");
    }

    /**
     * Binds a key listener to the actual {@code scene} of the {@code stage}.
     * <p>
     * This method adds an {@link EventHandler} to the {@link Scene}.
     * If you press {@code ESC}, then it continues the game.
     */
    protected void keyListenerPause() {
        Logger.debug("keyListenerPause() method called.");
        var pauseEH = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                var kc = keyEvent.getCode();
                if (kc == KeyCode.ESCAPE && !paused) {
                    Logger.info("Continuing the game.");
                    var stage = MainJFX.getPrimaryStage();
                    continueFirstLevel();
                    stage.removeEventHandler(KeyEvent.KEY_PRESSED, this);

                } else if (kc == KeyCode.ESCAPE) {
                    gameFirstLC.getEngine().removeKeyListener();
                    paused = false;
                }
                // Reminder for me if you ever wonder about it:
                // The above lines are a tricky one.
                // If you press the ESCAPE button, then the gameFirstLevelController calls this
                // PauseController, and then it sets this method and at this time this thinks, that
                // the ESCAPE button is pressed, and then continues the game.
            }
        };

        removeKeyListener();
        keyEventHandlerMap.put(KeyEvent.KEY_PRESSED, pauseEH);
        var stage = MainJFX.getPrimaryStage();
        stage.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandlerMap.get(KeyEvent.KEY_PRESSED));
        Logger.debug("keyListenerPause() method finished.");
    }

    /**
     * Sets the {@link Label}'s text for the in game time.
     */
    public void setInGameTime() {
        Logger.debug("setInGameTime() method called.");
        EntityManager em = MainJFX.getEntityManager();
        AllTime allTime = Queries.getAllTimeByUserName(em, Authenticate.getLoggedInUser());
        int elapsedSecs = allTime == null ? 0 : allTime.getElapsedTime();
        String formattedTime = TimeHelper.convertSecondsToDuration(elapsedSecs);
        lblTimeQuery.setText(formattedTime);
        Logger.debug("setInGameTime() method finished.");
    }

    /**
     * This method implements the continue button's behaviour when it is pressed.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     * @throws IOException If the fxml file is not found.
     */
    public void OnBtnContinuePressed(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            Logger.debug("BtnContinue pressed.");
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
            Logger.debug("BtnContinue clicked.");
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
            Logger.debug("BtnExit pressed.");
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
            Logger.debug("BtnExit clicked.");
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
            Logger.debug("BtnMenu pressed.");
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
            Logger.debug("BtnMenu clicked.");
            loadMainMenu();
        }
    }

    /**
     * Implements the exit button's behaviour.
     * <p>
     * Closes the application.
     */
    private void AppExit() {
        Logger.debug("AppExit() method called.");
        Stage stage = MainJFX.getPrimaryStage();
        stage.close();
    }

    /**
     * Implements the menu button's behaviour.
     * <p>
     * Loads the main menu, if something goes wrong, closes the application.
     */
    private void loadMainMenu() {
        try {
            Logger.debug("loadMainMenu() method called.");
            var stage = MainJFX.getPrimaryStage();
            var fl = new FXMLLoader(getClass().getClassLoader().getResource("MainMenu.fxml"));
            var mainController = new MainMenuController();
            fl.setController(mainController);
            var ap = (AnchorPane) fl.load();
            var mainScene = stage.getScene();
            mainScene.setRoot(ap);
            removeKeyListener();
            mainController.setInGameTime();
            Logger.debug("loadMainMenu() method finished.");
        } catch (IOException io) {
            Logger.error("MainMenu.fxml has not founded, closing the application.", io);
            AppExit();
        } catch (Exception ex) {
            Logger.error("Some error occurred during loading the main menu, " +
                "closing the application.", ex);
            AppExit();
        }
    }

    /**
     * Implements the continue button's behaviour.
     * <p>
     * Loads the game level, if something goes wrong, tries to load the main menu.
     * If something very bad happens, closes the application.
     */
    private void continueFirstLevel() {
        try {
            Logger.debug("continueFirstLevel() method called.");
            var stage = MainJFX.getPrimaryStage();
            var fl = new FXMLLoader(getClass().getClassLoader().getResource("GameLevel.fxml"));
            fl.setController(gameFirstLC);
            var ap = (AnchorPane) fl.load();
            var gameScene = stage.getScene();
            gameScene.setRoot(ap);
            removeKeyListener();
            gameFirstLC.getEngine().letsContinue(ap);
            Logger.debug("continueFirstLevel() method finished.");
        } catch (IOException io) {
            Logger.error("GameLevel.fxml has not founded, closing the application.", io);
            loadMainMenu();
        } catch (Exception ex) {
            Logger.error("Some error occurred during loading the main menu, closing the application.", ex);
            AppExit();
        }
    }

}
