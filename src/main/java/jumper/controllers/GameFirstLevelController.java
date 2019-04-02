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

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.ParallelCamera;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jumper.Queries.Queries;
import jumper.authentication.Authenticate;
import jumper.engine.GameLevelEngine;
import jumper.helpers.GameEngineHelper;
import jumper.model.DB.AllTime;
import jumper.model.DB.Score;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.TemporalUnit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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

    public void endLevel(int points, double elapsedTime) {
        tr.cancel();
        trGenerate.cancel();
        EntityManager em = Main.getEntityManager();
        Score score = Queries.getScoreByUserName(em, Authenticate.getLoggedInUser());
        AllTime allTime = Queries.getAllTimeByUserName(em, Authenticate.getLoggedInUser());
        if (score != null) {
            score.setLevel(GameEngineHelper.levelCounter + 1);
            score.setScore(score.getScore() + points);


            em.getTransaction().begin();
            em.persist(score);
            em.getTransaction().commit();


            em.detach(score);
        } else {
            logger.error("Score value to {} has not been recorded!",
                    Authenticate.getLoggedInUser().getUserName());
        }
        if(allTime != null){
            int elapsedSecs = Math.toIntExact(Duration.
                    ofNanos(Double.valueOf(elapsedTime).longValue()).toSeconds());
            allTime.setElapsedTime(allTime.getElapsedTime() + elapsedSecs);

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

    public void gameOver() {
        tr.cancel();
        trGenerate.cancel();
        EntityManager em = Main.getEntityManager();
        em.getTransaction().begin();
        Score score = new Score();
        score.setLevel(1);
        score.setScore(0);
        score.setUserName(Authenticate.getLoggedInUser().getUserName());
        em.persist(score);
        em.getTransaction().commit();
        em.close();
        gameLevelEngine.drawGameOverText();
        //TODO: call db, set game level to 0.
    }

    //endregion GameLevelEngine

}
