package rahulstech.javafx.visualization.javafxvisualization;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import rahulstech.javafx.visualization.javafxvisualization.controller.HomeController;
import rahulstech.javafx.visualization.javafxvisualization.util.FxmlUtil;
import rahulstech.javafx.visualization.javafxvisualization.util.ResourceUtil;

public class ExpenseTrackerExtensionApp extends Application {

    public static final double MIN_WIDTH = 750;

    public static final double MIN_HEIGHT = 600;
    @Override
    public void start(Stage stage) throws Exception {
        stage.setOnCloseRequest(e -> System.exit(0));
        HomeController controller = FxmlUtil.loadViewWithController(stage,
                ResourceUtil.view("home-view.fxml"));
        Scene scene = getScene(stage);
        controller.showInScene(scene);
        stage.show();
    }

    private Scene getScene(Stage window) {
        StackPane root = new StackPane();
        Scene scene = new Scene(root,MIN_WIDTH,MIN_HEIGHT);
        scene.getStylesheets().addAll(
                BootstrapFX.bootstrapFXStylesheet(),
                ResourceUtil.css("style.css")
        );
        window.setScene(scene);
        return scene;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
