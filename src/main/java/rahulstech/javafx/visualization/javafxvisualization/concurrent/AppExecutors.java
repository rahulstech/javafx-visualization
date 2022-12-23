package rahulstech.javafx.visualization.javafxvisualization.concurrent;

import javafx.concurrent.Task;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    private static final Object LOCK = new Object();

    private static Executor bgExecutor = null;

    public static Executor getBackgroundExecutor() {
        synchronized (LOCK) {
            if (null == bgExecutor) {
                bgExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            }
            return bgExecutor;
        }
    }

    public static <R> void executeOnBackground(Task<R> task) {
        getBackgroundExecutor().execute(task);
    }
}
