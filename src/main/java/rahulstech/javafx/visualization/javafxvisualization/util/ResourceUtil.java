package rahulstech.javafx.visualization.javafxvisualization.util;

import rahulstech.javafx.visualization.javafxvisualization.ExpenseTrackerExtensionApp;

import java.net.URL;

@SuppressWarnings("unused")
public class ResourceUtil {

    private static final String VIEW_FOLDER = "view";
    private static final String CSS_FOLDER = "css";

    public static URL view(String name) {
        return ExpenseTrackerExtensionApp.class.getResource(VIEW_FOLDER+"/"+name);
    }

    public static String css(String name) {
        URL url = ExpenseTrackerExtensionApp.class.getResource(CSS_FOLDER + "/" + name);
        if (null == url) return null;
        return url.toExternalForm();
    }
}
