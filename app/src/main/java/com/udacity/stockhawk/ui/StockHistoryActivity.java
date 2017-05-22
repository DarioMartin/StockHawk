package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StockHistoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String SYMBOL = "symbol";
    private static final int STOCK_LOADER_BY_SYMBOL = 123;


    private String symbol;
    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_history);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(SYMBOL)) {
            symbol = getIntent().getStringExtra(SYMBOL);
        } else {
            finish();
        }

        setTitle(symbol);

        chart = (LineChart) findViewById(R.id.chart);

        getSupportLoaderManager().initLoader(STOCK_LOADER_BY_SYMBOL, null, this);

        setUpChart(chart);
        setUpXAxis(chart);
        setUpYAxis(chart);

    }

    private void setUpChart(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setDragDecelerationFrictionCoef(0.9f);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);
        chart.animateX(2500);

        Legend l = chart.getLegend();
        l.setEnabled(false);

    }

    private void setUpYAxis(LineChart chart) {
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextSize(10f);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void setUpXAxis(LineChart chart) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                try {
                    DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    Date netDate = (new Date((long) value));
                    return sdf.format(netDate);
                } catch (Exception ex) {
                    return "xx";
                }
            }
        });
    }

    private void fillChart(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setValueTextColor(getResources().getColor(R.color.colorAccent));
        dataSet.setDrawIcons(false);
        dataSet.setColor(Color.WHITE);
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setLineWidth(1f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(9f);
        dataSet.setDrawFilled(true);
        dataSet.setFormLineWidth(1f);
        dataSet.setFormSize(15.f);
        dataSet.setFillColor(getResources().getColor(R.color.colorPrimary));

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == STOCK_LOADER_BY_SYMBOL) {
            return new CursorLoader(this,
                    Contract.Quote.URI,
                    new String[]{Contract.Quote.COLUMN_HISTORY},
                    Contract.Quote.COLUMN_SYMBOL + "=?",
                    new String[]{symbol},
                    null);
        } else {
            throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0) {
            data.moveToPosition(0);
            String historySt = data.getString(0);
            List<Entry> entries = prepareHistory(historySt);
            fillChart(entries);
        }
    }

    private List<Entry> prepareHistory(String historySt) {

        List<Entry> entries = new ArrayList<>();

        historySt = historySt.trim();
        String[] datesValues = historySt.split("\n");

        float i = 0;

        for (String dateValue : datesValues) {
            String[] position = dateValue.split(",");
            entries.add(new Entry(Float.parseFloat(position[0]), Float.parseFloat(position[1])));
        }

        Collections.reverse(entries);

        return entries;
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
