package jumper.Controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


/**
 * Base {@code class} of all {@code controller classes} in this application.
 * This is an abstract class, contains {@code methods} and {@code variables} that are used in the program.
 */
public abstract class AbstractController
{
    double oldValX = 800.0, oldValY = 600.0, changeNewX, changeNewY, newStageX, newStageY;
    static double oldStageX, oldStageY;

    /**
     * Abstract method, only for {@link OptionsController} to reach the given controller's {@code resizeOnLoad} method.
     * Use to resize the {@link Scene} before show it to user.
     * Put it in action methods (i.e. on button press, on button click), because it was created to use between navigating pages.
     * @param oldValueX {@link Stage}'s old X coordinate before change (if there was a change)
     * @param oldValueY {@link Stage}'s old Y coordinate before change (if there was a change)
     * @param newValueX {@link Stage}'s new X coordinate after change (if there was a change)
     * @param newValueY {@link Stage}'s new Y coordinate after change (if there was a change)
     */
    protected void resizeOnLoad(Number oldValueX, Number oldValueY, Number newValueX, Number newValueY)
    {
    }

    /**
     * Use it after loading first {@link Scene}.
     * Sets the parameters to the same X and Y value.
     * The parameters are the {@link Stage}'s {@code width} and {@code height}.
     * When resizing scenes, the scene's width and height are less than the stage's, and to avoid screen size decrease,
     * use the old coordinates.
     * @param oldValueX {@link Stage}'s original Width
     * @param oldValueY {@link Stage}'s original Height
     * @param newValueX {@link Stage}'s new Width
     * @param newValueY {@link Stage}'s new Height
     */
    protected void setOldAndNewValues(double oldValueX, double oldValueY, double newValueX, double newValueY)
    {
        this.oldStageX = oldValueX;
        this.oldStageY = oldValueY;
        this.changeNewX = newValueX;
        this.changeNewY = newValueY;
    }

    /**
     * Adds {@link ChangeListener}s to the main stage.
     * If the {@link Stage}'s width or height changes, it stores the values in {@code changeNewX} and {@code changeNewY}.
     */
    protected void addResizeListenerToStage()
    {
        var stage = Main.getPrimaryStage();
        var widthResize = new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue)
            {
                changeNewX = newValue.doubleValue();
            }
        };

        var heightResize = new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue)
            {
                changeNewY = newValue.doubleValue();
            }
        };

        stage.widthProperty().removeListener(widthResize);
        stage.heightProperty().removeListener(heightResize);
        stage.widthProperty().addListener(widthResize);
        stage.heightProperty().addListener(heightResize);
    }


    //Another class idea:

    /**
     * Stores the {@link Pane}'s width and height and the {@link Stage}'s width and height.
     * Use it if you want to change {@link Scene}, after setting the scene's root, call this method.
     * @param pane The {@link Pane} which you set as the root of the {@link Scene}
     * @param stage The main {@link Stage} which contains the scene
     */
    protected void setNewAndStageXY(Pane pane, Stage stage)
    {
        changeNewX = pane.getWidth();
        changeNewY = pane.getHeight();
        newStageX = stage.getWidth();
        newStageY = stage.getHeight();
    }

    /**
     * @deprecated Used to trigger resize events, for example, set the {@link Stage}'s {@code width} and {@code height} to
     * the default size, then show the scene, after it, set the {@code stage}'s {@code width} and {@code height}
     * to the resized sizes.
     * @param stage The main {@code stage}
     * @param scene The {@link Scene} that belongs to the {@code stage}
     */
    @Deprecated
    protected void setOldAndNewAndStageProps(Stage stage, Scene scene){
        stage.setScene(scene);
        stage.setWidth(oldStageX);
        stage.setHeight(oldStageY);
        stage.show();
        stage.setWidth(newStageX);
        stage.setHeight(newStageY);
    }

    /**
     * Each {@code controller} has its own {@code addResizeListener} method, this is used to reach the {@code controller}'s
     * method from {@link OptionsController}.
     */
    protected void addResizeListener(){}

}
