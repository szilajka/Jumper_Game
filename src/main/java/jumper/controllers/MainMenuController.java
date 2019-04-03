package jumper.controllers;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import java.io.IOException;

/**
 * {@code Controller} of the {@code MainMenu view}.
 */
public class MainMenuController {
    /**
     * The Start button.
     */
    public Button btnStart;
    /**
     * The Scoreboard button
     */
    public Button btnScoreBoard;
    /**
     * The Exit button.
     */
    public Button btnExit;
    /**
     * Label for text: {@code 'Time in game:'}
     */
    public Label lblTime;
    /**
     * Label for the time that the user spent in this game.
     */
    public Label lblTimeQuery;

    /**
     * The {@link Logger} of the class.
     */
    private static final Logger logger = LogManager.getLogger("MainMenuController");

    //region Constructors

    /**
     * Constructor of the class.
     * Creates a new {@code MainMenuController} instance.
     */
    public MainMenuController() {
        logger.debug("MainMenuController constructor called.");
    }

    //endregion Constructors

    /**
     * Sets the
     */
    public void setInGameTime(){
        EntityManager em = Main.getEntityManager();
        AllTime allTime = Queries.getAllTimeByUserName(em, Authenticate.getLoggedInUser());
        int elapsedSecs = allTime == null ? 0 : allTime.getElapsedTime();
        String formattedTime = TimeHelper.convertSecondsToDuration(elapsedSecs);
        lblTimeQuery.setText(formattedTime);
    }

    //region Button Actions

    /**
     * Handles the button click on button Start.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     * @throws IOException If the FXML file is not existing.
     */
    public void onBtnStartClicked(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
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
    public void onBtnStartPressed(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            logger.debug("BtnStart pressed.");
            FirstLevelStart();
        }
    }

    /**
     * Handles the button click on button Scoreboard.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     */
    public void onBtnScoreBoardClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            logger.debug("BtnScoreBoard clicked.");
            ScoreBoard();
        }
    }


    /**
     * Handles the key press on button Scoreboard.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     */
    public void onBtnScoreBoardPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            logger.debug("BtnScoreBoard pressed.");
            ScoreBoard();
        }
    }

    /**
     * Handles the button click on button Exit.
     *
     * @param mouseEvent The {@link MouseEvent} that triggers this method.
     */
    public void onBtnExitClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            logger.debug("BtnExit clicked.");
            MenuExit();
        }
    }

    /**
     * Handles the key press on button Exit.
     *
     * @param keyEvent The {@link KeyEvent} that triggers this method.
     */
    public void onBtnExitPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            logger.debug("BtnExit pressed.");
            MenuExit();
        }
    }

    //endregion Button Actions

    //region Implementations of Button Actions

    /**
     * Implements the loading of the first level.
     */
    private void FirstLevelStart() {
        try {
            logger.debug("FirstLevelStart method called.");
            Stage stage = Main.getPrimaryStage();

            var fl = new FXMLLoader(getClass().getClassLoader()
                    .getResource("GameFirstLevel.fxml"));
            var gameFirstLC = new GameFirstLevelController();
            fl.setController(gameFirstLC);
            var root = (AnchorPane) fl.load();

            Scene gameScene = stage.getScene();
            gameScene.setRoot(root);
            gameFirstLC.initGameEngineLevel(root);
            gameFirstLC.runGameLevelEngine();
            logger.debug("FirstLevelStart method has finished.");
        } catch (IOException io) {
            logger.error("GameFirstLevel.fxml has not found, closing application.", io);
            MenuExit();
        } catch (Exception ex) {
            logger.error("Some error occured, closing application.", ex);
            MenuExit();
        }
    }

    /**
     * Implements the loading of ScoreBoard menu.
     */
    private void ScoreBoard() {
        try {
            var stage = Main.getPrimaryStage();
            FXMLLoader fl = new FXMLLoader(getClass().getClassLoader()
                    .getResource("Scoreboard.fxml"));
            ScoreboardController sbController = new ScoreboardController();
            fl.setController(sbController);
            AnchorPane ap = (AnchorPane) fl.load();
            var sbScene = stage.getScene();
            sbScene.setRoot(ap);
            sbController.setTableView();
            sbController.keyListenerScoreBoard();
        } catch (IOException io) {
            logger.error("Scoreboard.fxml not found, closing the application.", io);
            MenuExit();
        }
    }

    /**
     * Implements exiting the application.
     */
    private void MenuExit() {
        logger.debug("MenuExit() method called.");
        Stage stage = Main.getPrimaryStage();
        stage.close();
    }

    //endregion Implementations of Button Actions

}
