package com.sam_chordas.android.stockhawk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockIntentService;

import java.util.List;


public class MyStockDetailActivity extends AppCompatActivity {

    private Context mContext;
    boolean isConnected;
    private Intent mServiceIntent;
    List<Entry> mEntries;
    LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;


        setContentView(R.layout.activity_my_stock_detail);
        mChart = (LineChart) findViewById(R.id.linechart);

        Intent fillintent = getIntent();
        String symbol = fillintent.getStringExtra("symbol");


        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // The intent service is for executing immediate pulls from the Yahoo API
        // GCMTaskService can only schedule tasks, they cannot execute immediately
        mServiceIntent = new Intent(this, StockIntentService.class);
        if (savedInstanceState == null){
            // Run the initialize task service so that some stocks appear upon an empty database
            mServiceIntent.putExtra("tag", "history");
            mServiceIntent.putExtra("symbol",symbol);
            if (isConnected){
                startService(mServiceIntent);
            } else{
                Toast.makeText(mContext, getString(R.string.network_toast), Toast.LENGTH_SHORT).show();
            }
        }

    }


    public class HistoryReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            mEntries= bundle.getParcelable("history");

            LineDataSet lineDataSet = new LineDataSet(mEntries,"graph");

            LineData lineData = new LineData(lineDataSet);
            mChart.setData(lineData);
            mChart.invalidate();


        }
    }
}
