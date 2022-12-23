package rahulstech.javafx.visualization.javafxvisualization.util;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

@SuppressWarnings("unused")
public class DialogUtil {

    private static final String TAG = DialogUtil.class.getSimpleName();

    private static final long ANIMATION_DURATION = 200;

    private static final int MAX_FRAME_STOPS = 10;

    private static final double BACKDROP_OPACITY = 0.5;

    private static long durationFraction(long max, double percentage) {
        return (long) Math.floor(max *percentage/100);
    }

    private static double opacityFraction(double max, double percentage) {
        return max *percentage/100;
    }

    private static double frameStopPercentage(int current) {
        return (double) 100*current/MAX_FRAME_STOPS;
    }

    private static Region createBackdrop() {
        AnchorPane backdrop = new AnchorPane();
        backdrop.setBackground(getBackdropBackground(0));
        return backdrop;
    }

    private static Animation animateBackdropShow(Region backdrop) {
        Timeline animation = new Timeline();
        for (int stop = 0; stop <= MAX_FRAME_STOPS; stop++) {
            double percentage = frameStopPercentage(stop);
            long duration = durationFraction(ANIMATION_DURATION,percentage);
            double opacity = opacityFraction(BACKDROP_OPACITY,percentage);
            //log(TAG,"animateBackdropShow(): stop="+stop+" percentage="+percentage+" duration="+duration+" opacity="+opacity);
            animation.getKeyFrames().add(
                    new KeyFrame(Duration.millis(duration),
                            new KeyValue(backdrop.backgroundProperty(),
                                    getBackdropBackground(opacity))));
        }
        return animation;
    }

    private static Background getBackdropBackground(double opacity) {
        return new Background(new BackgroundFill(Color.rgb(0,0,0,opacity),CornerRadii.EMPTY,Insets.EMPTY));
    }

    private static Animation animateBackdropHide(Region backdrop) {
        Timeline animation = new Timeline();
        for (int stop = 0; stop <= MAX_FRAME_STOPS; stop++) {
            double percentage = frameStopPercentage(stop);
            long duration = durationFraction(ANIMATION_DURATION,percentage);
            double opacity = opacityFraction(BACKDROP_OPACITY,100-percentage);
            //log(TAG,"animateBackdropHide(): stop="+stop+" percentage="+percentage+" duration="+duration+" opacity="+opacity);
            animation.getKeyFrames().add(
                    new KeyFrame(Duration.millis(duration),
                            new KeyValue(backdrop.backgroundProperty(),
                                    getBackdropBackground(opacity))));
        }
        return animation;
    }

    public static void showAsDialog(Stage window, Node view) {
        if (null == window) throw new NullPointerException("null == window");
        if (null == view) throw new NullPointerException("null == view");

        StackPane root = getSceneRoot(window);

        final Region backdrop = createBackdrop();
        final Animation backdropAnimation = animateBackdropShow(backdrop);
        queue(() -> {
            root.getChildren().add(backdrop);
            root.getChildren().add(view);
            backdropAnimation.play();
        });
    }

    public static void dismissDialog(Stage window, Node view/* add callback on dismiss */) {
        if (null == window) throw new NullPointerException("null == window");
        if (null == view) throw new NullPointerException("null == view");

        StackPane root = getSceneRoot(window);

        int viewIndex = root.getChildren().indexOf(view);
        int backdropIndex = viewIndex-1;

        if (viewIndex > 1) {
            Region backdrop = (Region) root.getChildren().get(backdropIndex);
            Animation backdropAnimation = animateBackdropHide(backdrop);
            backdropAnimation.setOnFinished((e)->
                root.getChildren().remove(backdropIndex)
            );
            queue(() -> {
                root.getChildren().remove(viewIndex);
                backdropAnimation.play();
            });
        }
    }

    private static StackPane getSceneRoot(Stage window) {
        Scene scene = window.getScene();
        return (StackPane) scene.getRoot();
    }

    private static void queue(Runnable command) {
        // runLater(Runnable) ensure show and hide animation run sequentially
        // For example:
        // SHOW Dialog1 : running
        // SHOW Dialog2 : queued
        // HIDE Dialog1 : queued
        Platform.runLater(command);
    }
}
