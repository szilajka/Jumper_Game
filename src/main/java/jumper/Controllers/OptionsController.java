package jumper.Controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * The Option {@link Scene}'s {@code controller} class.
 * It gets a {@code generic parameter} that {@code extends} the {@link AbstractController}.
 * We want to be sure, that this {@code scene} is available from all {@code scenes} and
 * we can go back to the previous {@code scene} without creating a new scene and lose any information about it.
 * <p>
 * These controller classes have a {@link Map} that contains {@link ChangeListener}s, in this {@code map} the {@code Value}
 * parameter is not type safety and while compiling, get some warning about the classes use unchecked operations.
 * To avoid this, we ignored these warnings with an annotation.
 *
 * @param <T> The {@code controller} that called this {@code scene}.
 */
//This is needed to remove warning about Map warnings, that is about that I use ChangeListener there without type,
//so type safety is not possible
@SuppressWarnings("unchecked")
public class OptionsController<T extends AbstractController> extends AbstractController
{
    public CheckBox chkFullScreen;
    public Label lblFullScreen;
    public Label lblResolution;
    public ComboBox<String> comboBoxResolution;
    public Button btnBack;

    private T prevSceneController;
    private boolean fullscreen;

    private Map<String, ChangeListener> changeListenerMap;

    /**
     * {@code Constructor} of the class.
     * This initializes the {@link Map}, that contains the {@link ChangeListener}s, as a {@link HashMap}.
     *
     * @param prevController The {@code controller} that called this {@link Scene}.
     * @param oldValueX      Initialize a variable
     * @param oldValueY      Initialize a variable
     * @param newValueX      Initialize a variable
     * @param newValueY      Initialize a variable
     */
    public OptionsController(T prevController, double oldValueX, double oldValueY, double newValueX, double newValueY)
    {
        this.prevSceneController = prevController;
        this.fullscreen = Main.getPrimaryStage().isFullScreen();
        this.oldValX = oldValueX;
        this.oldValY = oldValueY;
        this.changeNewX = newValueX;
        this.changeNewY = newValueY;
        changeListenerMap = new HashMap<>();
    }

    /**
     * THis method implements changing between full screen and windowed mode.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     */
    public void onChkFullScreenClicked(MouseEvent mouseEvent)
    {
        if (chkFullScreen.isSelected())
        {
            var stage = Main.getPrimaryStage();
            stage.setFullScreen(true);
            fullscreen = true;
            lblResolution.setDisable(false);
            comboBoxResolution.setDisable(false);
            comboBoxResolution.setVisible(true);
            changeNewX = stage.getWidth();
            changeNewY = stage.getHeight();
        } else
        {
            var stage = Main.getPrimaryStage();
            stage.setFullScreen(false);
            fullscreen = false;
            lblResolution.setDisable(true);
            comboBoxResolution.setDisable(true);
            changeNewX = stage.getWidth();
            changeNewY = stage.getHeight();
        }
    }

    /**
     * Implementation of {@see jumper.Controllers.AbstractController#addResizeListener()}.
     */
    protected void addResizeListener()
    {
        resize();
    }

    /**
     * Removes the {@link ChangeListener}s from the main {@link javafx.stage.Stage}.
     * This is used, when the application changes to another {@link Scene}.
     * Without this method, the {@code change listeners} would not be removed from the main {@code stage}
     * and the application would do some unnecessary operation.
     */
    private void removeResizeListener()
    {
        var stage = Main.getPrimaryStage();
        stage.widthProperty().removeListener(changeListenerMap.get("width"));
        stage.heightProperty().removeListener(changeListenerMap.get("height"));
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

        changeListenerMap.clear();

        changeListenerMap.put("width", widthResize);
        changeListenerMap.put("height", heightResize);

        removeResizeListener();

        stage.widthProperty().addListener(changeListenerMap.get("width"));
        stage.heightProperty().addListener(changeListenerMap.get("height"));

    }

    /**
     * Compute the X coordinates and Widths of the elements in this {@link Scene}.
     * It compute a ratio by the given values.
     * If it just a simple resize, then the parameters are the width of the {@code scene} before and after resizing.
     * If the application changes between {@code scenes} then they are the original width of the scene and the resized width.
     *
     * @param oldValue Width of the {@code scene} before resizing or the original width of the {@code scene}
     * @param newValue Width of the {@code scene} after resizing
     */
    private void resizeXAndWidth(Number oldValue, Number newValue)
    {
        double ratio = newValue.doubleValue() / oldValue.doubleValue();

        lblFullScreen.setLayoutX(lblFullScreen.getLayoutX() * ratio);
        chkFullScreen.setLayoutX(chkFullScreen.getLayoutX() * ratio);
        lblResolution.setLayoutX(lblResolution.getLayoutX() * ratio);
        comboBoxResolution.setLayoutX(comboBoxResolution.getLayoutX() * ratio);
        btnBack.setLayoutX(btnBack.getLayoutX() * ratio);

        lblFullScreen.setPrefWidth(lblFullScreen.prefWidth(-1) * ratio);
        chkFullScreen.setPrefWidth(chkFullScreen.getPrefWidth() * ratio);
        lblResolution.setPrefWidth(lblResolution.prefWidth(-1) * ratio);
        comboBoxResolution.setPrefWidth(comboBoxResolution.getPrefWidth() * ratio);
        btnBack.setPrefWidth(btnBack.getPrefWidth() * ratio);
    }

    /**
     * Compute the Y coordinates and Heights of the elements in this {@link Scene}.
     * It compute a ratio by the given values.
     * If it just a simple resize, then the parameters are the height of the {@code scene} before and after resizing.
     * If the application changes between {@code scenes} then they are the original height of the scene and the resized height.
     *
     * @param oldValue Height of the {@code scene} before resizing or the original height of the {@code scene}
     * @param newValue Height of the {@code scene} after resizing
     */
    private void resizeYAndHeight(Number oldValue, Number newValue)
    {
        double ratio = newValue.doubleValue() / oldValue.doubleValue();

        lblFullScreen.setLayoutY(lblFullScreen.getLayoutY() * ratio);
        chkFullScreen.setLayoutY(chkFullScreen.getLayoutY() * ratio);
        lblResolution.setLayoutY(lblResolution.getLayoutY() * ratio);
        comboBoxResolution.setLayoutY(comboBoxResolution.getLayoutY() * ratio);
        btnBack.setLayoutY(btnBack.getLayoutY() * ratio);

        lblFullScreen.setPrefHeight(lblFullScreen.prefHeight(-1) * ratio);
        chkFullScreen.setPrefHeight(chkFullScreen.getPrefHeight() * ratio);
        lblResolution.setPrefHeight(lblResolution.prefHeight(-1) * ratio);
        comboBoxResolution.setPrefHeight(comboBoxResolution.getPrefHeight() * ratio);
        btnBack.setPrefHeight(btnBack.getPrefHeight() * ratio);
    }

    /**
     * This method implements the resolution changing.
     * This {@link ComboBox} only available if the application is in full screen mode.
     *
     * @param event The {@link ActionEvent} that triggers this method.
     */
    public void onCBResolutionAction(ActionEvent event)
    {
        var stage = Main.getPrimaryStage();
        var res = comboBoxResolution.getSelectionModel().getSelectedItem();
        var resWxH = res.split("x");
        var resWidth = Double.parseDouble(resWxH[0]);
        var resHeight = Double.parseDouble(resWxH[1]);
        if (stage.getWidth() != resWidth || stage.getHeight() != resHeight)
        {
            stage.setWidth(resWidth);
            stage.setHeight(resHeight);
        }
    }

    /**
     * This method implements the back button's behaviour when it is pressed.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     * @throws IOException If the fxml file is not found.
     */
    public void onBtnBackPressed(KeyEvent keyEvent) throws IOException
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            BackToPreviousScene();
        }
    }

    /**
     * This method implements the back button's behaviour when it is clicked.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     * @throws IOException If the fxml file is not found.
     */
    public void onBtnBackClicked(MouseEvent mouseEvent) throws IOException
    {
        BackToPreviousScene();
    }

    /**
     * Implements the back button's behaviour.
     * If it is pressed/clicked it should navigate you back to the previous {@link Scene}.
     *
     * @throws IOException If the fxml file is not found.
     */
    private void BackToPreviousScene() throws IOException
    {
        removeResizeListener();
        var className = prevSceneController.getClass().getName();
        var fullFxmlName = className.substring(0, className.length() - "Controller".length());
        var fxmlName = fullFxmlName.substring(fullFxmlName.lastIndexOf('.') + 1);
        var stage = Main.getPrimaryStage();
        Scene scene;
        var fl = new FXMLLoader(getClass().getClassLoader().getResource(fxmlName + ".fxml"));
        fl.setController(prevSceneController);
        var root = (AnchorPane) fl.load();
        stage.setFullScreen(fullscreen);
        scene = btnBack.getScene();
        scene.setRoot(root);
        setNewAndStageXY(root, stage);
        prevSceneController.resizeOnLoad(oldStageX, oldStageY, changeNewX, changeNewY);
        prevSceneController.addResizeListener();
    }

    /**
     * Implementation of {@see AbstractController#resizeOnLoad(Number, Number, Number, Number)} method.
     * The {@code applyCss()} method is necessary, because if we work with {@link Label}s then their width and
     * height are computed usually so we need to get it by this method.
     * @param oldValueX {@link }'s old X coordinate before change (if there was a change)
     * @param oldValueY {@link javafx.stage.Stage}'s old Y coordinate before change (if there was a change)
     * @param newValueX {@link javafx.stage.Stage}'s new X coordinate after change (if there was a change)
     * @param newValueY {@link javafx.stage.Stage}'s new Y coordinate after change (if there was a change)
     */
    @Override
    protected void resizeOnLoad(Number oldValueX, Number oldValueY, Number newValueX, Number newValueY)
    {
        lblFullScreen.applyCss();
        lblResolution.applyCss();
        resizeXAndWidth(oldValueX, newValueX);
        resizeYAndHeight(oldValueY, newValueY);
    }

    /**
     * When loading this {@link Scene}, this sets the {@link CheckBox}'s {@code selected} value.
     *
     * @param fs The value of the application's full screen property.
     */
    protected void setChkFullScreen(boolean fs)
    {
        chkFullScreen.setSelected(fs);
    }

    /**
     * Initialize the {@link ComboBox} which contains the resolutions.
     */
    protected void setComboBox()
    {
        if (this.comboBoxResolution.getItems().isEmpty())
        {
            this.comboBoxResolution.getItems().addAll("640x400", "800x600", "1024x768", "1280x720", "1920x1080");
        }
        if (Main.getPrimaryStage().isFullScreen())
        {
            comboBoxResolution.setDisable(false);
        } else
        {
            comboBoxResolution.setDisable(true);
        }
    }

}
