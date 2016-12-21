package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.linechart.MyLineChart;
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter;
import com.sam_chordas.android.stockhawk.util.Helpers;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by simon on 13-Dec-16.
 */

public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String mSymbol;
    private static final int CURSOR_LOADER_ID = 0;
    private Context mContext;
    private Cursor mCursor;
    private MyLineChart mMyLineChart;

    @BindView(R.id.linechart) LineChartView mLineChartView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        ButterKnife.bind(this);

        mSymbol = getIntent().getStringExtra("symbol");

        //Set title stock symbol
        if(mSymbol != null) setTitle(mSymbol);

        try {
            //mMyLineChart = new MyLineChart(this, mLineChartView, new float[]{}, new String[]{});
            //mMyLineChart.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Last 24 hours
        long nowTime = System.currentTimeMillis();
        String timeRange = String.valueOf((86400000 - nowTime));

        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP,
                        QuoteColumns.CREATED, QuoteColumns.ISCURRENT},
                QuoteColumns.SYMBOL + " = ? AND "+
                QuoteColumns.CREATED + " > ?",
                new String[]{mSymbol, timeRange},null
                /*" created desc limit 24"*/);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        try {
            if (mMyLineChart == null) {
                mMyLineChart = new MyLineChart(this, mLineChartView,
                        Helpers.getFloatValueFromCursor(data, Helpers.STOCK_CHANGE),
                        Helpers.getCreatedFromCursor(data));
                mMyLineChart.show();
            } else {
                mMyLineChart.update(Helpers.getFloatValueFromCursor(data, Helpers.STOCK_CHANGE),
                        Helpers.getCreatedFromCursor(data));
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Data Length:"+data.getCount());
        }
        System.out.println("Data Length:"+data.getCount());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }
}
