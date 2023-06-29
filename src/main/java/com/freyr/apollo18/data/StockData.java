package com.freyr.apollo18.data;

import com.freyr.apollo18.Apollo18;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StockData {
    private final String API_KEY;
    private final String symbol;

    protected StockData(Apollo18 bot, String symbol) {
        this.API_KEY = bot.getConfig().get("ALPHAVANTAGE", System.getenv("ALPHAVANTAGE"));
        this.symbol = symbol;
    }

    protected String retrieveStockData() {
        try {
            String apiUrl = "https://www.alphavantage.co/query?" + "function=TIME_SERIES_INTRADAY" + "&symbol=" + symbol + "&interval=60min" + "&apikey=" + API_KEY;

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected TimeSeriesCollection parseStockData(String stockData) {
        TimeSeries timeSeries = new TimeSeries("Stock Prices");

        try {
            JSONObject json = new JSONObject(stockData);
            System.out.println(json);
            JSONObject timeSeriesData = json.getJSONObject("Time Series (60min)");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            for (String key : timeSeriesData.keySet()) {
                Date date = dateFormat.parse(key);
                JSONObject dailyData = timeSeriesData.getJSONObject(key);
                double closePrice = dailyData.getDouble("4. close") * 0.23;
                timeSeries.addOrUpdate(new Hour(date), closePrice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(timeSeries);
        return dataset;
    }

    protected void displayLineChart(TimeSeriesCollection dataset) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart("Weekly Stock Prices", // Chart title
                "Date", // X-axis label
                "Price", // Y-axis label
                dataset, // Dataset
                true, // Legend
                true, // Tooltips
                false // URLs
        );

        // Set rendering hints for better color accuracy
        chart.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        chart.getRenderingHints().put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Set line color based on price trend
        for (int series = 0; series < dataset.getSeriesCount(); series++) {
            int itemCount = dataset.getItemCount(series);
            for (int item = 1; item < itemCount; item++) {
                double currentValue = dataset.getYValue(series, item);
                double previousValue = dataset.getYValue(series, item - 1);

                if (currentValue > previousValue) {
                    renderer.setSeriesPaint(series, new Color(Integer.parseInt("006400", 16)));
                } else {
                    renderer.setSeriesPaint(series, new Color(Integer.parseInt("ff0000", 16)));
                }
            }
        }

        plot.setRenderer(renderer);
        chart.setBackgroundPaint(Color.WHITE);

        ChartFrame frame = new ChartFrame("Stock Prices", chart);
        frame.pack();

        // Create a new folder for the ticker if it doesn't exist
        String folderPath = "src/main/resources/stock_data/" + symbol;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdir();
            System.out.println("Created folder: " + folderPath);
        }

        // Save the chart as an image
        try {
            String imagePath = "src/main/resources/stock_data/" + symbol + "/" + symbol + "-graph.png"; // Specify the file path and format (e.g., PNG)
            ChartUtils.saveChartAsPNG(new File(imagePath), chart, frame.getWidth(), frame.getHeight());
            System.out.println("Chart saved as image: " + imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
