package jumper.controllers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.ParallelCamera;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jumper.queries.Queries;
import jumper.authentication.Authenticate;
import jumper.engine.GameLevelEngine;
import jumper.helpers.GameEngineHelper;
import jumper.model.DB.AllTime;
import jumper.model.DB.Score;
import jumper.model.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
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
        gameLevelEngine = new GameLevelEngine(GameFirstLevelController.this,
                GameEngineHelper.levelCounter);
    }

    //endregion Constructor

    //region GameLevelEngine

    public GameLevelEngine getEngine() {
        return this.gameLevelEngine;
    }

    public void initGameEngineLevel(AnchorPane ap) {
        EntityManager em = Main.getEntityManager();
        em.getTransaction().begin();
        Score score = Queries.getScoreByUserName(em, Authenticate.getLoggedInUser());
        em.close();
        if (score != null) {
            gameLevelEngine.setLevelCounter(score.getLevel());
        }
        int velocityY = (score == null ? Player.finalStartVelocityY : score.getVelocityY());
        gameLevelEngine.init(ap, velocityY);
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
            Platform.exit();
        });

    }

    public void escPressed() {
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
            pauseC.setInGanemTime();
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
            var mainScene = stage.getScene();
            ParallelCamera newCam = new ParallelCamera();
            newCam.setLayoutY(0);
            mainScene.setCamera(newCam);
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

    public void endLevel(int points, double elapsedTime, int velocityY) {
        tr.cancel();
        trGenerate.cancel();
        EntityManager em = Main.getEntityManager();
        Score score = Queries.getScoreByUserName(em, Authenticate.getLoggedInUser());
        AllTime allTime = Queries.getAllTimeByUserName(em, Authenticate.getLoggedInUser());
        if (score != null) {
            score.setLevel(GameEngineHelper.levelCounter + 1);
            score.setScore(score.getScore() + points);
            score.setVelocityY(velocityY);
            em.getTransaction().begin();
            em.persist(score);
            em.getTransaction().commit();
            em.detach(score);
        } else {
            logger.error("Score value to {} has not been recorded!",
                    Authenticate.getLoggedInUser().getUserName());
        }
        if(allTime != null){
            int elapsedSecs = Math.toIntExact(Double.valueOf(elapsedTime).longValue());
            allTime.setElapsedTime(allTime.getElapsedTime() + elapsedSecs);
            em.getTransaction().begin();
            em.persist(allTime);
            em.getTransaction().commit();
            em.detach(allTime);
        }
        em.close();
        gameLevelEngine.drawNextLevelText();
    }

    public void nextLevel() {
        GameEngineHelper.levelCounter++;
        gameLevelEngine.resetLevel();
        gameLevelEngine.setLevelCounter(GameEngineHelper.levelCounter);
        runGameLevelEngine();
        //TODO
    }

    public void gameOver(double elapsedTime) {
        tr.cancel();
        trGenerate.cancel();
        EntityManager em = Main.getEntityManager();
        AllTime allTime = Queries.getAllTimeByUserName(em, Authenticate.getLoggedInUser());
        int elapsedSecs = Math.toIntExact(Double.valueOf(elapsedTime).longValue());
        allTime.setElapsedTime(allTime.getElapsedTime() + elapsedSecs);
        Score score = new Score();
        score.setLevel(1);
        score.setScore(0);
        score.setVelocityY(Player.finalStartVelocityY);
        score.setUserName(Authenticate.getLoggedInUser().getUserName());
        em.getTransaction().begin();
        em.persist(score);
        em.persist(allTime);
        em.getTransaction().commit();
        em.detach(allTime);
        em.detach(score);
        em.close();
        gameLevelEngine.drawGameOverText();
        //TODO: call db, set game level to 0.
    }

    //endregion GameLevelEngine

}
