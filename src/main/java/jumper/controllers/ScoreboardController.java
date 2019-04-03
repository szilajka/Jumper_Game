package jumper.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
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

public class ScoreboardController {
    private static final Logger logger = LogManager.getLogger("ScoreboardController");
    public Label labelScoreBoard;
    public TableView<ScoreBoard> tableViewScoreBoard;
    public TableColumn<ScoreBoard, String> tvNum;
    public TableColumn<ScoreBoard, String> tvUserName;
    public TableColumn<ScoreBoard, String> tvScore;

    public ScoreboardController() {
    }

    public void setListView() {
        try {
            EntityManager em = Main.getEntityManager();
            List<Score> scoreList = Queries.getTopTenScoreBoard(em);
            List<ScoreBoard> sbList = new ArrayList<>();
            for (int i = 0; i < scoreList.size(); i++) {
                Score score = scoreList.get(i);
                ScoreBoard sb = new ScoreBoard(i + 1, score.getUserName(),
                        score.getScore());
                sbList.add(sb);
            }
            ObservableList<ScoreBoard> sbObsList = FXCollections.observableList(sbList);

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
