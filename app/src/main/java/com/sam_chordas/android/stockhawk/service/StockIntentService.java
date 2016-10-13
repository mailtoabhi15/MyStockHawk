package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;
import com.sam_chordas.android.stockhawk.ui.StockHawkAppWidget;

import static android.R.id.input;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

  public StockIntentService(){
    super(StockIntentService.class.getName());
  }

  public StockIntentService(String name) {
    super(name);
  }

  void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                              int[] appWidgetIds) {

    Cursor c =context.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
            new String[] {QuoteColumns.SYMBOL }, null,
            null,null);
    if (c == null )
      return;
    String dump = DatabaseUtils.dumpCursorToString(c);
    String widgetText = null;
    if(!c.moveToFirst())
    {
      c.close();
      return;
    }

    widgetText = c.getString(c.getColumnIndex(("symbol")));

    for (int appWidgetId : appWidgetIds) {
      // Construct the RemoteViews object
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_hawk_app_widget);
      views.setTextViewText(R.id.appwidget_text, widgetText);

      Intent intent = new Intent(context, MyStocksActivity.class);
      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
      views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

      // Instruct the widget manager to update the widget
      appWidgetManager.updateAppWidget(appWidgetId, views);
    }
  }


  @Override protected void onHandleIntent(final Intent intent) {

    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, StockHawkAppWidget.class));
    updateAppWidget(this, appWidgetManager, appWidgetIds);


    Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
    StockTaskService stockTaskService = new StockTaskService(this);
    Bundle args = new Bundle();
    if(intent != null && (intent.getStringExtra("tag") != null))
    {
      if (intent.getStringExtra("tag").equals("add")){
        args.putString("symbol", intent.getStringExtra("symbol"));
      }
    }
    else
      return;

    try {//Dixit:handling the Invalid Stock Crash
      // We can call OnRunTask from the intent service to force it to run immediately instead of
      // scheduling a task.
      stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
    }
    catch (Exception e){
      //DIXIT-IMP:refernce from: http://stackoverflow.com/a/28318124
      Handler handler=new Handler(Looper.getMainLooper());
      handler.post(new Runnable() {
        @Override
        public void run() {
          Toast.makeText(getApplicationContext(),
                  intent.getStringExtra("symbol") + " " + getString(R.string.invalid_stock),
                  Toast.LENGTH_LONG).show();
        }

      });
    }

  }


}
