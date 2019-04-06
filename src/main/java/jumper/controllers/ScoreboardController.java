package jumper.controllers;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jumper.model.DB.Score;
import jumper.model.ScoreBoard;
import jumper.queries.Queries;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tinylog.Logger;

/**
 * {@code Controller} of the {@code Scoreboard} view.
 */
public class ScoreboardController {
    /**
     * {@link Label} for text: {@code Scoreboard}.
     */
    public Label labelScoreBoard;
    /**
     * {@link TableView} that will contain the {@link ScoreBoard} elements.
     */
    public TableView<ScoreBoard> tableViewScoreBoard;
    /**
     * {@link TableColumn} of the {@link #tableViewScoreBoard}.
     * <p>
     * This will contain the row number.
     */
    public TableColumn<ScoreBoard, String> tvNum;
    /**
     * {@link TableColumn} of the {@link #tableViewScoreBoard}.
     * <p>
     * This will contain the username of the user in the table.
     */
    public TableColumn<ScoreBoard, String> tvUserName;
    /**
     * {@link TableColumn} of the {@link #tableViewScoreBoard}.
     * <p>
     * This will contain the score of the user in the table.
     */
    public TableColumn<ScoreBoard, String> tvScore;
    /**
     * This {@link Map} stores the {@link EventHandler}s that responds to key presses.
     */
    private Map<EventType<KeyEvent>, EventHandler<KeyEvent>> keyEventHandlerMap;

    /**
     * Creates an empty {@code ScoreboardController}.
     */
    public ScoreboardController() {
        this.keyEventHandlerMap = new HashMap<>();
        Logger.info("A new ScoreboardController object created.");
    }

    /**
     * Sets the {@link TableView}'s elements.
     * <p>
     * Sets the scoreboard's values with the Top 10 {@link jumper.model.DB.User} score.
     */
    public void setTableView() {
        try {
            Logger.debug("setTableView() method started.");
            EntityManager em = MainJFX.getEntityManager();
            List<Score> scoreList = Queries.getTopTenScoreBoard(em);
            List<ScoreBoard> sbList = new ArrayList<>();
            for (int i = 0; i < scoreList.size(); i++) {
                Score score = scoreList.get(i);
                ScoreBoard sb = new ScoreBoard(i + 1, score.getUserName(),
                    score.getScore());
                sbList.add(sb);
            }
            tvNum.setCellValueFactory(new PropertyValueFactory<>("rowNumber"));
            tvUserName.setCellValueFactory(new PropertyValueFactory<>("userName"));
            tvScore.setCellValueFactory(new PropertyValueFactory<>("score"));
            tvNum.setSortType(TableColumn.SortType.ASCENDING);
            tableViewScoreBoard.setEditable(false);
            tableViewScoreBoard.setItems(FXCollections.observableList(sbList));
            Logger.debug("setTableView() method finished.");
        } catch (Exception ex) {
            Logger.error("Some error occured during during setting ListView.", ex);
        }
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
     * Implements the behaviour of this {@code scene} when a key is pressed.
     */
    public void keyListenerScoreBoard() {
        Logger.debug("keyListenerScoreBoard() method called.");
        var pauseEH = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                var kc = keyEvent.getCode();
                if (kc == KeyCode.ESCAPE) {
                    Logger.info("ESCAPE has been pressed.");
                    var stage = MainJFX.getPrimaryStage();
                    stage.removeEventHandler(KeyEvent.KEY_PRESSED,
                        keyEventHandlerMap.get(KeyEvent.KEY_PRESSED));
                    GoToMainMenu();
                }
            }
        };

        removeKeyListener();
        keyEventHandlerMap.put(KeyEvent.KEY_PRESSED, pauseEH);
        var stage = MainJFX.getPrimaryStage();
        stage.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandlerMap.get(KeyEvent.KEY_PRESSED));
        Logger.debug("keyListenerScoreBoard() method finished.");
    }

    /**
     * Implements the loading of the MainJFX Menu.
     */
    private void GoToMainMenu() {
        try {
            Logger.debug("GoToMainMenu() method called.");
            var stage = MainJFX.getPrimaryStage();
            FXMLLoader fl = new FXMLLoader(getClass().getClassLoader()
                .getResource("MainMenu.fxml"));
            var mainMenuController = new MainMenuController();
            fl.setController(mainMenuController);
            AnchorPane ap = (AnchorPane) fl.load();
            var mainScene = stage.getScene();
            mainScene.setRoot(ap);
            mainMenuController.setInGameTime();
            Logger.debug("GoToMainMenu() method finished.");
        } catch (IOException io) {
            Logger.error("MainMenu.fxml not found, closing the application.", io);
            MenuExit();
        }
    }

    /**
     * Implements exiting the application.
     */
    private void MenuExit() {
        Logger.debug("MenuExit() method called.");
        Stage stage = MainJFX.getPrimaryStage();
        stage.close();
    }

}
