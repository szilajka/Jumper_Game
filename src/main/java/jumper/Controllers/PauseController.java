package jumper.Controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

/**
 * This class is the {@code controller} of the Pause menu.
 * This {@link Scene} is only available while the user is playing.
 */
public class PauseController extends AbstractController
{
    public Button btnContinue;
    public Button btnExit;
    public Button btnMenu;

    private static final Logger logger = LogManager.getLogger("PauseController");
    private GameFirstLevelController gameFirstLC;
    private Map<String, ChangeListener<Number>> changeListenerMap;
    private boolean paused = true;
    private Map<EventType<KeyEvent>, EventHandler<KeyEvent>> keyEventHandlerMap;

    //region Constructor

    /**
     * The constructor of this class.
     * We give the game level controller as parameter, so later we can continue the game from where we paused.
     *
     * @param gameFirstLevelController The {@link GameFirstLevelController} that paused the game.
     */
    public PauseController(GameFirstLevelController gameFirstLevelController)
    {
        logger.debug("PauseController constructor started.");
        this.gameFirstLC = gameFirstLevelController;
        this.changeListenerMap = new HashMap<>();
        this.keyEventHandlerMap = new HashMap<>();
        logger.debug("PauseController constructor finished.");
    }

    //endregion Constructor

    //region Resize Methods

    @Override
    protected void addResizeListener()
    {
        resize();
    }

    private void removeResizeListener()
    {
        var stage = Main.getPrimaryStage();

        if (changeListenerMap.get("width") != null)
        {
            stage.widthProperty().addListener(changeListenerMap.get("width"));
        }
        if (changeListenerMap.get("height") != null)
        {
            stage.heightProperty().addListener(changeListenerMap.get("height"));
        }
    }

    private void resize()
    {
        var widthResize = new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue)
            {
                resizeXAndWidth(oldValue, newValue);
            }
        };

        var heightResize = new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue)
            {
                resizeYAndHeight(oldValue, newValue);
            }
        };

        removeResizeListener();

        changeListenerMap.put("width", widthResize);
        changeListenerMap.put("height", heightResize);

        var stage = Main.getPrimaryStage();

        stage.widthProperty().addListener(changeListenerMap.get("width"));
        stage.heightProperty().addListener(changeListenerMap.get("height"));

    }

    @Override
    protected void resizeOnLoad(Number oldValueX, Number oldValueY, Number newValueX, Number newValueY)
    {
        resizeXAndWidth(oldValueX, newValueX);
        resizeYAndHeight(oldValueY, newValueY);
    }

    private void resizeXAndWidth(Number oldValue, Number newValue)
    {
        double ratio = newValue.doubleValue() / oldValue.doubleValue();

        btnContinue.setLayoutX(btnContinue.getLayoutX() * ratio);
        btnMenu.setLayoutX(btnMenu.getLayoutX() * ratio);
        btnExit.setLayoutX(btnExit.getLayoutX() * ratio);

        btnContinue.setPrefWidth(btnContinue.getPrefWidth() * ratio);
        btnMenu.setPrefWidth(btnMenu.getPrefWidth() * ratio);
        btnExit.setPrefWidth(btnExit.getPrefWidth() * ratio);

    }

    private void resizeYAndHeight(Number oldValue, Number newValue)
    {
        double ratio = newValue.doubleValue() / oldValue.doubleValue();

        btnContinue.setLayoutY(btnContinue.getLayoutY() * ratio);
        btnMenu.setLayoutY(btnMenu.getLayoutY() * ratio);
        btnExit.setLayoutY(btnExit.getLayoutY() * ratio);

        btnContinue.setPrefHeight(btnContinue.getPrefHeight() * ratio);
        btnMenu.setPrefHeight(btnMenu.getPrefHeight() * ratio);
        btnExit.setPrefHeight(btnExit.getPrefHeight() * ratio);

    }

    //endregion Resize Methods

    //region Key Listener

    private void removeKeyListener()
    {
        var stage = Main.getPrimaryStage();
        if (keyEventHandlerMap.get(KeyEvent.KEY_PRESSED) != null)
        {
            stage.removeEventHandler(KeyEvent.KEY_PRESSED, keyEventHandlerMap.get(KeyEvent.KEY_PRESSED));
        }
    }

    /**
     * This method adds an {@link EventHandler} to the {@link Scene}.
     * If you press {@code ESC}, then it continues the game.
     */
    protected void keyListenerPause()
    {
        var pauseEH = new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent keyEvent)
            {
                var kc = keyEvent.getCode();
                if (kc == KeyCode.ESCAPE && !paused)
                {
                    var stage = Main.getPrimaryStage();
                    continueFirstLevel();
                    stage.removeEventHandler(KeyEvent.KEY_PRESSED, this);

                }
                else if (kc == KeyCode.ESCAPE)
                {
                    gameFirstLC.removeKeyListener();
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
    public void OnBtnContinuePressed(KeyEvent keyEvent) throws IOException
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
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
    public void OnBtnContinueClicked(MouseEvent mouseEvent) throws IOException
    {
        if (mouseEvent.getButton() == MouseButton.PRIMARY)
        {
            logger.debug("BtnContinue clicked.");
            continueFirstLevel();
        }
    }

    /**
     * This method implements the exit button's behaviour when it is pressed.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     */
    public void OnBtnExitPressed(KeyEvent keyEvent)
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            logger.debug("BtnExit pressed.");
            AppExit();
        }
    }

    /**
     * This method implements the exit button's behaviour when it is clicked.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     */
    public void OnBtnExitClicked(MouseEvent mouseEvent)
    {
        if (mouseEvent.getButton() == MouseButton.PRIMARY)
        {
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
    public void OnBtnMenuPressed(KeyEvent keyEvent) throws IOException
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
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
    public void OnBtnMenuClicked(MouseEvent mouseEvent) throws IOException
    {
        if (mouseEvent.getButton() == MouseButton.PRIMARY)
        {
            logger.debug("BtnMenu clicked.");
            loadMainMenu();
        }
    }

    //endregion Button Actions

    //region Implementation of Button Actions

    /**
     * Implements the exit button's behaviour.
     */
    private void AppExit()
    {
        logger.debug("AppExit() method called.");
        removeResizeListener();
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    /**
     * Implements the menu button's behaviour.
     *
     * @throws IOException If the fxml file is not found.
     */
    private void loadMainMenu()
    {
        try
        {
            logger.debug("loadMainMenu() method called.");
            var stage = Main.getPrimaryStage();
            var fl = new FXMLLoader(getClass().getClassLoader().getResource("MainMenu.fxml"));
            var mainController = new MainMenuController();
            fl.setController(mainController);
            var ap = (AnchorPane) fl.load();
            var mainScene = btnMenu.getScene();
            mainScene.setRoot(ap);
            setNewAndStageXY(ap, stage);
            removeKeyListener();
            removeResizeListener();
            mainController.resizeOnLoad(oldStageX, oldStageY, changeNewX, changeNewY);
            mainController.addResizeListener();
        }
        catch (IOException io)
        {
            logger.error("MainMenu.fxml has not founded, closing the application.", io);
            AppExit();
        }
        catch (Exception ex)
        {
            logger.error("Some error occured during loading the main menu, closing the application.", ex);
            AppExit();
        }
    }

    /**
     * Implements the continue button's behaviour.
     *
     * @throws IOException If the fxml file is not found.
     */
    private void continueFirstLevel()
    {
        try
        {
            logger.debug("continueFirstLevel() method called.");
            var stage = Main.getPrimaryStage();
            var fl = new FXMLLoader(getClass().getClassLoader().getResource("GameFirstLevel.fxml"));
            fl.setController(gameFirstLC);
            var ap = (AnchorPane) fl.load();
            var gameScene = btnContinue.getScene();
            gameScene.setRoot(ap);
            setNewAndStageXY(ap, stage);
            removeKeyListener();
            removeResizeListener();
            gameFirstLC.letsContinue(ap);
        }
        catch (IOException io)
        {
            logger.error("MainMenu.fxml has not founded, closing the application.", io);
            AppExit();
        }
        catch (Exception ex)
        {
            logger.error("Some error occured during loading the main menu, closing the application.", ex);
            AppExit();
        }
    }

    //endregion Implementation of Button Actions

}
