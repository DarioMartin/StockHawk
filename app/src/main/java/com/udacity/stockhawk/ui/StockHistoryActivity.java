package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class StockHistoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String SYMBOL = "symbol";
    private static final int STOCK_LOADER_BY_SYMBOL = 123;


    private String symbol;
    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_history);

        symbol = getIntent().getExtras().getString(SYMBOL);

        Toast.makeText(this, symbol, Toast.LENGTH_SHORT).show();

        chart = (LineChart) findViewById(R.id.chart);


        getSupportLoaderManager().initLoader(STOCK_LOADER_BY_SYMBOL, null, this);

    }

    private void fillChart(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setColor(getResources().getColor(R.color.colorAccent));
        dataSet.setValueTextColor(getResources().getColor(R.color.colorPrimary));

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
            entries.add(new Entry(i++, Float.parseFloat(position[1])));
        }

        return entries;
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
