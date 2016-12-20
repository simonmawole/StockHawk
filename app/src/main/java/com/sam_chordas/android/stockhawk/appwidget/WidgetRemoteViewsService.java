package com.sam_chordas.android.stockhawk.appwidget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

/**
 * Created by simon on 13-Dec-16.
 */

/**
 * RemoteViewsService controlling the data being shown in the scrollable weather detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetRemoteViewsService extends RemoteViewsService {
    private static final String[] QUOTES_COLUMN = {
            QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.CHANGE,
            QuoteColumns.ISUP,
            QuoteColumns.CREATED,
            QuoteColumns.ISCURRENT
    };
    // these indices must match the projection
    static final int INDEX_ID = 0;
    static final int INDEX_SYMBOL = 1;
    static final int INDEX_BIDPRICE = 2;
    static final int INDEX_PERCENT_CHANGE = 3;
    static final int INDEX_CHANGE = 4;
    static final int INDEX_ISUP = 5;
    static final int INDEX_CREATED = 6;
    static final int INDEX_ISCURRENT = 7;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        QUOTES_COLUMN,
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_list_item);

                String symbol = data.getString(INDEX_SYMBOL);
                String bid_price = data.getString(INDEX_BIDPRICE);
                String change = data.getString(INDEX_CHANGE);
                String percent_change = data.getString(INDEX_PERCENT_CHANGE);
                String is_current = data.getString(INDEX_ISCURRENT);
                String created = data.getString(INDEX_CREATED);
                String is_up = data.getString(INDEX_ISUP);

                if (data.getInt(INDEX_ISUP) == 1){
                    views.setTextColor(R.id.tvChange, getResources().getColor(R.color.green_700));
                } else{
                    views.setTextColor(R.id.tvChange, getResources().getColor(R.color.red_700));
                }
                if (Utils.showPercent){
                    views.setTextViewText(R.id.tvChange, percent_change);
                } else{
                    views.setTextViewText(R.id.tvChange, change);
                }
                views.setTextViewText(R.id.tvSymbol, symbol);
                views.setTextViewText(R.id.tvBidPrice, bid_price);

                final Intent intent = new Intent();
                //Uri quoteUri = QuoteProvider.Quotes.withSymbol(symbol);
                //intent.setData(quoteUri);
                views.setOnClickFillInIntent(R.id.fl_list_item, intent.putExtra("symbol", symbol));
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.stock_symbol, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
