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
import org.tinylog.Logger;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * {@code Controller} of the {@code GameFirstLevel} view.
 */
public class GameFirstLevelController {
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
     * The {@link GameLevelEngine} object that computes the updates.
     */
    private GameLevelEngine gameLevelEngine;

    //region Constructor

    /**
     * An empty {@code constructor} of the class.
     */
    public GameFirstLevelController() {
        Logger.debug("GameFirstLevelController constructor called.");
        gameLevelEngine = new GameLevelEngine(GameFirstLevelController.this,
                GameEngineHelper.levelCounter);
    }

    //endregion Constructor

    //region GameLevelEngine

    /**
     * Gets the {@link GameLevelEngine} that computes the updates.
     *
     * @return the {@code Game Level Engine} object
     */
    public GameLevelEngine getEngine() {
        Logger.debug("getEngine() method called.");
        return this.gameLevelEngine;
    }

    /**
     * Initialize the {@code engine} with the proper values.
     *
     * @param ap the {@link AnchorPane} that the engine will draw on
     */
    public void initGameEngineLevel(AnchorPane ap) {
        Logger.debug("initGameEngineLevel() method called.");
        EntityManager em = Main.getEntityManager();
        em.getTransaction().begin();
        Score score = Queries.getScoreByUserName(em, Authenticate.getLoggedInUser());
        em.close();
        if (score != null) {
            Logger.debug("No score found with the current user.");
            gameLevelEngine.setLevelCounter(score.getLevel());
        }
        int velocityY = (score == null ? Player.finalStartVelocityY : score.getVelocityY());
        gameLevelEngine.init(ap, velocityY);
        gameLevelEngine.keyListener();
        Logger.debug("initGameEngineLevel() method finished.");
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
                gameLevelEngine.runTask();
            }
        };

        tsEnemyGen = new TimerTask() {
            @Override
            public synchronized void run() {
                gameLevelEngine.generateEnemyTask();
            }
        };

        timerUpdate = new Timer();
        timerUpdate.schedule(tsUpdate, 0, 17);
        timerEnemyGen = new Timer();
        timerEnemyGen.schedule(tsEnemyGen, 0, 4000);
        Stage stage = Main.getPrimaryStage();
        stage.setOnCloseRequest(windowEvent -> {
            timerUpdate.cancel();
            timerEnemyGen.cancel();
            stage.close();
            Platform.exit();
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
        gameLevelEngine.setLeftReleased(true);
        gameLevelEngine.setRightReleased(true);
        timerUpdate.cancel();
        timerEnemyGen.cancel();
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
            pauseC.setInGameTime();
            gameLevelEngine.removeKeyListener();
            Logger.debug("escPressed() method finished.");
        } catch (IOException io) {
            Logger.error("Pause.fxml not found, going back to Main Menu.", io);
            errorGoToMainMenu();
        }
    }

    /**
     * Goes to the Main Menu if any error occurred.
     */
    public void errorGoToMainMenu() {
        try {
            Logger.debug("errorGoToMainMenu() method called.");
            timerUpdate.cancel();
            timerEnemyGen.cancel();
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
            Logger.debug("errorGoToMainMenu() method finished.");
        } catch (IOException io) {
            Logger.error("MainMenu.fxml not founded, closing application.", io);
            gameLevelEngine.removeKeyListener();
            Main.getPrimaryStage().close();
        } catch (Exception ex) {
            Logger.error("Something bad happened, closing application.", ex);
            gameLevelEngine.removeKeyListener();
            Main.getPrimaryStage().close();
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
        Logger.debug("endLevel() method called.");
        timerUpdate.cancel();
        timerEnemyGen.cancel();
        Logger.debug("Saving to database started.");
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
            Logger.debug("Score {} has been saved to {} user", score.getScore(),
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
        em.close();
        Logger.debug("Saving to database finished.");
        gameLevelEngine.drawNextLevelText();
        Logger.debug("endLevel() method finished.");
    }

    /**
     * Handles the loading of the next {@code level}.
     */
    public void nextLevel() {
        Logger.debug("nextLevel() method called.");
        GameEngineHelper.levelCounter++;
        gameLevelEngine.resetLevel();
        gameLevelEngine.setLevelCounter(GameEngineHelper.levelCounter);
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
        Logger.debug("gameOver() method called.");
        timerUpdate.cancel();
        timerEnemyGen.cancel();
        EntityManager em = Main.getEntityManager();
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
        em.close();
        Logger.debug("Saving to database finished.");
        gameLevelEngine.drawGameOverText();
        Logger.debug("gameOver() method finished.");
    }

    //endregion GameLevelEngine

}
