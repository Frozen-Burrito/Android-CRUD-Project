package com.fernando.proyectofinal.ui.forms;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fernando.proyectofinal.ActionType;
import com.fernando.proyectofinal.CreateActivity;
import com.fernando.proyectofinal.CustomUtil;
import com.fernando.proyectofinal.GardenDetailsActivity;
import com.fernando.proyectofinal.MainActivity;
import com.fernando.proyectofinal.R;
import com.fernando.proyectofinal.ResourceType;
import com.fernando.proyectofinal.db.DatabaseManager;
import com.fernando.proyectofinal.db.DbHelper;
import com.fernando.proyectofinal.entities.Garden;
import com.fernando.proyectofinal.entities.Location;
import com.fernando.proyectofinal.entities.Tag;
import com.fernando.proyectofinal.ui.dialogs.AlertDialogResult;
import com.fernando.proyectofinal.ui.dialogs.LocationDialogFragment;
import com.fernando.proyectofinal.ui.dialogs.TagDialogFragment;

import java.util.List;

public class GardenForm extends Fragment implements AlertDialogResult<ActionType> {

    public static final String TAG = "GardenForm";

    public static final String RETURN_TO = "RETURN_TO";

    private Garden mGarden = new Garden();
    private List<Tag> mTagList;
    private List<Location> mLocationList;

    private ActionType mFormAction;
    private String mReturnToActivity;
    private DatabaseManager mDatabaseManager;

    // Views
    private EditText mEditName;
    private EditText mEditAddress;

    private ImageButton mBtnEditTag;
    private ImageButton mBtnClearTag;
    private ImageButton mBtnEditLocation;
    private ImageButton mBtnClearLocation;

    // Spinners
    private Spinner mLocationSpinner;
    private ArrayAdapter<Location> mLocationArrayAdapter;
    private Spinner mTagSpinner;
    private ArrayAdapter<Tag> mTagArrayAdapter;

    public GardenForm() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_garden_form, container, false);
        mDatabaseManager = DatabaseManager.getInstance(getActivity());

        // Get garden ID
        Bundle bundle = getArguments();
        mFormAction = (ActionType) bundle.getSerializable(CreateActivity.ACTION);
        mReturnToActivity = bundle.getString(RETURN_TO);
        long id = bundle.getLong(DbHelper._ID);

        bindViews(view, id);

        Button btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(viewClick -> saveChanges());

        // region Tag Actions
        ImageButton btnAddTag = view.findViewById(R.id.btnAddTag);
        btnAddTag.setOnClickListener(root -> {
            new TagDialogFragment(ActionType.CREATE, ResourceType.GARDEN, 0, mDatabaseManager, this).show(getChildFragmentManager(), TagDialogFragment.TAG);
        });

        mBtnEditTag.setOnClickListener(root -> {
            long tagId = mTagList.get(mTagSpinner.getSelectedItemPosition()).getId();
            new TagDialogFragment(ActionType.EDIT, ResourceType.GARDEN, tagId, mDatabaseManager, this).show(getChildFragmentManager(), TagDialogFragment.TAG);
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
        mGarden.setName(mEditName.getText().toString());
        mGarden.setAddress(mEditAddress.getText().toString());

        long tag = mTagList.size() > 0 ? mTagList.get(mTagSpinner.getSelectedItemPosition()).getId() : -1;
        long location = mLocationList.size() > 0 ? mLocationList.get(mLocationSpinner.getSelectedItemPosition()).getId() : -1;

        mGarden.setTag(tag);
        mGarden.setLocation(location);

        if (mGarden.validar()) {
            switch(mFormAction) {
                case CREATE:
                    mDatabaseManager.insertEntity(Garden.TABLE_NAME, mGarden.getContentValues());
                    break;
                case EDIT:
                    mDatabaseManager.editEntity(Garden.TABLE_NAME, mGarden.getId(), mGarden.getContentValues());
                    break;
            }

            if (GardenDetailsActivity.TAG.equals(mReturnToActivity)) {
                Intent i = new Intent(getActivity(), GardenDetailsActivity.class);
                i.putExtra(DbHelper._ID, mGarden.getId());
                startActivity(i);
            } else {
                Intent i = new Intent(getActivity(), MainActivity.class);
                i.putExtra(DbHelper._ID, mGarden.getId());
                startActivity(i);
            }

        } else {
            for (String errorMsg : mGarden.getErrors()) {
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadGardenData(long id) {
        Cursor gardenCursor = mDatabaseManager.findById(Garden.TABLE_NAME, id);
        mGarden = Garden.fromCursor(gardenCursor);

        if (mGarden == null) {
            Log.w(TAG, "El jardín es null");
        }
    }

    private void bindViews(View view, long id) {
        mEditName = view.findViewById(R.id.editGardenName);
        mEditAddress = view.findViewById(R.id.editDireccion);

        mLocationSpinner = view.findViewById(R.id.spinnerLocation);
        mTagSpinner = view.findViewById(R.id.spinnerProductType);

        mBtnEditTag = view.findViewById(R.id.btnEditTag);
        mBtnClearTag = view.findViewById(R.id.btnClearTag);
        mBtnEditLocation = view.findViewById(R.id.btnEditLocation);
        mBtnClearLocation = view.findViewById(R.id.btnClearLocation);

        setupSpinners();

        if (mFormAction == ActionType.EDIT && id != -1) {
            loadGardenData(id);

            mEditName.setText(mGarden.getName());
            mEditAddress.setText(mGarden.getAddress());

            mTagSpinner.setSelection(CustomUtil.getTagPositionById(mTagList, mGarden.getTag()));
            mLocationSpinner.setSelection(CustomUtil.getLocationPositionById(mLocationList, mGarden.getLocation()));
        }
    }

    private void getTagList() {
        Cursor tagCursor = mDatabaseManager.findMany(
            Tag.TABLE_NAME,
            Tag.ALL_COLUMNS,
            Tag.COLUMN_CATEGORY + " = ?",
            String.valueOf(ResourceType.GARDEN.getValue()));

        mTagList = Tag.manyFromCursor(tagCursor);
    }

    private void getLocationList() {
        Cursor locationCursor = mDatabaseManager.findMany(
            Location.TABLE_NAME,
            Location.ALL_COLUMNS);

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

    @Override
    public void alertDialogResult(ActionType result) {
        updateSpinner(ResourceType.TAG);
        updateSpinner(ResourceType.LOCATION);
    }
}
