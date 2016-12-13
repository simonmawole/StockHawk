package com.sam_chordas.android.stockhawk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;

/**
 * Created by simon on 03-Nov-16.
 */

public class Helpers {

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
}
