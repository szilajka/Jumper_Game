package jumper.Controllers;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class PauseController
{
    public Button btnContinue;
    public Button btnExit;
    public Button btnMenu;
    private GameFirstLevelController gflc;

    public PauseController(GameFirstLevelController gflc)
    {
        this.gflc = gflc;
    }

    protected void keyListenerPause()
    {
        var pauseEH = new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent keyEvent)
            {
                var kc = keyEvent.getCode();
                if (kc == KeyCode.ESCAPE)
                {
                    try
                    {
                        continueFirstLevel();
                        btnMenu.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, this);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        btnMenu.getScene().addEventHandler(KeyEvent.KEY_PRESSED, pauseEH);
    }

    private void continueFirstLevel() throws IOException
    {
        var stage = (Stage) btnContinue.getScene().getWindow();
        var fl = new FXMLLoader(getClass().getClassLoader().getResource("GameFirstLevel.fxml"));
        fl.setController(gflc);
        var ap = (AnchorPane) fl.load();
        var gameScene = new Scene(ap);
        stage.setScene(gameScene);
        stage.show();
        gflc.letsContinue(ap);
    }

    public void OnBtnContinuePressed(KeyEvent keyEvent) throws IOException
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            continueFirstLevel();
        }
    }

    public void OnBtnContinueClicked(MouseEvent mouseEvent) throws IOException
    {
        continueFirstLevel();
    }

    public void OnBtnExitPressed(KeyEvent keyEvent)
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            AppExit();
        }
    }

    public void OnBtnExitClicked(MouseEvent mouseEvent)
    {
        AppExit();
    }

    public void OnBtnMenuPressed(KeyEvent keyEvent) throws IOException
    {
        if (keyEvent.getCode() == KeyCode.ENTER)
        {
            loadMainMenu(keyEvent);
        }
    }

    public void OnBtnMenuClicked(MouseEvent mouseEvent) throws IOException
    {
        loadMainMenu(mouseEvent);
    }

    private void AppExit()
    {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    private void loadMainMenu(Event event) throws IOException
    {
        var mainMenu = (AnchorPane) FXMLLoader.load(getClass().getClassLoader().getResource("MainMenu.fxml"));
        var stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(mainMenu));
        stage.show();
    }

}
