package com.exemplo.clock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            mViewHolder.mBattery.setText(String.format("%s%%",level));
        }
    };

    private ViewHolder mViewHolder = new ViewHolder();
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private boolean mTicker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getSupportActionBar() != null) getSupportActionBar().hide();

        this.mViewHolder.mHourMinutes = findViewById(R.id.hour_minute);
        this.mViewHolder.mSeconds = findViewById(R.id.seconds);
        this.mViewHolder.mBattery = findViewById(R.id.battery_level);

        this.mViewHolder.mHourMinutes.setOnClickListener(V -> showSystemUI());
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(this.mReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        this.mTicker = true;
        this.startClock();

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mTicker = false;
        this.unregisterReceiver(this.mReceiver);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
    }

    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void startClock() {
        final Calendar calendar = Calendar.getInstance();

        this.mRunnable = new Runnable() {
            @Override
            public void run() {

                if(!mTicker) return;

                calendar.setTimeInMillis(System.currentTimeMillis());

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);

                mViewHolder.mHourMinutes.setText(String.format(Locale.getDefault(),"%02d:%02d",hour,minutes));
                mViewHolder.mSeconds.setText(String.format(Locale.getDefault(),"%02d",second));

                long now = SystemClock.elapsedRealtime();
                long next = now + (1000 - (now % 1000));
                mHandler.postAtTime(mRunnable,next);
            }
        };
        this.mRunnable.run();
    }

    public static class ViewHolder {
        TextView mHourMinutes;
        TextView mSeconds;
        TextView mBattery;
    }
}