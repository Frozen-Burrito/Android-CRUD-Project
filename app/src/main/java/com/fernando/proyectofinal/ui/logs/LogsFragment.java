package com.fernando.proyectofinal.ui.logs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fernando.proyectofinal.CustomUtil;
import com.fernando.proyectofinal.MainActivity;
import com.fernando.proyectofinal.R;
import com.fernando.proyectofinal.TimerService;
import com.fernando.proyectofinal.adapters.LogsAdapter;
import com.fernando.proyectofinal.db.DatabaseManager;
import com.fernando.proyectofinal.entities.Action;
import com.fernando.proyectofinal.entities.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LogsFragment extends Fragment {

    private static final String TAG = "LogFragment";

    private static final long INITIAL_TIME_MS = 300000;
    private static final String TIME_REMAINING = "TIME_REMAINING";
    private static final String END_TIME = "END_TIME";
    private static final String VIEW_KEY = "VIEW_AVAILABLE";

    // Views
    private TextView mCurrentUserText;
    private TextView mCountdownText;
    private TextView mCooldownText;

    // Timer
    private CountDownTimer mTimer;
    private boolean mIsViewAvailable = true;
    private long mTimeRemaining = INITIAL_TIME_MS;
    private long mTimeCompleted;

    // Data
    private User mUser = new User();
    private List<Action> mActivityLogData = new ArrayList<>();
    private HashMap<Long, String> mUserMap = new HashMap<Long, String>();
    private LogsAdapter mAdapter;
    private DatabaseManager mDatabaseManager;
    private RecyclerView mActionsRecycler;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs, container, false);
        mDatabaseManager = DatabaseManager.getInstance(getActivity());

        if (getActivity() != null) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences(User.USER_PREFS_KEY, Context.MODE_PRIVATE);
            mTimeRemaining = sharedPref.getLong(TIME_REMAINING, INITIAL_TIME_MS);
            mTimeCompleted = sharedPref.getLong(END_TIME, 0);
            mIsViewAvailable = sharedPref.getBoolean(VIEW_KEY, true);
        }

        bindViews(view);
        displayRecentActions();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(br, new IntentFilter(TimerService.COUNTDOWN_BR));
        Log.i(TAG, "Registered broacast receiver");
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "Fragmento en pausa");

        if (mIsViewAvailable) mTimeRemaining = INITIAL_TIME_MS;
        if (mUser.getLevel() == User.USER_NORMAL) mIsViewAvailable = false;

        if (getActivity() != null) {
            getActivity().startService(new Intent(getActivity(), TimerService.class));
            getActivity().unregisterReceiver(br);

            SharedPreferences sharedPref = getActivity().getSharedPreferences(User.USER_PREFS_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putLong(TIME_REMAINING, mTimeRemaining);
            editor.putLong(END_TIME, mTimeCompleted);
            editor.putBoolean(VIEW_KEY, mIsViewAvailable);
            editor.apply();
        }

        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    private void bindViews(View view) {
        mCurrentUserText = view.findViewById(R.id.currentUsername);
        mCountdownText = view.findViewById(R.id.timerCountdownText);
        mCooldownText = view.findViewById(R.id.cooldownText);
        mActionsRecycler = view.findViewById(R.id.logsRecycler);
    }

    private void displayRecentActions() {
        if (getActivity() == null) return;

        mUser = CustomUtil.getUserFromPrefs(getActivity());

        mActionsRecycler.setVisibility(View.INVISIBLE);
        mCurrentUserText.setText(mUser.getUsername());

        boolean userAuth = !mUser.getUsername().equals(User.DEFAULT_USERNAME) && mUser.getLevel() > -1;

        if (userAuth) {
            Cursor actionCursor = mDatabaseManager.findMany(Action.TABLE_NAME, Action.ALL_COLUMNS);
            mActivityLogData = Action.manyFromCursor(actionCursor);
            Collections.reverse(mActivityLogData);

            Cursor userCursor = mDatabaseManager.findMany(User.TABLE_NAME, User.ALL_COLUMNS);
            mUserMap = CustomUtil.getUsernameHashMap(User.manyFromCursor(userCursor));

            mActionsRecycler.setVisibility(View.VISIBLE);
            mCountdownText.setText(getString(R.string.emptyPlaceholder));

            if (mUser.getLevel() == User.USER_NORMAL) {

                mActionsRecycler.setVisibility(mIsViewAvailable ? View.VISIBLE : View.INVISIBLE);
                mCountdownText.setVisibility(mIsViewAvailable ? View.VISIBLE : View.INVISIBLE);
                mCooldownText.setVisibility(mIsViewAvailable ? View.INVISIBLE : View.VISIBLE);
                startTimer();
            }

        } else {
            mCountdownText.setVisibility(View.INVISIBLE);
            mCooldownText.setText(getString(R.string.view_restricted));
            mCooldownText.setVisibility(View.VISIBLE);
        }

        mActionsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new LogsAdapter(mActivityLogData, mUserMap, getContext());
        mActionsRecycler.setAdapter(mAdapter);
    }

    private void startTimer() {
        mTimeCompleted = System.currentTimeMillis() + mTimeRemaining;

        mTimer = new CountDownTimer(mTimeRemaining, 1000) {
            @Override
            public void onTick(long millisRemaining) {
                mTimeRemaining = millisRemaining;
                updateTimerText(mTimeRemaining);
            }

            @Override
            public void onFinish() {
                completeTimer();
            }
        }.start();
    }


    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Tick");
            if (intent.getExtras() != null) {
                long millisUntilFinished = intent.getLongExtra(TIME_REMAINING, 0);
                updateTimerText(millisUntilFinished);
            }
        }
    };

    @Override
    public void onStop() {
        try {
            getActivity().unregisterReceiver(br);
        } catch (Exception e) {
            Log.i(TAG, "Recevier already unregistered");
        }
        super.onStop();
    }

    public void updateTimerText(long timeRemaining) {
        int minutes = (int) (timeRemaining / 1000) / 60;
        int seconds = (int) (timeRemaining / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        if (getContext() != null) {
            mCooldownText.setText(getString(R.string.view_cooldown) + " " + timeLeftFormatted);
            mCountdownText.setText(timeLeftFormatted);
        }
    }

    private void completeTimer() {
        mTimeRemaining = INITIAL_TIME_MS;
        mIsViewAvailable = !mIsViewAvailable;
        updateTimerText(mTimeRemaining);

        displayRecentActions();
    }
}