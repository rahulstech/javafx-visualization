package rahulstech.javafx.visualization.javafxvisualization.adapter;

import java.util.*;

@SuppressWarnings("unused")
public class ListXYChartAdapter<XData,YData> extends XYChartAdapter<XData,YData> {

    public abstract static class Entry<XData,YData> {
        private final int series;
        private final List<XData> xData;
        private final List<YData> yData;

        public Entry(int series, List<XData> xData, List<YData> yData) {
            this.series = series;
            this.xData = xData == null ? Collections.emptyList() : xData;
            this.yData = yData == null ? Collections.emptyList() : yData;
        }

        public Entry(List<XData> xData, List<YData> yData) {
            this(XYChartAdapter.DEFAULT_SERIES,xData,yData);
        }

        public List<XData> getAllXData() {
            return xData;
        }

        public List<YData> getAllYData() {
            return yData;
        }

        public abstract XData getXData(int position);

        public abstract YData getYData(int position);

        public abstract int size();
    }

    public static class SimpleEntry<XData,YData> extends Entry<XData,YData> {

        public SimpleEntry(int series, List<XData> xData, List<YData> yData) {
            super(series, xData, yData);
            ensureEqualSize();
        }

        public SimpleEntry(List<XData> xData, List<YData> yData) {
            super(xData, yData);
            ensureEqualSize();
        }

        @Override
        public XData getXData(int position) {
            return getAllXData().get(position);
        }

        @Override
        public YData getYData(int position) {
            return getAllYData().get(position);
        }

        @Override
        public int size() {
            return getAllXData().size();
        }

        private void ensureEqualSize() {
            if (getAllXData().size() != getAllYData().size()) {
                throw new IllegalArgumentException("xData.size() != yData.size()");
            }
        }
    }

    private final HashMap<Integer,Entry<XData,YData>> mapSeriesEntry = new HashMap<>();

    public ListXYChartAdapter() {}

    @SafeVarargs
    public ListXYChartAdapter(Entry<XData,YData>... entries) {
        setEntries(Arrays.asList(entries));
    }

    public void setEntries(List<Entry<XData,YData>> entries) {
        mapSeriesEntry.clear();
        if (null != entries) {
            for (Entry<XData,YData> e : entries) {
                mapSeriesEntry.put(e.series,e);
            }
        }
    }

    public List<Entry<XData,YData>> getEntries() {
        return new ArrayList<>(mapSeriesEntry.values());
    }

    public Entry<XData,YData> getEntry(int series) {
        Entry<XData,YData> entry = mapSeriesEntry.get(series);
        if (null == entry) {
            throw new IllegalArgumentException("series="+series+" does not exists");
        }
        return entry;
    }

    @Override
    public int getSeriesCount() {
        return mapSeriesEntry.size();
    }

    @Override
    public int getDataCount(int series) {
        return mapSeriesEntry.get(series).size();
    }

    @Override
    public XData getXData(int series, int data) {
        Entry<XData,YData> entry = getEntry(series);
        return entry.getXData(data);
    }

    @Override
    public YData getYData(int series, int data) {
        Entry<XData,YData> entry = getEntry(series);
        return entry.getYData(data);
    }
}
