package jumper.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.ParallelCamera;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jumper.engine.GameLevelEngine;
import jumper.model.FallingRectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class GameFirstLevelController {
    private static final Logger logger = LogManager.getLogger("GameFirstLevelController");
    private Timer tr, trGenerate;
    private TimerTask ts, tsGenerate;
    protected boolean paused = false;

    private GameLevelEngine gameLevelEngine;

    //region Constructor

    public GameFirstLevelController() {
        logger.debug("GameFirstLevelController constructor called.");
        gameLevelEngine = new GameLevelEngine(FallingRectangle.basicEnemyVelocitiyY,
                GameFirstLevelController.this);
    }

    //endregion Constructor

    //region GameLevelEngine

    public GameLevelEngine getEngine(){
        return this.gameLevelEngine;
    }

    public void initGameEngineLevel(AnchorPane ap) {
        gameLevelEngine.init(ap);
        gameLevelEngine.keyListener();
    }

    public void runGameLevelEngine() {
        ts = new TimerTask() {
            @Override
            public synchronized void run() {
                gameLevelEngine.runTask();
            }
        };

        tsGenerate = new TimerTask() {
            @Override
            public synchronized void run() {
                gameLevelEngine.generateEnemyTask();
            }
        };

        tr = new Timer();
        tr.schedule(ts, 0, 17);
        trGenerate = new Timer();
        trGenerate.schedule(tsGenerate, 0, 4000);
        Stage stage = Main.getPrimaryStage();
        stage.setOnCloseRequest(windowEvent -> {
            tr.cancel();
            trGenerate.cancel();
            stage.close();
        });

    }

    public void escPressed(){
        paused = true;
        gameLevelEngine.setLeftReleased(true);
        gameLevelEngine.setRightReleased(true);
        tr.cancel();
        trGenerate.cancel();
        try {
            var stage = Main.getPrimaryStage();
            var fl = new FXMLLoader(getClass().getClassLoader().getResource("Pause.fxml"));
            var pauseC = new PauseController(GameFirstLevelController.this);
            fl.setController(pauseC);
            var ap = (AnchorPane) fl.load();
            Scene pauseScene = stage.getScene();
            ParallelCamera newCam = new ParallelCamera();
            newCam.setLayoutY(0);
            pauseScene.setCamera(newCam);
            pauseScene.setRoot(ap);
            pauseC.keyListenerPause();
            gameLevelEngine.removeKeyListener();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void errorGoToMainMenu() {
        try {
            tr.cancel();
            trGenerate.cancel();
            var stage = Main.getPrimaryStage();
            var fl = new FXMLLoader(getClass().getClassLoader().getResource("MainMenu.fxml"));
            var mainController = new MainMenuController();
            fl.setController(mainController);
            var ap = (AnchorPane) fl.load();
            var mainScene = Main.getPrimaryStage().getScene();
            mainScene.setRoot(ap);
            gameLevelEngine.removeKeyListener();
        } catch (IOException io) {
            logger.error("MainMenu.fxml not founded, closing application.", io);
            gameLevelEngine.removeKeyListener();
            Main.getPrimaryStage().close();
        } catch (Exception ex) {
            logger.error("Something bad happened, closing application.", ex);
            gameLevelEngine.removeKeyListener();
            Main.getPrimaryStage().close();
        }
    }

    //endregion GameLevelEngine

}
