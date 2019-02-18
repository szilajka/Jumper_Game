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

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//This is needed to remove warning about Map warnings, that is about that I use ChangeListener there without type,
//so type safety is not possible
@SuppressWarnings("unchecked")
public class MainMenuController extends AbstractController
{
    public Button btnStart;
    public Button btnExit;
    public Button btnOptions;

    private Map<String, ChangeListener> changeListenerMap;


    public MainMenuController()
    {
        changeListenerMap = new HashMap<>();
    }

    public MainMenuController(double oldValueX, double oldValueY, double newValueX, double newValueY)
    {
        this.oldValX = oldValueX;
        this.oldValY = oldValueY;
        this.changeNewX = newValueX;
        this.changeNewY = newValueY;
        System.out.println("itt is jártam");
        changeListenerMap = new HashMap<>();
    }

    public void onBtnStartClicked(MouseEvent mouseEvent) throws IOException
    {
        if (mouseEvent.getButton() == MouseButton.PRIMARY)
        {
            FirstLevelStart();
        }
    }

    private void FirstLevelStart() throws IOException
    {
        removeResizeListener();
        var root = (AnchorPane) FXMLLoader.load(getClass().getClassLoader().getResource("GameFirstLevel.fxml"));
        Stage stage = (Stage) btnStart.getScene().getWindow();
        Scene gameScene = new Scene(root);
        stage.setScene(gameScene);
        var gflC = new GameFirstLevelController();
        gflC.init(root);
        stage.show();
        gflC.run();
    }

    public void onBtnExitClicked(MouseEvent mouseEvent)
    {
        if (mouseEvent.getButton() == MouseButton.PRIMARY)
        {
            MenuExit();
        }
    }

    private void MenuExit()
    {
        removeResizeListener();
        Stage stage = Main.getPrimaryStage();
        stage.close();
    }

    public void onBtnStartPressed(KeyEvent keyEvent) throws IOException
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            FirstLevelStart();
        }
    }

    public void onBtnExitPressed(KeyEvent keyEvent)
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            MenuExit();
        }
    }

    public void onBtnOptionsPressed(KeyEvent keyEvent) throws IOException
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            OptionsMenu();
        }
    }

    public void onBtnOptionsClicked(MouseEvent mouseEvent) throws IOException
    {
        OptionsMenu();
    }

    private void OptionsMenu() throws IOException
    {
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
        optionsController.setComboBox();
        optionsController.resizeOnLoad(oldStageX, oldStageY, changeNewX, changeNewY);
        optionsController.addResizeListener();
    }

    private void removeResizeListener()
    {
        var stage = Main.getPrimaryStage();
        stage.widthProperty().removeListener(changeListenerMap.get("width"));
        stage.heightProperty().removeListener(changeListenerMap.get("height"));
        stage.fullScreenProperty().removeListener(changeListenerMap.get("fullscreen"));
    }

    public void addResizeListener()
    {
        resize();
    }

    @Override
    protected void resizeOnLoad(Number oldValueX, Number oldValueY, Number newValueX, Number newValueY)
    {
        resizeXandWidth(oldValueX, newValueX);
        resizeYandHeight(oldValueY, newValueY);
    }

    private void resize()
    {
        var widthResize = new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue)
            {
                resizeXandWidth(oldValue, newValue);
            }
        };

        var heightResize = new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue)
            {
                resizeYandHeight(oldValue, newValue);
            }
        };

        var fullScreenResize = new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1)
            {
                //WARNING: This java.awt methods, like this, give some kind of warning
                //link: https://bugs.openjdk.java.net/browse/JDK-8156779?focusedCommentId=14213204&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-14213204
                //they fix it in openjfx12 if they can
                var dp = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
                var fsWidth = dp.getWidth();
                var fsHeight = dp.getHeight();
                var stage = Main.getPrimaryStage();
                resizeXandWidth(stage.getWidth(), fsWidth);
                resizeYandHeight(stage.getHeight(), fsHeight);
            }
        };

        var stage = Main.getPrimaryStage();

        changeListenerMap.put("width", widthResize);
        changeListenerMap.put("height", heightResize);
        changeListenerMap.put("fullscreen", fullScreenResize);

        removeResizeListener();

        stage.widthProperty().addListener(changeListenerMap.get("width"));
        stage.heightProperty().addListener(changeListenerMap.get("height"));
        stage.fullScreenProperty().addListener(changeListenerMap.get("fullscreen"));

    }

    private void resizeXandWidth(Number oldValue, Number newValue)
    {
        btnStart.setLayoutX(btnStart.getLayoutX() * newValue.doubleValue() / oldValue.doubleValue());
        btnExit.setLayoutX(btnExit.getLayoutX() * newValue.doubleValue() / oldValue.doubleValue());
        btnOptions.setLayoutX(btnOptions.getLayoutX() * newValue.doubleValue() / oldValue.doubleValue());

        btnStart.setPrefWidth(btnStart.getPrefWidth() * newValue.doubleValue() / oldValue.doubleValue());
        btnExit.setPrefWidth(btnExit.getPrefWidth() * newValue.doubleValue() / oldValue.doubleValue());
        btnOptions.setPrefWidth(btnOptions.getPrefWidth() * newValue.doubleValue() / oldValue.doubleValue());
    }

    private void resizeYandHeight(Number oldValue, Number newValue)
    {
        btnStart.setLayoutY(btnStart.getLayoutY() * newValue.doubleValue() / oldValue.doubleValue());
        btnExit.setLayoutY(btnExit.getLayoutY() * newValue.doubleValue() / oldValue.doubleValue());
        btnOptions.setLayoutY(btnOptions.getLayoutY() * newValue.doubleValue() / oldValue.doubleValue());

        btnStart.setPrefHeight(btnStart.getPrefHeight() * newValue.doubleValue() / oldValue.doubleValue());
        btnExit.setPrefHeight(btnExit.getPrefHeight() * newValue.doubleValue() / oldValue.doubleValue());
        btnOptions.setPrefHeight(btnOptions.getPrefHeight() * newValue.doubleValue() / oldValue.doubleValue());
    }

}