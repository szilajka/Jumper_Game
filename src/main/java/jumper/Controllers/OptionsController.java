package jumper.Controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The Option {@link Scene}'s {@code controller} class.<br>
 * It gets a {@code generic parameter} that {@code extends} the {@link AbstractController}.<br>
 * We want to be sure, that this {@code scene} is available from all {@code scenes} and
 * we can go back to the previous {@code scene} without creating a new scene and lose any information about it.<br>
 *
 * @param <T> The {@code controller} that called this {@code scene}.
 */
public class OptionsController<T extends AbstractController> extends AbstractController
{
    public CheckBox chkFullScreen;
    public Label lblFullScreen;
    public Button btnBack;

    private T prevSceneController;
    private boolean fullScreenChangingX = false, fullScreenChangingY = false;
    private static double oldStageXBeforeFS, oldStageYBeforeFS, oldSceneXBeforeFS, oldSceneYBeforeFS;

    private Map<String, ChangeListener<Number>> changeListenerMap;
    private ChangeListener<Boolean> fullScreenChangeListener;

    //region Constructor

    /**
     * {@code Constructor} of the class.<br>
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
        this.oldValX = oldValueX;
        this.oldValY = oldValueY;
        this.changeNewX = newValueX;
        this.changeNewY = newValueY;
        changeListenerMap = new HashMap<>();
    }

    //endregion Constructor

    //region Full screen

    /**
     * This method implements changing between full screen and windowed mode.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     */
    public void onChkFullScreenClicked(MouseEvent mouseEvent)
    {
        if (chkFullScreen.isSelected())
        {
            var stage = Main.getPrimaryStage();
            stage.setFullScreen(true);
        } else
        {
            var stage = Main.getPrimaryStage();
            stage.setFullScreen(false);
        }
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

    //endregion Full screen

    //region Resize Methods

    /**
     * Contains logic for full screen resize for X coordinates.<br>
     * This causes some warning when compiling, due to some javafx issues.<br>
     * They should fix it in javafx12/openjfx12.<br>
     * Link to this issue:
     *
     * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8156779?focusedCommentId=14213204&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-14213204"
     * target="_blank">Issue</a>
     */
    private void setFullScreenResizeX()
    {
        //WARNING: This java.awt methods, like this, give some kind of warning
        //link: https://bugs.openjdk.java.net/browse/JDK-8156779?focusedCommentId=14213204&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-14213204
        //they fix it in openjfx12 if they can
        var dp = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        var fsWidth = dp.getWidth();
        var stage = Main.getPrimaryStage();
        //if we call this method from width property changelistener, then the stage's width will be the full screen's width
        if (stage.getWidth() == fsWidth)    //if changing to full screen
        {
            resizeXAndWidth(oldStageXBeforeFS, fsWidth);
        } else
        {
            resizeXAndWidth(fsWidth, oldSceneXBeforeFS);
        }
    }

    /**
     * Contains logic for full screen resize for Y coordinates.<br>
     * This causes some warning when compiling, due to some javafx issues.<br>
     * They should fix it in javafx12/openjfx12.<br>
     * Link to this issue:
     *
     * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8156779?focusedCommentId=14213204&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-14213204"
     * target="_blank">Issue</a>
     */
    private void setFullScreenResizeY()
    {
        //WARNING: This java.awt methods, like this, give some kind of warning
        //link: https://bugs.openjdk.java.net/browse/JDK-8156779?focusedCommentId=14213204&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-14213204
        //they fix it in openjfx12 if they can
        var dp = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        var fsHeight = dp.getHeight();
        var stage = Main.getPrimaryStage();
        if (stage.getHeight() == fsHeight)
        {
            resizeYAndHeight(oldStageYBeforeFS, fsHeight);
        } else
        {
            resizeYAndHeight(fsHeight, oldSceneYBeforeFS);
        }
    }

    /**
     * Implementation of {@link AbstractController#addResizeListener()}.
     */
    protected void addResizeListener()
    {
        resize();
    }

    /**
     * Removes the {@link ChangeListener}s from the main {@link javafx.stage.Stage}.<br>
     * This is used, when the application changes to another {@link Scene}.<br>
     * Without this method, the {@code change listeners} would not be removed from the main {@code stage}
     * and the application would do some unnecessary operation.
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
        if (fullScreenChangeListener != null)
        {
            stage.fullScreenProperty().removeListener(fullScreenChangeListener);
        }
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
                if (!fullScreenChangingX)
                {
                    resizeXAndWidth(oldValue, newValue);
                } else
                {
                    setFullScreenResizeX();
                    fullScreenChangingX = false;
                }
            }
        };

        var heightResize = new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue)
            {
                if (!fullScreenChangingY)
                {
                    resizeYAndHeight(oldValue, newValue);
                } else
                {
                    setFullScreenResizeY();
                    fullScreenChangingY = false;
                }
            }
        };

        fullScreenChangeListener = (observableValue, aBoolean, t1) -> {
            var stage = Main.getPrimaryStage();
            oldStageXBeforeFS = stage.getWidth();
            oldStageYBeforeFS = stage.getHeight();
            if (stage.isFullScreen())
            {
                oldSceneXBeforeFS = stage.getScene().getWidth();
                oldSceneYBeforeFS = stage.getScene().getHeight();
            }
            fullScreenChangingX = true;
            fullScreenChangingY = true;
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        };

        var stage = Main.getPrimaryStage();

        changeListenerMap.clear();

        changeListenerMap.put("width", widthResize);
        changeListenerMap.put("height", heightResize);

        removeResizeListener();

        stage.widthProperty().addListener(changeListenerMap.get("width"));
        stage.heightProperty().addListener(changeListenerMap.get("height"));
        stage.fullScreenProperty().addListener(fullScreenChangeListener);

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
        double ratio = newValue.doubleValue() / oldValue.doubleValue();

        lblFullScreen.setLayoutX(lblFullScreen.getLayoutX() * ratio);
        chkFullScreen.setLayoutX(chkFullScreen.getLayoutX() * ratio);
        btnBack.setLayoutX(btnBack.getLayoutX() * ratio);

        lblFullScreen.setPrefWidth(lblFullScreen.prefWidth(-1) * ratio);
        chkFullScreen.setPrefWidth(chkFullScreen.getPrefWidth() * ratio);
        btnBack.setPrefWidth(btnBack.getPrefWidth() * ratio);
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
        double ratio = newValue.doubleValue() / oldValue.doubleValue();

        lblFullScreen.setLayoutY(lblFullScreen.getLayoutY() * ratio);
        chkFullScreen.setLayoutY(chkFullScreen.getLayoutY() * ratio);
        btnBack.setLayoutY(btnBack.getLayoutY() * ratio);

        lblFullScreen.setPrefHeight(lblFullScreen.prefHeight(-1) * ratio);
        chkFullScreen.setPrefHeight(chkFullScreen.getPrefHeight() * ratio);
        btnBack.setPrefHeight(btnBack.getPrefHeight() * ratio);
    }


    /**
     * Implementation of {@link AbstractController#resizeOnLoad(Number, Number, Number, Number)} method.<br>
     * The {@code applyCss()} method is necessary, because if we work with {@link Label}s then their width and
     * height are computed usually so we need to get it by this method.
     *
     * @param oldValueX {@link }'s old X coordinate before change (if there was a change)
     * @param oldValueY {@link javafx.stage.Stage}'s old Y coordinate before change (if there was a change)
     * @param newValueX {@link javafx.stage.Stage}'s new X coordinate after change (if there was a change)
     * @param newValueY {@link javafx.stage.Stage}'s new Y coordinate after change (if there was a change)
     */
    @Override
    protected void resizeOnLoad(Number oldValueX, Number oldValueY, Number newValueX, Number newValueY)
    {
        lblFullScreen.applyCss();
        resizeXAndWidth(oldValueX, newValueX);
        resizeYAndHeight(oldValueY, newValueY);
    }

    //endregion Resize Methods

    //region Button Actions

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
        if (mouseEvent.getButton() == MouseButton.PRIMARY)
        {
            BackToPreviousScene();
        }
    }

    //endregion Button Actions

    //region Back to Previous Scene

    /**
     * Implements the back button's behaviour.<br>
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
        stage.setFullScreen(Main.getPrimaryStage().isFullScreen());
        scene = btnBack.getScene();
        scene.setRoot(root);
        setNewAndStageXY(root, stage);
        prevSceneController.resizeOnLoad(oldStageX, oldStageY, changeNewX, changeNewY);
        prevSceneController.addResizeListener();
    }

    //endregion Back to Previous Scene


}
