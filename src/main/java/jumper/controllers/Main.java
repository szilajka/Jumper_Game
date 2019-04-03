package jumper.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main extends Application {
    private static final Logger logger = LogManager.getLogger("Main");
    private static Stage primaryStage;
    private static EntityManagerFactory emf;


    @Override
    public void start(Stage stage) throws Exception {
        logger.debug("Application started. Start function called.");
        emf = Persistence.createEntityManagerFactory("jumper");
        primaryStage = stage;
        AnchorPane ap = (AnchorPane) FXMLLoader.load(getClass().getClassLoader()
                .getResource("Welcome.fxml"));
        Scene scene = new Scene(ap);
        stage.setScene(scene);
        stage.setTitle("Jumper Game");
        stage.show();
        stage.setOnCloseRequest((event) -> {
            logger.debug("Application closes.");
            stage.close();
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        emf.close();
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

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

}
