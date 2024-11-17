package com.javadata.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;

public class ChartUtils {

    public static ChartPanel createChart(String chartTitle, String xLabel, String yLabel) {
        JFreeChart chart = ChartFactory.createBarChart(
                chartTitle, 
                xLabel, 
                yLabel, 
                null, // Dataset will be added later
                PlotOrientation.VERTICAL, 
                true, true, false
        );
        return new ChartPanel(chart);
    }
}
