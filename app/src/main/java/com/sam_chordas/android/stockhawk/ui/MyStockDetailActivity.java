package com.sam_chordas.android.stockhawk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockIntentService;
import com.sam_chordas.android.stockhawk.service.StockTaskService;

import java.util.List;

import static com.sam_chordas.android.stockhawk.service.StockTaskService.ACTION_HISTORY;


public class MyStockDetailActivity extends AppCompatActivity {

    private Context mContext;
    boolean isConnected;
    private Intent mServiceIntent;
    List<Entry> mEntries;
    LineChart mChart;
    HistoryReceiver mHistoryReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        mHistoryReceiver = new HistoryReceiver();

        setContentView(R.layout.activity_my_stock_detail);

        mChart = (LineChart) findViewById(R.id.linechart);

        Intent fillintent = getIntent();
        String symbol = fillintent.getStringExtra("symbol");

        TextView tv = (TextView)findViewById(R.id.stock_symbol_graph);
        tv.setText(symbol);

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

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter mHistoryIntentFilter = new IntentFilter(StockTaskService.ACTION_HISTORY);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mHistoryReceiver,
                mHistoryIntentFilter);

    }

    public class HistoryReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            mEntries= bundle.getParcelableArrayList("history");

            LineDataSet lineDataSet = new LineDataSet(mEntries,"graph");

            LineData lineData = new LineData(lineDataSet);
            mChart.setDescription(getString(R.string.stock_history_data));

            XAxis xAxis = mChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(10f);
            xAxis.setTextColor(Color.YELLOW);

            mChart.animateXY(750, 750);
            mChart.setData(lineData);
            mChart.invalidate();


        }
    }

    @Override
    protected void onDestroy() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHistoryReceiver);
        super.onDestroy();
    }
}
