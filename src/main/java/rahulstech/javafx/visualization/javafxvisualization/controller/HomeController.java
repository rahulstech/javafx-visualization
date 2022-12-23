package rahulstech.javafx.visualization.javafxvisualization.controller;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import rahulstech.javafx.visualization.javafxvisualization.concurrent.AppExecutors;
import rahulstech.javafx.visualization.javafxvisualization.etapp.ExpenseTrackerDataProcessor;
import rahulstech.javafx.visualization.javafxvisualization.etapp.model.ExpenseTrackerJsonBackup;
import rahulstech.javafx.visualization.javafxvisualization.gson.GsonUtil;
import rahulstech.javafx.visualization.javafxvisualization.util.FxmlUtil;
import rahulstech.javafx.visualization.javafxvisualization.util.ResourceUtil;

import java.io.File;

import static rahulstech.javafx.visualization.javafxvisualization.util.LogUtil.log;
import static rahulstech.javafx.visualization.javafxvisualization.util.LogUtil.logError;

@SuppressWarnings("unused")
public class HomeController extends BaseController {

    private static final String TAG = HomeController.class.getSimpleName();

    public ToolBar toolbar;

    public StackPane contentPanel;

    private File mLastDir = getDefaultDirectory();

    private JsonBackupDataParseTask oldParseTask = null;

    @Override
    protected void onInitializeWindow() {
        super.onInitializeWindow();
        setTitle("Expense Tracker Extension");
    }

    @Override
    protected void onInitializeScene() {
        super.onInitializeScene();
    }

    public void onUploadBackupFile() {
        pickBackupJsonFile();
    }

    private File getDefaultDirectory() {
        try {
            String userHome = System.getProperty("user.home");
            File dir = new File(userHome);
            if (!dir.exists() || !dir.canRead()) {
                return null;
            }
            return dir;
        }
        catch (Exception ex) {
            return null;
        }
    }

    private void pickBackupJsonFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Backup File");
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Json","*.json"));
        chooser.setInitialDirectory(mLastDir);
        File backupFile = chooser.showOpenDialog(getWindow());
        onParseBackupFile(backupFile);
    }

    private void onParseBackupFile(File backupFile) {
        if (null == backupFile) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("No backup file selected");
            alert.show();
            return;
        }
        mLastDir = backupFile.getParentFile();
        if (null != oldParseTask) {
            oldParseTask.cancel(true);
        }
        oldParseTask = new JsonBackupDataParseTask(backupFile);
        AppExecutors.executeOnBackground(oldParseTask);
    }

    private void onBackDataParsingComplete(ExpenseTrackerDataProcessor data) {
        if (null == data){
            return;
        }
        ChartController controller = FxmlUtil.loadViewWithController(getWindow(),ResourceUtil.view("chart-view.fxml"));
        controller.setDataProcessor(data);
        contentPanel.getChildren().clear();
        contentPanel.getChildren().add(controller.getView());
    }

    private class JsonBackupDataParseTask extends Task<ExpenseTrackerDataProcessor> {

        private static final String TAG = JsonBackupDataParseTask.class.getSimpleName();

        private final File backupFile;

        public JsonBackupDataParseTask(File backupFile) {
            if (null == backupFile) throw new NullPointerException("backup file == null");
            this.backupFile = backupFile;
        }

        @Override
        protected ExpenseTrackerDataProcessor call() throws Exception {
            ExpenseTrackerJsonBackup data = GsonUtil.parse(backupFile,ExpenseTrackerJsonBackup.class);
            log(TAG,"file: \""+backupFile+"\" parse complete and hasData="+(data != null));
            if (null == data) return null;
            return new ExpenseTrackerDataProcessor(backupFile,data);
        }

        @Override
        protected void failed() {
            super.failed();
            logError(TAG,"backup file \""+backupFile+"\" failed",getException());
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            log(TAG,"task successful");
            onBackDataParsingComplete(oldParseTask.getValue());
        }
    }
}
