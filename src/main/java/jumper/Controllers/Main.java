package jumper.Controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This is the Main {@code class} of the application.
 * This {@code class} is responsible to start the application.
 */
public class Main extends Application
{

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception
    {
        primaryStage = stage;
        var fl = new FXMLLoader(getClass().getClassLoader().getResource("MainMenu.fxml"));
        var mainMenuController = new MainMenuController();
        fl.setController(mainMenuController);
        Parent root = fl.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Jumper Game");
        stage.show();
        mainMenuController.addResizeListener();
        mainMenuController.addResizeListenerToStage();
        mainMenuController.setOldAndNewValues(stage.getWidth(), stage.getHeight(), stage.getWidth(), stage.getHeight());
        stage.setOnCloseRequest((event) -> {
            stage.close();
        });
    }

    public static void main(String[] args)
    {
        launch(args);
    }

    /**
     * This function returns the {@link Stage} that is used by this application.
     * @return A {@link Stage}, that is used by the application.
     */
    public static Stage getPrimaryStage()
    {
        return primaryStage;
    }

}
