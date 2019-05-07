package jumper.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.ParallelCamera;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jumper.authentication.Authenticate;
import jumper.engine.GameEngine;
import jumper.helpers.GameEngineHelper;
import jumper.model.DB.AllTime;
import jumper.model.DB.Score;
import jumper.model.Player;
import jumper.queries.Queries;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.tinylog.Logger;

/**
 * {@code Controller} of the {@code GameFirstLevel} view.
 */
public class GameLevelController {
    /**
     * A {@link Timer} that starts the screen updating.
     */
    private Timer timerUpdate;
    /**
     * A {@link Timer} that starts the enemy generating.
     */
    private Timer timerEnemyGen;
    /**
     * A {@link TimerTask} which is called by the {@code timerUpdate}
     * {@link Timer} to run the updates.
     */
    private TimerTask tsUpdate;
    /**
     * A {@link TimerTask} which is called by the {@code timerEnemyGen}
     * {@link Timer} to generate enemies.
     */
    private TimerTask tsEnemyGen;
    /**
     * Indicates whether is paused or not.
     */
    protected boolean paused = false;

    /**
     * The {@link GameEngine} object that computes the updates.
     */
    private GameEngine gameEngine;

    //region Constructor

    /**
     * An empty {@code constructor} of the class.
     */
    public GameLevelController() {
        Logger.debug("GameLevelController constructor called.");
        gameEngine = new GameEngine(GameLevelController.this,
            GameEngineHelper.levelCounter);
    }

    //endregion Constructor

    //region GameEngine

    /**
     * Gets the {@link GameEngine} that computes the updates.
     *
     * @return the {@code Game Level Engine} object
     */
    public GameEngine getEngine() {
        Logger.debug("getEngine() method called.");
        return this.gameEngine;
    }

    /**
     * Initialize the {@code engine} with the proper values.
     *
     * @param ap the {@link AnchorPane} that the engine will draw on
     */
    public void initGameEngineLevel(AnchorPane ap) {
        EntityManager em = MainJFX.getEntityManager();
        try {
            Logger.debug("initGameEngineLevel() method called.");
            em.getTransaction().begin();
            Score score = Queries.getScoreByUserName(em, Authenticate.getLoggedInUser());
            if (score != null) {
                Logger.debug("No score found with the current user.");
                gameEngine.setLevelCounter(score.getLevel());
            }
            int velocityY = (score == null ? Player.finalStartVelocityY : score.getVelocityY());
            gameEngine.init(ap, velocityY);
            gameEngine.keyListener();
            Logger.debug("initGameEngineLevel() method finished.");
        } catch (Exception ex) {
            Logger.error("Some error occurred during initializing Game Engine Level.", ex);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Initializes the {@link TimerTask}s and
     * starts the {@link Timer}s and starts the {@code game}.
     */
    public void runGameLevelEngine() {
        Logger.debug("runGameLevelEngine() method called.");
        tsUpdate = new TimerTask() {
            @Override
            public synchronized void run() {
                gameEngine.runTask();
            }
        };

        tsEnemyGen = new TimerTask() {
            @Override
            public synchronized void run() {
                gameEngine.generateEnemyTask();
            }
        };

        timerUpdate = new Timer();
        timerUpdate.schedule(tsUpdate, 0, 17);
        timerEnemyGen = new Timer();
        timerEnemyGen.schedule(tsEnemyGen, 0, 2000);
        Stage stage = MainJFX.getPrimaryStage();
        stage.setOnCloseRequest(windowEvent -> {
            timerUpdate.cancel();
            timerEnemyGen.cancel();
            MainJFX.stopEMF();
            stage.close();
            //Platform.exit();
        });
        Logger.debug("runGameLevelEngine() method finished.");
    }

    /**
     * Handles the ESCAPE key presses.
     * <p>
     * If the game is not paused and the {@code ESCAPE} key pressed, then loads the {@code pause scene}.
     */
    public void escPressed() {
        Logger.debug("escPressed() method called.");
        paused = true;
        gameEngine.setLeftReleased(true);
        gameEngine.setRightReleased(true);
        timerUpdate.cancel();
        timerEnemyGen.cancel();
        try {
            var stage = MainJFX.getPrimaryStage();
            stage.setOnCloseRequest(windowEvent -> {
                MainJFX.stopEMF();
                stage.close();
                //Platform.exit();
            });
            var fl = new FXMLLoader(getClass().getClassLoader().getResource("Pause.fxml"));
            var pauseC = new PauseController(GameLevelController.this);
            fl.setController(pauseC);
            var ap = (AnchorPane) fl.load();
            Scene pauseScene = stage.getScene();
            ParallelCamera newCam = new ParallelCamera();
            newCam.setLayoutY(0);
            pauseScene.setCamera(newCam);
            pauseScene.setRoot(ap);
            pauseC.keyListenerPause();
            pauseC.setInGameTime();
            gameEngine.removeKeyListener();
            Logger.debug("escPressed() method finished.");
        } catch (IOException io) {
            Logger.error("Pause.fxml not found, going back to MainJFX Menu.", io);
            errorGoToMainMenu();
        }
    }

    /**
     * Goes to the MainJFX Menu if any error occurred.
     */
    public void errorGoToMainMenu() {
        try {
            Logger.debug("errorGoToMainMenu() method called.");
            timerUpdate.cancel();
            timerEnemyGen.cancel();
            var stage = MainJFX.getPrimaryStage();
            stage.setOnCloseRequest(windowEvent -> {
                MainJFX.stopEMF();
                stage.close();
                //Platform.exit();
            });
            var fl = new FXMLLoader(getClass().getClassLoader().getResource("MainMenu.fxml"));
            var mainController = new MainMenuController();
            fl.setController(mainController);
            var ap = (AnchorPane) fl.load();
            var mainScene = stage.getScene();
            ParallelCamera newCam = new ParallelCamera();
            newCam.setLayoutY(0);
            mainScene.setCamera(newCam);
            mainScene.setRoot(ap);
            mainController.setInGameTime();
            gameEngine.removeKeyListener();
            Logger.debug("errorGoToMainMenu() method finished.");
        } catch (IOException io) {
            Logger.error("MainMenu.fxml not founded, closing application.", io);
            gameEngine.removeKeyListener();
            MainJFX.getPrimaryStage().close();
        } catch (Exception ex) {
            Logger.error("Something bad happened, closing application.", ex);
            gameEngine.removeKeyListener();
            MainJFX.getPrimaryStage().close();
        }
    }

    /**
     * Handles the end of the actual {@code game level}.
     * Saves the score, the elapsed time to database and draws the {@code next level} text.
     *
     * @param points      the points the user earned in this {@code level}
     * @param elapsedTime the time the user spent in the game while completing this {@code level}
     * @param velocityY   the Y velocity of the user at the end of the {@code level}
     */
    public void endLevel(int points, double elapsedTime, int velocityY) {
        EntityManager em = MainJFX.getEntityManager();
        try {
            Logger.debug("endLevel() method called.");
            timerUpdate.cancel();
            timerEnemyGen.cancel();
            Logger.debug("Saving to database started.");
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
                Logger.debug("Score {} and next level {} has been saved to {} user",
                    score.getScore(), score.getLevel(),
                    Authenticate.getLoggedInUser().getUserName());
            } else {
                Logger.error("Score value to {} has not been recorded!",
                    Authenticate.getLoggedInUser().getUserName());
            }
            if (allTime != null) {
                int elapsedSecs = Math.toIntExact(Double.valueOf(elapsedTime).longValue());
                allTime.setElapsedTime(allTime.getElapsedTime() + elapsedSecs);
                em.getTransaction().begin();
                em.persist(allTime);
                em.getTransaction().commit();
                em.detach(allTime);
                Logger.debug("{} elapsed seconds has been saved to {} user.",
                    allTime.getElapsedTime(), Authenticate.getLoggedInUser().getUserName());
            }
            Logger.debug("Saving to database finished.");
            gameEngine.drawNextLevelText();
            Logger.debug("endLevel() method finished.");
        } catch (Exception ex) {
            Logger.error("Some error occurred during ending level.", ex);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Handles the loading of the next {@code level}.
     */
    public void nextLevel() {
        Logger.debug("nextLevel() method called.");
        GameEngineHelper.levelCounter++;
        gameEngine.resetLevel();
        gameEngine.setLevelCounter(GameEngineHelper.levelCounter);
        runGameLevelEngine();
        Logger.debug("nextLevel() method finished.");
    }

    /**
     * Handles when the {@code user} died in the game.
     *
     * @param elapsedTime the time the {@code user} spent in this game
     *                    while completing this {@code level}
     */
    public void gameOver(double elapsedTime) {
        EntityManager em = MainJFX.getEntityManager();
        try {

            Logger.debug("gameOver() method called.");
            timerUpdate.cancel();
            timerEnemyGen.cancel();
            GameEngineHelper.levelCounter = 1;
            AllTime allTime = Queries.getAllTimeByUserName(em, Authenticate.getLoggedInUser());
            int elapsedSecs = Math.toIntExact(Double.valueOf(elapsedTime).longValue());
            if (allTime != null) {
                allTime.setElapsedTime(allTime.getElapsedTime() + elapsedSecs);
            }
            Score score = new Score();
            score.setLevel(1);
            score.setScore(0);
            score.setVelocityY(Player.finalStartVelocityY);
            score.setUserName(Authenticate.getLoggedInUser().getUserName());
            Logger.debug("Saving to database started.");
            em.getTransaction().begin();
            em.persist(score);
            em.persist(allTime);
            em.getTransaction().commit();
            em.detach(allTime);
            em.detach(score);
            Logger.debug("Saving to database finished.");
            gameEngine.drawGameOverText();
            Logger.debug("gameOver() method finished.");
        } catch (Exception ex) {
            Logger.error("Some error occurred during gameOver().", ex);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    //endregion GameEngine

}
