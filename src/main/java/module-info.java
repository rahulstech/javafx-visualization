module rahulstech.javafx.visualization.javafxvisualization {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;

    opens rahulstech.javafx.visualization.javafxvisualization to javafx.fxml;
    opens rahulstech.javafx.visualization.javafxvisualization.controller to javafx.fxml;
    opens rahulstech.javafx.visualization.javafxvisualization.etapp.model to com.google.gson;

    exports rahulstech.javafx.visualization.javafxvisualization;
}