package jumper.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.pmw.tinylog.Logger;

/**
 * The main class of the application.
 * <p>
 * This class loads the application.
 */
public class MainJFX extends Application {
    /**
     * The {@link Stage} that is used all over the application.
     */
    private static Stage primaryStage;
    /**
     * An {@link EntityManagerFactory} that is used to create the connection
     * between the application and the database.
     */
    private static EntityManagerFactory emf;


    @Override
    public void start(Stage stage) throws Exception {
        Logger.debug("Application started. Start function called.");
        emf = Persistence.createEntityManagerFactory("jumper");
        primaryStage = stage;
        AnchorPane ap = (AnchorPane) FXMLLoader.load(getClass().getClassLoader()
            .getResource("Welcome.fxml"));
        Scene scene = new Scene(ap);
        stage.setScene(scene);
        stage.setTitle("Jumper Game");
        stage.show();
        stage.setOnCloseRequest((event) -> {
            Logger.debug("Application closes.");
            stage.close();
        });
    }

    @Override
    public void stop() throws Exception {
        if (emf.isOpen()) {
            emf.close();
        }
        super.stop();
    }

    /**
     * The main method of the application.
     *
     * @param args any arguments that the user wants to pass to the application.
     */
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

    /**
     * Creates a new {@link EntityManager} object.
     *
     * @return a new {@code Entity Manager}
     */
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

}
