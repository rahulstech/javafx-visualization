package rahulstech.javafx.visualization.javafxvisualization.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import rahulstech.javafx.visualization.javafxvisualization.etapp.model.TransactionType;

import java.io.IOException;

public class TransactionTypeTypeAdapter extends TypeAdapter<TransactionType> {
    @Override
    public void write(JsonWriter out, TransactionType value) throws IOException {}

    @Override
    public TransactionType read(JsonReader in) throws IOException {
        int type = in.nextInt();
        if (1 == type) return TransactionType.INCOME;
        if (0 == type) return TransactionType.EXPENSE;
        return null;
    }
}
