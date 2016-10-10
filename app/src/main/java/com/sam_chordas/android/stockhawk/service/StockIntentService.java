package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;

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

  @Override protected void onHandleIntent(final Intent intent) {
    Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
    StockTaskService stockTaskService = new StockTaskService(this);
    Bundle args = new Bundle();
    if (intent.getStringExtra("tag").equals("add")){
      args.putString("symbol", intent.getStringExtra("symbol"));
    }
    try {//Dixit:handling the Invalid Stock Crash
      // We can call OnRunTask from the intent service to force it to run immediately instead of
      // scheduling a task.
      stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
    }
    catch (Exception e){
      //refernce from: http://stackoverflow.com/a/28318124
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
