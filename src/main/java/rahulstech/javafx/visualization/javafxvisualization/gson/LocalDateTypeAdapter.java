package rahulstech.javafx.visualization.javafxvisualization.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd",Locale.ENGLISH);
    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {}

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        String dateString = in.nextString();
        return LocalDate.parse(dateString,DATE_TIME_FORMATTER);
    }
}
