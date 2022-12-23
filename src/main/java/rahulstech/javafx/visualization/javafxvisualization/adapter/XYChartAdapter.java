package rahulstech.javafx.visualization.javafxvisualization.adapter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class XYChartAdapter<XData,YData> {

    private static final String TAG = XYChartAdapter.class.getSimpleName();

    public static final int DEFAULT_SERIES_COUNT = 1;

    public static final int DEFAULT_SERIES = 1;

    public int getSeriesCount() {
        return DEFAULT_SERIES_COUNT;
    }

    public int getSeries(int position) {
        return DEFAULT_SERIES;
    }

    public abstract int getDataCount(int series);

    public abstract XData getXData(int series, int data);

    public abstract YData getYData(int series, int data);

    protected XYChart.Data<XData,YData> onCreateXYData(int series, int data) {
        return new XYChart.Data<>(getXData(series,data),getYData(series,data));
    }

    protected XYChart.Series<XData,YData> onCreateXYSeries(int series, ObservableList<XYChart.Data<XData,YData>> data) {
        return new XYChart.Series<>(data);
    }

    private XYChart.Series<XData,YData> createSeries(int series) {
        ArrayList<XYChart.Data<XData,YData>> data = new ArrayList<>();
        int count = getDataCount(series);
        for (int d = 0; d < count; d++) {
            data.add(onCreateXYData(series,d));
        }
        return onCreateXYSeries(series, FXCollections.observableList(data));
    }

    public final List<XYChart.Series<XData,YData>> build() {
        ArrayList<XYChart.Series<XData,YData>> series = new ArrayList<>();
        int count = getSeriesCount();
        for (int s = 0; s < count; s++) {
            series.add(createSeries(getSeries(s)));
        }
        return series;
    }

    public final ObservableList<XYChart.Series<XData,YData>> getData() {
        List<XYChart.Series<XData,YData>> series = build();
        return FXCollections.observableList(series);
    }
}
