package jumper.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main extends Application {
    private static final Logger logger = LogManager.getLogger("Main");
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        logger.debug("Application started. Start function called.");
        primaryStage = stage;
        var fl = new FXMLLoader(getClass().getClassLoader().getResource("MainMenu.fxml"));
        var mainMenuController = new MainMenuController();
        fl.setController(mainMenuController);
        Parent root = fl.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Jumper Game");
        stage.show();
        stage.setOnCloseRequest((event) -> {
            logger.debug("Application closes.");
            stage.close();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This function returns the {@link Stage} that is used by this application.
     *
     * @return A {@link Stage}, that is used by the application.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

}
