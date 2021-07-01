package com.fernando.proyectofinal.ui.weather;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fernando.proyectofinal.CustomUtil;
import com.fernando.proyectofinal.R;
import com.fernando.proyectofinal.ResourceType;
import com.fernando.proyectofinal.adapters.WeatherAdapter;
import com.fernando.proyectofinal.db.DatabaseManager;
import com.fernando.proyectofinal.entities.Tag;
import com.fernando.proyectofinal.entities.Weather;

import java.util.List;

public class WeatherFragment extends Fragment {

    private static final String TAG = "WeatherFragment";

    private TextView mCurrentTemperature;
    private ImageView mCurrentWeatherIcon;

    private List<Weather> mWeatherData;
    private List<Tag> mTagList;
    private WeatherAdapter mAdapter;
    private DatabaseManager mDatabaseManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        mDatabaseManager = DatabaseManager.getInstance(getActivity());

        displayWeather(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    private void displayWeather(View view) {
        Cursor weatherCursor = mDatabaseManager.findMany(Weather.TABLE_NAME, Weather.ALL_COLUMNS);
        mWeatherData = Weather.manyFromCursor(weatherCursor);

        Cursor tagCursor = mDatabaseManager.findMany(
            Tag.TABLE_NAME,
            Tag.ALL_COLUMNS,
            Tag.COLUMN_CATEGORY + " = ?",
            String.valueOf(ResourceType.WEATHER.getValue()));
        mTagList = Tag.manyFromCursor(tagCursor);

        final RecyclerView weatherRecycler = view.findViewById(R.id.weatherRecycler);
        weatherRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new WeatherAdapter(mWeatherData, mTagList, getContext());
        weatherRecycler.setAdapter(mAdapter);

        mCurrentTemperature = view.findViewById(R.id.currentUsername);
        mCurrentWeatherIcon = view.findViewById(R.id.currentWeatherIcon);

        if (mWeatherData.size() > 0) {
            double averageTemp = (mWeatherData.get(0).getMaxTemp() + mWeatherData.get(0).getMinTemp()) / 2;
            mCurrentTemperature.setText(averageTemp + "°");

            String weatherType = CustomUtil.getTypeString(mTagList, mWeatherData.get(0).getTag()).toLowerCase();
            int icon = 0;
            switch (weatherType) {
                case "lluvioso con sol":
                    icon = R.drawable.ic_lluvia_sol;
                    break;
                case "lluvia":
                    icon = R.drawable.ic_lluvia;
                    break;
                case "tormenta":
                    icon = R.drawable.ic_tormenta;
                    break;
                case "nubes":
                    icon = R.drawable.ic_nubes;
                    break;
                case "nieve":
                    icon = R.drawable.ic_nieve;
                    break;
                default:
                    icon = R.drawable.ic_icon_sunny;
                    break;
            }

            mCurrentWeatherIcon.setImageResource(icon);
        } else {
            mCurrentTemperature.setText("--°");
            mCurrentWeatherIcon.setImageResource(R.drawable.ic_icon_sunny);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case 0:
                Log.i(TAG, String.valueOf(item.getGroupId()));
                mAdapter.editItem(item.getGroupId());
                return true;
            case 1:
                long id = mAdapter.getIdInPos(item.getGroupId());
                mDatabaseManager.deleteEntity(Weather.TABLE_NAME, id);
                mWeatherData.remove(item.getGroupId());
                mAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}