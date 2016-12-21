package com.sam_chordas.android.stockhawk.util;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sam_chordas.android.stockhawk.data.QuoteColumns;

import java.text.SimpleDateFormat;

/**
 * Created by simon on 03-Nov-16.
 */

public class Helpers {

    public static final int STOCK_PERCENT_CHANGE = 0;
    public static final int STOCK_CHANGE = 1;

    /**
     * Check to see if there is network connection
     *
     * @param c activity context
     * @return boolean
     * */
    public static boolean isNetworkConnected(Context c){
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Convert date from milliseconds to d MMM yyyy HH:mm
     *
     * @param millisec time in milliseconds
     * @return date in a simple format
     * */
    public static String getSimpleDateTime(long millisec){
        String formatedDate = "";
        if(millisec!= 0) {
            SimpleDateFormat userFormat = new SimpleDateFormat("d MMM yyyy HH:mm");
            formatedDate = userFormat.format(millisec);
        }

        return formatedDate;
    }

    /**
     * Extract stock percent_change or change from cursor data
     *
     * @param cursor Cursor
     * @param valueType int return either percent_change value or change
     * @return float[] values in float array type
     * */
    public static float[] getFloatValueFromCursor(Cursor cursor, int valueType){
        float[] data = new float[cursor.getCount()];
        if(cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                if (valueType == STOCK_PERCENT_CHANGE) {
                    data[i] = Float.valueOf(//Change string to float value
                            cursor.getString(cursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));
                    System.out.println(cursor.getString(cursor.getColumnIndex(QuoteColumns._ID))
                            +"=p"+cursor.getString(cursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));
                }
                if (valueType == STOCK_CHANGE) {
                    data[i] = Float.valueOf(//Change string to float value
                            cursor.getString(cursor.getColumnIndex(QuoteColumns.CHANGE)));
                    System.out.println(cursor.getString(cursor.getColumnIndex(QuoteColumns._ID))
                        +"=c"+cursor.getString(cursor.getColumnIndex(QuoteColumns.CHANGE)));
                }
            }
        }

        return data;
    }

    /**
     * Extract time from the cursor
     *
     * @param cursor the cursor data
     * @return string[]
     * */
    public static String[] getCreatedFromCursor(Cursor cursor){
        String[] data = new String[cursor.getCount()];
        if(cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                data[i] = cursor.getString(cursor.getColumnIndex(QuoteColumns.CREATED));
            }
        }

        return data;
    }

}
