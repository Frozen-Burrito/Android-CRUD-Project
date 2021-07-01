package com.fernando.proyectofinal;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class TimerService extends Service {

    private static final String TAG = "TimerService";
    public static final String COUNTDOWN_BR = "com.fernando.proyectofinal.timer_br";

    //300000
    private static final long INITIAL_TIME_MS = 10000;
    public static final String TIME_REMAINING = "TIME_REMAINING";

    Intent mBi = new Intent(COUNTDOWN_BR);

    CountDownTimer mTimer = null;

    @Override
    public void onCreate() {
        super.onCreate();

        startTimer();
    }

    private void startTimer() {
        Log.i(TAG, "Starting timer");
        mTimer = new CountDownTimer(INITIAL_TIME_MS, 1000) {
            @Override
            public void onTick(long millisRemaining) {
                Log.i(TAG, "Countdown seconds remaining: " + millisRemaining / 1000);
                mBi.putExtra(TIME_REMAINING, millisRemaining);
                sendBroadcast(mBi);
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished");
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        mTimer.cancel();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
