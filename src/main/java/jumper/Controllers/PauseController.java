package jumper.Controllers;

import javafx.event.EventHandler;
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

/**
 * This class is the {@code controller} of the Pause menu.
 * This {@link Scene} is only available while the user is playing.
 */
public class PauseController
{
    public Button btnContinue;
    public Button btnExit;
    public Button btnMenu;
    private GameFirstLevelController gflc;

    //region Constructor

    /**
     * The constructor of this class.
     * We give the game level controller as parameter, so later we can continue the game from where we paused.
     *
     * @param gflc The {@link GameFirstLevelController} that paused the game.
     */
    public PauseController(GameFirstLevelController gflc)
    {
        this.gflc = gflc;
    }

    //endregion Constructor

    //region Resize Methods

    //endregion Resize Methods

    //region Key Listener

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
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    /**
     * Implements the menu button's behaviour.
     *
     * @throws IOException If the fxml file is not found.
     */
    private void loadMainMenu() throws IOException
    {
        var fl = new FXMLLoader(getClass().getClassLoader().getResource("MainMenu.fxml"));
        fl.setController(new MainMenuController());
        var mainMenu = (AnchorPane) fl.load();
        var stage = Main.getPrimaryStage();
        stage.setScene(new Scene(mainMenu));
        stage.show();
    }

    /**
     * Implements the continue button's behaviour.
     *
     * @throws IOException If the fxml file is not found.
     */
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

    //endregion Implementation of Button Actions

}
