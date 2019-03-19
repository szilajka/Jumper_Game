package jumper.Controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
 * This class is the {@code controller} of the Main Menu.
 */
public class MainMenuController extends AbstractController
{
    public Button btnStart;
    public Button btnExit;
    public Button btnOptions;

    private Map<String, ChangeListener<Number>> changeListenerMap;
    private static final Logger logger = LogManager.getLogger("MainMenuController");

    //region Constructors

    /**
     * Constructor of the class.<br>
     */
    public MainMenuController()
    {
        logger.debug("MainMenuController constructor called.");
        changeListenerMap = new HashMap<>();
    }

    /*public MainMenuController(double oldValueX, double oldValueY, double newValueX, double newValueY)
    {
        this.oldValX = oldValueX;
        this.oldValY = oldValueY;
        this.changeNewX = newValueX;
        this.changeNewY = newValueY;
        changeListenerMap = new HashMap<>();
    }*/

    //endregion Constructors

    //region Button Actions

    /**
     * Handles the button click on button Start.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     * @throws IOException If the FXML file is not existing.
     */
    public void onBtnStartClicked(MouseEvent mouseEvent) throws IOException
    {
        if (mouseEvent.getButton() == MouseButton.PRIMARY)
        {
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
    public void onBtnStartPressed(KeyEvent keyEvent) throws IOException
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            logger.debug("BtnStart pressed.");
            FirstLevelStart();
        }
    }

    /**
     * Handles the button click on button Exit.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     */
    public void onBtnExitClicked(MouseEvent mouseEvent)
    {
        if (mouseEvent.getButton() == MouseButton.PRIMARY)
        {
            logger.debug("BtnExit clicked.");
            MenuExit();
        }
    }

    /**
     * Handles the key press on button Exit.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     */
    public void onBtnExitPressed(KeyEvent keyEvent)
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            logger.debug("BtnExit pressed.");
            MenuExit();
        }
    }

    /**
     * Handles the button click on button Options.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     * @throws IOException If the FXML file is not existing.
     */
    public void onBtnOptionsClicked(MouseEvent mouseEvent) throws IOException
    {
        if (mouseEvent.getButton() == MouseButton.PRIMARY)
        {
            logger.debug("BtnOptions clicked.");
            OptionsMenu();
        }
    }

    /**
     * Handles the key press on button Options.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     * @throws IOException If the FXML file is not existing.
     */
    public void onBtnOptionsPressed(KeyEvent keyEvent) throws IOException
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            logger.debug("BtnOptions pressed.");
            OptionsMenu();
        }
    }

    //endregion Button Actions

    //region Implementations of Button Actions

    /**
     * Implements the loading of the first level.
     *
     * @throws IOException If the FXML file is not existing.
     */
    private void FirstLevelStart()
    {
        try
        {
            logger.debug("FirstLevelStart method called.");
            removeResizeListener();
            Stage stage = Main.getPrimaryStage();

            var fl = new FXMLLoader(getClass().getClassLoader().getResource("GameFirstLevel.fxml"));
            var gameFirstLC = new GameFirstLevelController();
            fl.setController(gameFirstLC);
            var root = (AnchorPane) fl.load();

            Scene gameScene = btnStart.getScene();
            gameScene.setRoot(root);
            setNewAndStageXY(root, stage);
            gameFirstLC.init(root);
            gameFirstLC.addResizeListener();
            logger.debug("FirstLevelStart method has finished.");
        } catch (IOException io)
        {
            logger.error("GameFirstLevel.fxml has not found, closing application.", io);
            MenuExit();
        } catch (Exception ex)
        {
            logger.error("Some error occured, closing application.", ex);
            MenuExit();
        }
    }

    /**
     * Implements exiting the application.
     */
    private void MenuExit()
    {
        logger.debug("MenuExit() method called.");
        removeResizeListener();
        Stage stage = Main.getPrimaryStage();
        stage.close();
    }

    /**
     * Implements the loading of the Options menu.
     *
     * @throws IOException If the FXML file is not existing.
     */
    private void OptionsMenu() throws IOException
    {
        try
        {
            logger.debug("OptionsMenu() method called.");
            removeResizeListener();
            var stage = Main.getPrimaryStage();
            Scene optionsScene;
            var fl = new FXMLLoader(getClass().getClassLoader().getResource("Options.fxml"));
            var optionsController = new OptionsController<MainMenuController>(MainMenuController.this, oldValX, oldValY, changeNewX, changeNewY);
            fl.setController(optionsController);
            var ap = (AnchorPane) fl.load();
            optionsScene = btnOptions.getScene();
            optionsScene.setRoot(ap);
            setNewAndStageXY(ap, stage);
            optionsController.setChkFullScreen(Main.getPrimaryStage().isFullScreen());
            optionsController.resizeOnLoad(oldStageX, oldStageY, changeNewX, changeNewY);
            optionsController.addResizeListener();
        }
        catch (IOException io)
        {
            logger.error("Options.fxml has not found, closing application.", io);
            MenuExit();
        }
        catch (Exception ex)
        {
            logger.error("Some error occured, closing application.", ex);
            MenuExit();
        }
    }

    //endregion Implementations of Button Actions

    //region Resize Methods

    /**
     * Removes the current {@link Scene}'s resize {@link ChangeListener}s from the {@link Stage}.
     */
    private void removeResizeListener()
    {
        var stage = Main.getPrimaryStage();
        if (changeListenerMap.get("width") != null)
        {
            stage.widthProperty().removeListener(changeListenerMap.get("width"));
        }
        if (changeListenerMap.get("height") != null)
        {
            stage.heightProperty().removeListener(changeListenerMap.get("height"));
        }
    }

    /**
     * Adds the current {@link Scene}'s resize {@link ChangeListener}s from the {@link Stage}.
     */
    public void addResizeListener()
    {
        resize();
    }

    /**
     * Implementation of {@link AbstractController#resizeOnLoad(Number, Number, Number, Number)} method.<br>
     *
     * @param oldValueX {@link Stage}'s old X coordinate before change (if there was a change)
     * @param oldValueY {@link Stage}'s old Y coordinate before change (if there was a change)
     * @param newValueX {@link Stage}'s new X coordinate after change (if there was a change)
     * @param newValueY {@link Stage}'s new Y coordinate after change (if there was a change)
     */
    @Override
    protected void resizeOnLoad(Number oldValueX, Number oldValueY, Number newValueX, Number newValueY)
    {
        resizeXAndWidth(oldValueX, newValueX);
        resizeYAndHeight(oldValueY, newValueY);
    }

    /**
     * Implements resizing to this {@link Scene}.
     */
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


        var stage = Main.getPrimaryStage();

        changeListenerMap.put("width", widthResize);
        changeListenerMap.put("height", heightResize);

        removeResizeListener();

        stage.widthProperty().addListener(changeListenerMap.get("width"));
        stage.heightProperty().addListener(changeListenerMap.get("height"));

    }

    /**
     * Compute the X coordinates and Widths of the elements in this {@link Scene}.<br>
     * Computes a ratio by the given values.<br>
     * If it just a simple resize, then the parameters are the width of the {@code scene} before and after resizing.<br>
     * If the application changes between {@code scenes} then they are the original width of the scene and the resized width.<br>
     *
     * @param oldValue Width of the {@code scene} before resizing or the original width of the {@code scene}
     * @param newValue Width of the {@code scene} after resizing
     */
    private void resizeXAndWidth(Number oldValue, Number newValue)
    {
        btnStart.setLayoutX(btnStart.getLayoutX() * newValue.doubleValue() / oldValue.doubleValue());
        btnExit.setLayoutX(btnExit.getLayoutX() * newValue.doubleValue() / oldValue.doubleValue());
        btnOptions.setLayoutX(btnOptions.getLayoutX() * newValue.doubleValue() / oldValue.doubleValue());

        btnStart.setPrefWidth(btnStart.getPrefWidth() * newValue.doubleValue() / oldValue.doubleValue());
        btnExit.setPrefWidth(btnExit.getPrefWidth() * newValue.doubleValue() / oldValue.doubleValue());
        btnOptions.setPrefWidth(btnOptions.getPrefWidth() * newValue.doubleValue() / oldValue.doubleValue());
    }

    /**
     * Compute the Y coordinates and Heights of the elements in this {@link Scene}.<br>
     * It compute a ratio by the given values.<br>
     * If it just a simple resize, then the parameters are the height of the {@code scene} before and after resizing.<br>
     * If the application changes between {@code scenes} then they are the original height of the scene and the resized height.<br>
     *
     * @param oldValue Height of the {@code scene} before resizing or the original height of the {@code scene}
     * @param newValue Height of the {@code scene} after resizing
     */
    private void resizeYAndHeight(Number oldValue, Number newValue)
    {
        btnStart.setLayoutY(btnStart.getLayoutY() * newValue.doubleValue() / oldValue.doubleValue());
        btnExit.setLayoutY(btnExit.getLayoutY() * newValue.doubleValue() / oldValue.doubleValue());
        btnOptions.setLayoutY(btnOptions.getLayoutY() * newValue.doubleValue() / oldValue.doubleValue());

        btnStart.setPrefHeight(btnStart.getPrefHeight() * newValue.doubleValue() / oldValue.doubleValue());
        btnExit.setPrefHeight(btnExit.getPrefHeight() * newValue.doubleValue() / oldValue.doubleValue());
        btnOptions.setPrefHeight(btnOptions.getPrefHeight() * newValue.doubleValue() / oldValue.doubleValue());
    }

    //endregion Resize Methods

}