package com.sam_chordas.android.stockhawk.linechart;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BounceEase;
import com.sam_chordas.android.stockhawk.R;

/**
 * Created by simon on 15-Dec-16.
 */

public class MyLineChart {


    private final LineChartView mChart;


    private final Context mContext;


    private String[] mLabels;// = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            //"11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", ""};

    private float[] mValues;// = {1F, 4F, 0F, -2F, -1F, 1.5F, 5F, 3, 1, 2, -3.5F, -2};

    private Tooltip mTip;


    public MyLineChart(Context context, LineChartView lcv, float[] values, String[] labels) {
        mContext = context;
        mChart = lcv;
        mValues = values;
        mLabels = labels;
    }

    public void show() {

        // Tooltip
        mTip = new Tooltip(mContext, R.layout.linechart_tooltip, R.id.value);

        mTip.setVerticalAlignment(Tooltip.Alignment.BOTTOM_TOP);
        mTip.setDimensions((int) Tools.fromDpToPx(58), (int) Tools.fromDpToPx(25));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            mTip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)).setDuration(200);

            mTip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 0f)).setDuration(200);

            mTip.setPivotX(Tools.fromDpToPx(65) / 2);
            mTip.setPivotY(Tools.fromDpToPx(25));
        }

        mChart.setTooltips(mTip);

        LineSet dataset = new LineSet(mLabels, mValues);
        dataset.setColor(mContext.getResources().getColor(R.color.grey_900))
                .setDotsColor(mContext.getResources().getColor(R.color.blue_500))
                .setThickness(2);
        mChart.addData(dataset);

        int maxValue = (int) getMaxValue(mValues);
        int minValue = (int) getMinValue(mValues);

        maxValue = maxValue + 2;
        minValue = minValue - 2;

        // Chart
        mChart.setBorderSpacing(Tools.fromDpToPx(15))
                .setAxisBorderValues(minValue,maxValue)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(mContext.getResources().getColor(R.color.grey_900))
                .setXAxis(false)
                .setYAxis(false);

        Runnable chartAction = new Runnable() {
            @Override
            public void run() {
                mTip.prepare(mChart.getEntriesArea(0).get(3), mValues[3]);
                mChart.showTooltip(mTip, true);
            }
        };

        Animation anim = new Animation().setEasing(new BounceEase()).setEndAction(chartAction);

        mChart.show(anim);
    }

    public void update(float[] values, String[] labels) {
        this.mValues = values;
        this.mLabels = labels;


        mChart.dismissAllTooltips();

        mChart.updateValues(0, mValues);

        //mChart.getChartAnimation().setEndAction(mBaseAction);
        mChart.notifyDataUpdate();
    }

    public void dismiss(Runnable action) {

        mChart.dismissAllTooltips();
        mChart.dismiss(new Animation().setEasing(new BounceEase()).setEndAction(action));
    }

    /**
     * Get the max value from the array of values
     *
     * @param values floating array
     * @return max the maximum value
     * */
    private float getMaxValue(float[] values){
        if(values.length != 0) {
            float max = values[0];
            for (int i = 1; i < values.length; i++) {
                max = Math.max(max, values[i]);
            }
            return max;
        } else {
            return 0;
        }
    }

    /**
     * Get the min value from the array of values
     *
     * @param values floating array
     * @return min the maximum value
     * */
    private float getMinValue(float[] values){
        if(values.length != 0) {
            float min = values[0];
            for (int i = 1; i < values.length; i++) {
                min = Math.min(min, values[i]);
            }
            return min;
        } else {
            return 0;
        }
    }
}