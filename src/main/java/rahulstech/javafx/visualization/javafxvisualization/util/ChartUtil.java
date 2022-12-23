package rahulstech.javafx.visualization.javafxvisualization.util;

import javafx.scene.chart.*;
import rahulstech.javafx.visualization.javafxvisualization.adapter.XYChartAdapter;

@SuppressWarnings("unused")
public class ChartUtil {

    public static  XYChart<String,Number>[] createSimpleCompareWithMeanValueChart(XYChartAdapter<String,Number> valuesAdapter,
                                                                            XYChartAdapter<String,Number> meanAdapter,
                                                                            double lowerBound, double upperBound, double tickUnit,
                                                                                  String xAxisLabel, String yAxisLabel) {
        return createCompareWithMeanValueChart(valuesAdapter,meanAdapter,
                createStringAxis(xAxisLabel),createNumberAxis(yAxisLabel,lowerBound,upperBound,tickUnit));
    }

    @SuppressWarnings("unchecked")
    public static <XData,YData> XYChart<XData,YData>[] createCompareWithMeanValueChart(XYChartAdapter<XData,YData> valuesAdapter,
                                                                                       XYChartAdapter<XData,YData> meanAdapter,
                                                                                       Axis<XData> xAxis, Axis<YData> yAxis) {

        BarChart<XData,YData> barChart = createBarChart(valuesAdapter,xAxis,yAxis);
        LineChart<XData,YData> lineChart = createLineChart(meanAdapter,xAxis,yAxis);

        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(true);
        lineChart.setAlternativeRowFillVisible(false);
        lineChart.setAlternativeColumnFillVisible(false);
        lineChart.setHorizontalGridLinesVisible(false);
        lineChart.setVerticalGridLinesVisible(false);
        lineChart.getXAxis().setVisible(false);
        lineChart.getYAxis().setVisible(false);

        return new XYChart[]{barChart,lineChart};
    }

    public static <XData,YData> BarChart<XData,YData> createBarChart(XYChartAdapter<XData,YData> adapter,
                                                                     Axis<XData> xAxis, Axis<YData> yAxis) {
        BarChart<XData,YData> barChart = new BarChart<>(xAxis,yAxis);
        barChart.setData(adapter.getData());
        return barChart;
    }

    public static <XData,YData> LineChart<XData,YData> createLineChart(XYChartAdapter<XData,YData> adapter,
                                                                       Axis<XData> xAxis, Axis<YData> yAxis) {
        LineChart<XData,YData> lineChart = new LineChart<>(xAxis,yAxis);
        lineChart.setData(adapter.getData());
        return lineChart;
    }

    public static Axis<String> createStringAxis(String name) {
        CategoryAxis axis = new CategoryAxis();
        axis.setLabel(name);
        return axis;
    }

    public static Axis<Number> createNumberAxis(String name, double lowerBound, double upperBound, double tickUnit) {
        return createNumberAxis(name,lowerBound,upperBound);
    }

    public static Axis<Number> createNumberAxis(String name, double lowerBound, double upperBound) {
        double tickUnit = (upperBound-lowerBound)*.1;
        return new NumberAxis(name,lowerBound,upperBound,tickUnit);
    }
}
