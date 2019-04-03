package jumper.controllers;

import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import jumper.model.DB.Score;
import jumper.model.ScoreBoard;
import jumper.queries.Queries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code Controller} of the {@code Scoreboard} view.
 */
public class ScoreboardController {
    /**
     * {@link Logger} of the class.
     */
    private static final Logger logger = LogManager.getLogger("ScoreboardController");
    /**
     * {@link Label} for text: {@code Scoreboard}
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
     * Creates an empty {@code ScoreboardController}
     */
    public ScoreboardController() {
    }

    /**
     * Sets the {@link TableView}'s elements.
     * <p>
     * Sets the scoreboard's values with the Top 10 {@link jumper.model.DB.User} score.
     */
    public void setTableView() {
        try {
            logger.debug("setTableView() method started.");
            EntityManager em = Main.getEntityManager();
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
        } catch (Exception ex) {
            logger.error("Some error occured during during setting ListView.", ex);
        }
    }

}
