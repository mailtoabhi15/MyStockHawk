package com.sam_chordas.android.stockhawk.service;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import static android.R.attr.data;
import static com.google.android.gms.common.api.Status.we;

public class StockDetailWidgetService extends RemoteViewsService {
    public StockDetailWidgetService() {
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockRemoteViewsFactory(this.getBaseContext(), intent);
    }
}

class StockRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private int mAppWidgetId;
    private Cursor mCursor = null;

    public StockRemoteViewsFactory(Context baseContext, Intent intent) {
        mContext = baseContext;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        //Dixit-Imp: In onCreate() you setup any connections/cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
        
    }

    @Override
    public void onDataSetChanged() {

        if (mCursor != null) {
            mCursor.close();
        }
        // This method is called by the app hosting the widget (e.g., the launcher)
        // However, our ContentProvider is not exported so it doesn't have access to the
        // data. Therefore we need to clear (and finally restore) the calling identity so
        // that calls use our process and permission
        final long identityToken = Binder.clearCallingIdentity();

        mCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{ QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);

        String dump = DatabaseUtils.dumpCursorToString(mCursor);
        Binder.restoreCallingIdentity(identityToken);

    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
    }

    //Dixit: below functions:getCount(),getViewTypeCount(),hasStableIds() & getLoadingView(), are
    // used for fetching Meta Data
    @Override
    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
    @Override
    public boolean hasStableIds() {
        return true;
    }
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    //Dixit: below functions:getViewAt & getItemId, are
    // used for fetching View Data
    @Override
    public RemoteViews getViewAt(int position) {

        if (position == AdapterView.INVALID_POSITION || mCursor == null)
            return null;

        RemoteViews mView = null;

        if(mCursor.moveToPosition(position)) {

            String dump = DatabaseUtils.dumpCursorToString(mCursor);

            mView = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item_quote);
            mView.setTextViewText(R.id.stock_symbol, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL)));
            mView.setTextViewText(R.id.bid_price, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));
            mView.setTextViewText(R.id.change, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.CHANGE)));

//            // Next, we set a fill-intent which will be used to fill-in the pending intent template
//            // which is set on the collection view in StockHawkDetailWidget.
            final Intent fillInIntent = new Intent();
//          fillInIntent.setData(QuoteProvider.Quotes.withSymbol(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL))));
            fillInIntent.putExtra("symbol",(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL))));
            mView.setOnClickFillInIntent(R.id.detail_widget_list_item,fillInIntent);

        }

        return mView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
