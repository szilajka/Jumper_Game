package jumper.controllers;

/*-
 * #%L
 * jumper_game
 * %%
 * Copyright (C) 2019 Szilárd Németi
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

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
