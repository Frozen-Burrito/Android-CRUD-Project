package com.fernando.proyectofinal.ui.home;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fernando.proyectofinal.GardenDetailsActivity;
import com.fernando.proyectofinal.R;
import com.fernando.proyectofinal.db.DatabaseManager;
import com.fernando.proyectofinal.entities.Garden;
import com.fernando.proyectofinal.entities.Location;
import com.fernando.proyectofinal.entities.Tag;

import java.time.LocalDate;
import java.util.List;


public class GardenDetails extends Fragment {

    private Garden mGarden = new Garden();
    private Tag mTag;
    private Location mLocation;

    private TextView mNameText;
    private TextView mAddressText;
    private TextView mTypeText;
    private TextView mLocationText;

    public GardenDetails() {}

    public static GardenDetails newInstance(long gardenId) {
        GardenDetails fragment = new GardenDetails();
        Bundle args = new Bundle();
        args.putLong(GardenDetailsActivity.GARDEN_ID, gardenId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mGardenId = getArguments().getLong(GardenDetailsActivity.GARDEN_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_garden_details, container, false);

        Cursor gardenCursor = GardenDetailsActivity.sDatabaseManager.findById(Garden.TABLE_NAME, GardenDetailsActivity.sGardenId);
        mGarden = Garden.fromCursor(gardenCursor);
        if (mGarden == null) return view;

        Cursor tagCursor = GardenDetailsActivity.sDatabaseManager.findById(Tag.TABLE_NAME, mGarden.getTag());
        Cursor locationCursor = GardenDetailsActivity.sDatabaseManager.findById(Location.TABLE_NAME, mGarden.getLocation());

        mTag = Tag.fromCursor(tagCursor);
        mLocation = Location.fromCursor(locationCursor);

        GardenDetailsActivity.getGardenInventory();

        mNameText = view.findViewById(R.id.gardenNameText);
        mAddressText = view.findViewById(R.id.gardenAddressText);
        mTypeText = view.findViewById(R.id.gardenTypeText);
        mLocationText = view.findViewById(R.id.gardenLocationText);

        mNameText.setText(mGarden.getName());
        mAddressText.setText(mGarden.getAddress());

        if (mTag != null) {
            mTypeText.setText(mTag.getName());
        }

        if (mLocation != null) {
            mLocationText.setText(mLocation.toString());
        }

        return view;
    }
}