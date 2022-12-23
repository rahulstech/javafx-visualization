package rahulstech.javafx.visualization.javafxvisualization.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import rahulstech.javafx.visualization.javafxvisualization.etapp.model.TransactionType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;

@SuppressWarnings("unused")
public class GsonUtil {

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class,new LocalDateTypeAdapter())
                .registerTypeAdapter(TransactionType.class,new TransactionTypeTypeAdapter())
                .create();
    }

    public static <T> T parse(File file, Class<T> klass) {
        Gson gson = getGson();
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader,klass);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
