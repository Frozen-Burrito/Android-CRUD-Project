package com.fernando.proyectofinal.ui.forms;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fernando.proyectofinal.ActionType;
import com.fernando.proyectofinal.CreateActivity;
import com.fernando.proyectofinal.CustomUtil;
import com.fernando.proyectofinal.MainActivity;
import com.fernando.proyectofinal.R;
import com.fernando.proyectofinal.ResourceType;
import com.fernando.proyectofinal.db.DatabaseManager;
import com.fernando.proyectofinal.db.DbHelper;
import com.fernando.proyectofinal.entities.Location;
import com.fernando.proyectofinal.entities.Tag;
import com.fernando.proyectofinal.entities.Weather;
import com.fernando.proyectofinal.ui.dialogs.AlertDialogResult;
import com.fernando.proyectofinal.ui.dialogs.LocationDialogFragment;
import com.fernando.proyectofinal.ui.dialogs.TagDialogFragment;

import java.util.Calendar;
import java.util.List;

import static android.content.ContentValues.TAG;

public class WeatherForm extends Fragment implements AlertDialogResult<ActionType>, DatePickerDialog.OnDateSetListener {

    private Weather mWeather = new Weather();
    private List<Tag> mTagList;
    private List<Location> mLocationList;

    private ActionType mFormAction;
    private DatabaseManager mDatabaseManager;

    // Views
    private TextView mDateText;
    private EditText mEditMaxTemp;
    private EditText mEditMinTemp;
    private EditText mEditHumidity;
    private EditText mEditRainProb;

    private ImageButton mBtnEditTag;
    private ImageButton mBtnClearTag;
    private ImageButton mBtnEditLocation;
    private ImageButton mBtnClearLocation;

    // Spinners
    private Spinner mLocationSpinner;
    private ArrayAdapter<Location> mLocationArrayAdapter;
    private Spinner mTagSpinner;
    private ArrayAdapter<Tag> mTagArrayAdapter;

    public WeatherForm() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_form, container, false);
        mDatabaseManager = DatabaseManager.getInstance(getActivity());

        Bundle bundle = getArguments();
        mFormAction = (ActionType) bundle.getSerializable(CreateActivity.ACTION);
        long id = bundle.getLong(DbHelper._ID);

        bindViews(view, id);

        // region Tag Actions
        ImageButton btnAddTag = view.findViewById(R.id.btnAddTag);
        btnAddTag.setOnClickListener(root -> {
            new TagDialogFragment(ActionType.CREATE, ResourceType.WEATHER, 0, mDatabaseManager, this).show(getChildFragmentManager(), TagDialogFragment.TAG);
        });

        mBtnEditTag.setOnClickListener(root -> {
            long tagId = mTagList.get(mTagSpinner.getSelectedItemPosition()).getId();
            new TagDialogFragment(ActionType.EDIT, ResourceType.WEATHER, tagId, mDatabaseManager, this).show(getChildFragmentManager(), TagDialogFragment.TAG);
        });

        mBtnClearTag.setOnClickListener(root -> {
            long tagId = mTagList.get(mTagSpinner.getSelectedItemPosition()).getId();
            mDatabaseManager.deleteEntity(Tag.TABLE_NAME, tagId);
            updateSpinner(ResourceType.TAG);
        });
        // endregion

        // region Location Actions
        ImageButton btnAddLocation = view.findViewById(R.id.btnAddLocation);
        btnAddLocation.setOnClickListener(root -> {
            new LocationDialogFragment(ActionType.CREATE, 0, mDatabaseManager, this).show(getChildFragmentManager(), LocationDialogFragment.TAG);
        });

        mBtnEditLocation.setOnClickListener(root -> {
            long locationId = mLocationList.get(mLocationSpinner.getSelectedItemPosition()).getId();
            new LocationDialogFragment(ActionType.EDIT, locationId, mDatabaseManager, this).show(getChildFragmentManager(), LocationDialogFragment.TAG);
        });

        mBtnClearLocation.setOnClickListener(root -> {
            long locationId = mLocationList.get(mLocationSpinner.getSelectedItemPosition()).getId();
            mDatabaseManager.deleteEntity(Location.TABLE_NAME, locationId);
            updateSpinner(ResourceType.LOCATION);
        });
        // endregion

        return view;
    }

    private void saveChanges() {
        mWeather.setMaxTemp(CustomUtil.doubleParseDefault(mEditMaxTemp.getText().toString()));
        mWeather.setMinTemp(CustomUtil.doubleParseDefault(mEditMinTemp.getText().toString()));
        mWeather.setHumidity(CustomUtil.doubleParseDefault(mEditHumidity.getText().toString()));
        mWeather.setRain(CustomUtil.doubleParseDefault(mEditRainProb.getText().toString()));

        long tag = mTagList.size() > 0 ? mTagList.get(mTagSpinner.getSelectedItemPosition()).getId() : -1;
        long location = mLocationList.size() > 0 ? mLocationList.get(mLocationSpinner.getSelectedItemPosition()).getId() : -1;

        mWeather.setTag(tag);
        mWeather.setLocation(location);

        if (mWeather.validar()) {
            switch(mFormAction) {
                case CREATE:
                    mDatabaseManager.insertEntity(Weather.TABLE_NAME, mWeather.getContentValues());
                    break;
                case EDIT:
                    mDatabaseManager.editEntity(Weather.TABLE_NAME, mWeather.getId(), mWeather.getContentValues());
                    break;
            }

            Intent i = new Intent(getActivity(), MainActivity.class);
            i.putExtra(MainActivity.SELECTED_TAB, MainActivity.WEATHER_TAB);
            startActivity(i);
        } else {
            for (String errorMsg : mWeather.getErrors()) {
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadWeatherData(long id) {
        Cursor weatherCursor = mDatabaseManager.findById(Weather.TABLE_NAME, id);
        mWeather = Weather.fromCursor(weatherCursor);

        if (mWeather == null) {
            Log.w(TAG, "El clima seleccionado es nulo");
        }
    }

    private void bindViews(View view, long id) {
        // Views
        Button btnDatePicker = view.findViewById(R.id.datePickerBtn);
        mDateText = view.findViewById(R.id.dateText);
        mEditMaxTemp = view.findViewById(R.id.editItemName);
        mEditMinTemp = view.findViewById(R.id.editItemDescription);
        mEditHumidity = view.findViewById(R.id.editItemPrice);
        mEditRainProb = view.findViewById(R.id.editRain);

        Button btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(viewClick -> saveChanges());

        btnDatePicker.setOnClickListener(anotherView -> {
            showDatePicker();
        });

        mBtnEditTag = view.findViewById(R.id.btnEditTag);
        mBtnClearTag = view.findViewById(R.id.btnClearTag);
        mBtnEditLocation = view.findViewById(R.id.btnEditLocation);
        mBtnClearLocation = view.findViewById(R.id.btnClearLocation);

        mLocationSpinner = view.findViewById(R.id.spinnerLocation);
        mTagSpinner = view.findViewById(R.id.spinnerProductType);

        // Tag & Location Data
        setupSpinners();

        if (mFormAction == ActionType.EDIT && id != -1) {
            loadWeatherData(id);

            mDateText.setText(mWeather.getDate());
            mEditMaxTemp.setText(String.valueOf(mWeather.getMaxTemp()));
            mEditMinTemp.setText(String.valueOf(mWeather.getMinTemp()));
            mEditHumidity.setText(String.valueOf(mWeather.getHumidity()));
            mEditRainProb.setText(String.valueOf(mWeather.getRain()));

            mTagSpinner.setSelection(CustomUtil.getTagPositionById(mTagList, mWeather.getTag()));
            mLocationSpinner.setSelection(CustomUtil.getLocationPositionById(mLocationList, mWeather.getLocation()));
        }
    }

    private void getTagList() {
        Cursor tagCursor = mDatabaseManager.findMany(
            Tag.TABLE_NAME,
            Tag.ALL_COLUMNS,
            Tag.COLUMN_CATEGORY + " = ?",
            String.valueOf(ResourceType.WEATHER.getValue()));

        mTagList = Tag.manyFromCursor(tagCursor);
    }

    private void getLocationList() {
        Cursor locationCursor = mDatabaseManager.findMany(Location.TABLE_NAME, Location.ALL_COLUMNS);
        mLocationList = Location.manyFromCursor(locationCursor);
    }

    private void setupSpinners() {
        // Tags
        getTagList();

        mTagArrayAdapter = new ArrayAdapter<>(
            getActivity(),
            android.R.layout.simple_spinner_item,
            mTagList
        );

        mTagArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTagSpinner.setAdapter(mTagArrayAdapter);

        // Locations
        getLocationList();

        mLocationArrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                mLocationList
        );

        mLocationArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocationSpinner.setAdapter(mLocationArrayAdapter);

        updateSpinnerActions();
    }

    private void updateSpinner(ResourceType resource) {
        switch (resource) {
            case TAG:
                getTagList();
                updateSpinnerActions();

                mTagArrayAdapter.clear();
                mTagArrayAdapter.addAll(mTagList);
                mTagArrayAdapter.notifyDataSetChanged();
                return;
            case LOCATION:
                getLocationList();
                updateSpinnerActions();

                mLocationArrayAdapter.clear();
                mLocationArrayAdapter.addAll(mLocationList);
                mLocationArrayAdapter.notifyDataSetChanged();
                return;
        }
    }

    private void updateSpinnerActions() {
        boolean isTagListPopulated = mTagList.size() > 0;
        mTagSpinner.setEnabled(isTagListPopulated);
        mBtnEditTag.setEnabled(isTagListPopulated);
        mBtnClearTag.setEnabled(isTagListPopulated);

        boolean isLocationListPopulated = mLocationList.size() > 0;
        mLocationSpinner.setEnabled(isLocationListPopulated);
        mBtnEditLocation.setEnabled(isLocationListPopulated);
        mBtnClearLocation.setEnabled(isLocationListPopulated);
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            getContext(),
            this,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String dateISO = year + "-"
            + CustomUtil.padLeftZeros(String.valueOf(month + 1), 2) + "-"
            + CustomUtil.padLeftZeros(String.valueOf(day), 2);

        mWeather.setDate(dateISO);
        mDateText.setText(dateISO);
    }

    @Override
    public void alertDialogResult(ActionType result) {
        updateSpinner(ResourceType.TAG);
        updateSpinner(ResourceType.LOCATION);
    }
}
