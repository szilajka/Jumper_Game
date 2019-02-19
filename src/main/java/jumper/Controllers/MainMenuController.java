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

    //region Constructors

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
        changeListenerMap = new HashMap<>();
    }

    //endregion Constructors

    //region Button Actions

    public void onBtnStartClicked(MouseEvent mouseEvent) throws IOException
    {
        if (mouseEvent.getButton() == MouseButton.PRIMARY)
        {
            FirstLevelStart();
        }
    }

    public void onBtnStartPressed(KeyEvent keyEvent) throws IOException
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            FirstLevelStart();
        }
    }


    public void onBtnExitClicked(MouseEvent mouseEvent)
    {
        if (mouseEvent.getButton() == MouseButton.PRIMARY)
        {
            MenuExit();
        }
    }

    public void onBtnExitPressed(KeyEvent keyEvent)
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            MenuExit();
        }
    }


    public void onBtnOptionsClicked(MouseEvent mouseEvent) throws IOException
    {
        if (mouseEvent.getButton() == MouseButton.PRIMARY)
        {
            OptionsMenu();
        }
    }

    public void onBtnOptionsPressed(KeyEvent keyEvent) throws IOException
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            OptionsMenu();
        }
    }

    //endregion Button Actions

    //region Implementations of Button Actions

    private void FirstLevelStart() throws IOException
    {
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
    }


    private void MenuExit()
    {
        removeResizeListener();
        Stage stage = Main.getPrimaryStage();
        stage.close();
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
        optionsController.resizeOnLoad(oldStageX, oldStageY, changeNewX, changeNewY);
        optionsController.addResizeListener();
    }

    //endregion Implementations of Button Actions

    //region Resize Methods

    private void removeResizeListener()
    {
        var stage = Main.getPrimaryStage();
        stage.widthProperty().removeListener(changeListenerMap.get("width"));
        stage.heightProperty().removeListener(changeListenerMap.get("height"));
    }

    public void addResizeListener()
    {
        resize();
    }

    @Override
    protected void resizeOnLoad(Number oldValueX, Number oldValueY, Number newValueX, Number newValueY)
    {
        resizeXAndWidth(oldValueX, newValueX);
        resizeYAndHeight(oldValueY, newValueY);
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


        var stage = Main.getPrimaryStage();

        changeListenerMap.put("width", widthResize);
        changeListenerMap.put("height", heightResize);

        removeResizeListener();

        stage.widthProperty().addListener(changeListenerMap.get("width"));
        stage.heightProperty().addListener(changeListenerMap.get("height"));

    }

    private void resizeXAndWidth(Number oldValue, Number newValue)
    {
        btnStart.setLayoutX(btnStart.getLayoutX() * newValue.doubleValue() / oldValue.doubleValue());
        btnExit.setLayoutX(btnExit.getLayoutX() * newValue.doubleValue() / oldValue.doubleValue());
        btnOptions.setLayoutX(btnOptions.getLayoutX() * newValue.doubleValue() / oldValue.doubleValue());

        btnStart.setPrefWidth(btnStart.getPrefWidth() * newValue.doubleValue() / oldValue.doubleValue());
        btnExit.setPrefWidth(btnExit.getPrefWidth() * newValue.doubleValue() / oldValue.doubleValue());
        btnOptions.setPrefWidth(btnOptions.getPrefWidth() * newValue.doubleValue() / oldValue.doubleValue());
    }

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