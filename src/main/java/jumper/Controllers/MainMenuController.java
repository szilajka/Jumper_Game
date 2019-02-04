package jumper.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {
    public Button btnStart;
    public Button btnExit;

    public void onBtnStartClicked(MouseEvent mouseEvent) throws IOException {
        var root = (AnchorPane) FXMLLoader.load(getClass().getClassLoader().getResource("GameFirstLevel.fxml"));
        Stage stage = (Stage) btnStart.getScene().getWindow();
        Scene gameScene = new Scene(root);
        stage.setScene(gameScene);
        GameFirstLevelController.init(root);
        GameFirstLevelController.showText(root);
        stage.show();
    }

    public void onBtnExitClicked(MouseEvent mouseEvent) {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }
}
