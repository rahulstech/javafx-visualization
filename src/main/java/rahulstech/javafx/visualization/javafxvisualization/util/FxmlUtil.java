package rahulstech.javafx.visualization.javafxvisualization.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.Stage;
import rahulstech.javafx.visualization.javafxvisualization.controller.BaseController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("unused")
public class FxmlUtil {

    @SuppressWarnings("unchecked")
    public static <C extends BaseController> C loadViewWithController(Stage window, URL location) {
        return (C) loadView(window,location,null, true);
    }

    @SuppressWarnings("unchecked")
    public static <V extends Node> V loadView(Stage window, URL location) {
        return (V) loadView(window,location,null,false);
    }

    @SuppressWarnings("unchecked")
    public static <C extends BaseController> C loadViewWithController(Stage window, URL location, ResourceBundle bundle) {
        return (C) loadView(window,location,bundle, true);
    }

    @SuppressWarnings("unchecked")
    public static <V extends Node> V loadView(Stage window, URL location, ResourceBundle bundle) {
        return (V) loadView(window,location,bundle,false);
    }

    private static Object loadView(Stage window, URL location, ResourceBundle bundle, boolean hasController) {
        if (null == location) throw new IllegalArgumentException("location of fxml is empty");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(location);
        if (null != bundle) {
            loader.setResources(bundle);
        }
        Node view = null;
        BaseController controller = null;
        try {
            view  = loader.load();
            controller = loader.getController();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        if (null == view) throw new NullPointerException("null == view for location \""+location+"\"");
        if (!hasController) {
            return view;
        }
        if (null == controller) {
            throw new NullPointerException("null == controller for location \""+location+"\"");
        }
        else {
            controller.setWindow(window);
            controller.setView(view);

            return controller;
        }
    }
}
