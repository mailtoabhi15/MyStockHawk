package com.sam_chordas.android.stockhawk.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockIntentService;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Implementation of App Widget functionality.
 */
public class StockHawkWidget extends AppWidgetProvider {

//    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
//                                int appWidgetId) {
//
//        CharSequence widgetText = context.getString(R.string.appwidget_text);
//        // Construct the RemoteViews object
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_hawk_app_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
//
//        Intent intent =new Intent(context, MyStocksActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
//        views.setOnClickPendingIntent(R.id.appwidget_text,pendingIntent);
//
//        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);
//    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        //Dixit: for gettign real data, we move the below updateAppWidget() code to Intent service
        //and instead start that service form here.
        Intent serviceIntent = new Intent(context, StockIntentService.class);
        context.startService(serviceIntent);
        // There may be multiple widgets active, so update all of them
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

