package com.fernando.proyectofinal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fernando.proyectofinal.db.DatabaseManager;
import com.fernando.proyectofinal.entities.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    public static final String SELECTED_TAB = "SELECTED_TAB";
    public static final int PERSON_TAB = R.id.peopleFragment;
    public static final int HOME_TAB = R.id.homeFragment;
    public static final int WEATHER_TAB = R.id.weatherFragment;
    public static final int LOGS_TAB = R.id.logsFragment;

    private Menu contextMenu;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable actionBarColor = new ColorDrawable(getColor(R.color.green));
        actionBar.setBackgroundDrawable(actionBarColor);

        Log.i(TAG, "Started timer service");

        int defaultTab = getIntent().getIntExtra(SELECTED_TAB, HOME_TAB);

        mUser = CustomUtil.getUserFromPrefs(this);
        DatabaseManager.setUserId(mUser.getId());

        // region Navigation
        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
            PERSON_TAB,
            HOME_TAB,
            WEATHER_TAB,
            LOGS_TAB).build();

        NavController navController = Navigation.findNavController(this, R.id.host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // endregion
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, CreateActivity.class);
            ResourceType resource = ResourceType.GARDEN;

            switch (navView.getSelectedItemId()) {
                case R.id.peopleFragment:
                    resource = ResourceType.PERSON;
                    break;
                case R.id.homeFragment:
                    resource = ResourceType.GARDEN;
                    break;
                case R.id.weatherFragment:
                    resource = ResourceType.WEATHER;
                    break;
            }

            i.putExtra(CreateActivity.ACTION, ActionType.CREATE);
            i.putExtra(CreateActivity.RESOURCE, resource);
            startActivity(i);
        });

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                fab.setVisibility(destination.getLabel().equals("Actividad") ? View.INVISIBLE : View.VISIBLE);
                Log.i(TAG, "onDestinationChanged: " + destination.getLabel());
            }
        });

        if (defaultTab != HOME_TAB) navView.setSelectedItemId(defaultTab);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateOptions();
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, TimerService.class));
        Log.i(TAG, "Stopped timer service");
        super.onDestroy();
    }

    private void updateOptions() {
        mUser = CustomUtil.getUserFromPrefs(this);
        boolean userAuth = !mUser.getUsername().equals(User.DEFAULT_USERNAME) && mUser.getLevel() > -1;
        if (contextMenu != null) {
            showOption(userAuth ? R.id.menuLogout : R.id.menuLogin);
            hideOption(userAuth ? R.id.menuLogin : R.id.menuLogout);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.contextMenu = menu;
        getMenuInflater().inflate(R.menu.options_menu, menu);
        updateOptions();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menuLogin:
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.putExtra(LoginActivity.AUTH_ACTION, LoginActivity.LOGIN);
                startActivity(i);
                return true;
            case R.id.menuLogout:
                CustomUtil.logoutUser(this);
                Intent logoutIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(logoutIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void hideOption(int id)
    {
        MenuItem item = contextMenu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id)
    {
        MenuItem item = contextMenu.findItem(id);
        item.setVisible(true);
    }
}