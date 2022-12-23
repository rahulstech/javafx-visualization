package rahulstech.javafx.visualization.javafxvisualization.controller;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("unused")
public abstract class BaseController implements Initializable {

    private Stage window;

    private Scene scene;

    private Node view;

    private URL location;

    private ResourceBundle resources;

    @Override
    public final void initialize(URL location, ResourceBundle resources) {
        this.location = location;
        this.resources = resources;
        onInitializeView(location,resources);
    }

    protected void onInitializeView(URL location, ResourceBundle resources) {}

    protected void onInitializeWindow() {}

    protected void onInitializeScene() {}

    public void setWindow(Stage window) {
        if (null == window) throw new NullPointerException("null == window");
        this.window = window;
        onInitializeWindow();
    }

    public Stage getWindow() {
        return window;
    }

    public void showInScene(Scene scene) {
        if (null == scene) throw new NullPointerException("null == scene");
        this.scene = scene;
        onInitializeScene();
        StackPane root = (StackPane) scene.getRoot();
        root.getChildren().clear();
        root.getChildren().add(getView());
    }

    public Scene getScene() {
        return scene;
    }

    public String getTitle() {
        return window.getTitle();
    }

    public void setTitle(String title) {
        window.setTitle(title);
    }

    public URL getLocation() {
        return location;
    }

    public ResourceBundle getResources() {
        return resources;
    }

    public void setView(Node view) {
        this.view = view;
    }

    @SuppressWarnings("unchecked")
    public <V extends Node> V getView() {
        return (V) view;
    }
}
