package rahulstech.javafx.visualization.javafxvisualization.util;

@SuppressWarnings("unused")
public class LogUtil {

    public static void log(String tag, String message) {
        System.out.println("["+tag+"] "+message);
    }

    public static void logError(String tag, String message, Throwable th) {
        System.err.println("["+tag+"] "+message);
        th.printStackTrace(System.err);
    }
}
