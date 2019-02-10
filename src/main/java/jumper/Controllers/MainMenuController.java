package jumper.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {
    public Button btnStart;
    public Button btnExit;

    public void onBtnStartClicked(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            FirstLevelStart();
        }
    }

    private void FirstLevelStart() throws IOException {
        var root = (AnchorPane) FXMLLoader.load(getClass().getClassLoader().getResource("GameFirstLevel.fxml"));
        Stage stage = (Stage) btnStart.getScene().getWindow();
        Scene gameScene = new Scene(root);
        stage.setScene(gameScene);
        var gflC = new GameFirstLevelController();
        gflC.init(root);
        stage.show();
        gflC.run();
    }

    public void onBtnExitClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            MenuExit();
        }
    }

    private void MenuExit() {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    public void onBtnStartPressed(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            FirstLevelStart();
        }
    }

    public void onBtnExitPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            MenuExit();
        }
    }
}
