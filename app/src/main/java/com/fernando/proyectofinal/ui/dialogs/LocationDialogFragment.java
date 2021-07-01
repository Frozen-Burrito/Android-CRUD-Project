package com.fernando.proyectofinal.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.fernando.proyectofinal.ActionType;
import com.fernando.proyectofinal.R;
import com.fernando.proyectofinal.db.DatabaseManager;
import com.fernando.proyectofinal.entities.Location;

public class LocationDialogFragment extends DialogFragment {

    private Location mLocation = new Location();
    private ActionType mAction;
    private String mMessage;

    private EditText mEditCountry;
    private EditText mEditLocality;
    private EditText mEditCity;

    private DatabaseManager mDbManager;
    private AlertDialogResult<ActionType> dialogResult;

    public static String TAG = "LOCATION_DIALOG";

    public LocationDialogFragment() {}

    public LocationDialogFragment(ActionType action, long locationId, DatabaseManager dbManager, final AlertDialogResult<ActionType> result) {
        mAction = action;
        mLocation.setId(locationId);
        mDbManager = dbManager;
        dialogResult = result;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.location_dialog, null);

        setupView(dialogView);
        if (mAction == ActionType.EDIT) {
            Log.i(TAG, "Editar Location con Id: " + mLocation.getId());
            loadLocation(mLocation.getId());
        };

        builder.setView(dialogView).setMessage(mMessage);

        Button btnOk = dialogView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(view -> saveLocation());

        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(view -> dismiss());

        return builder.create();
    }

    private void saveLocation() {

        mLocation.setPais(mEditCountry.getText().toString());
        mLocation.setEstado(mEditLocality.getText().toString());
        mLocation.setCiudad(mEditCity.getText().toString());

        switch (mAction) {
            case CREATE:
                mDbManager.insertEntity(Location.TABLE_NAME, mLocation.getContentValues());
                dialogResult.alertDialogResult(ActionType.CREATE);
                break;
            case EDIT:
                mDbManager.editEntity(Location.TABLE_NAME, mLocation.getId(), mLocation.getContentValues());
                dialogResult.alertDialogResult(ActionType.CREATE);
                break;
        }
        dismiss();
    }

    private void loadLocation(long id) {
        Cursor c = mDbManager.findById(Location.TABLE_NAME, id);
        mLocation = (Location) mLocation.fromCursor(c);

        if (mLocation == null) return;
        mEditCountry.setText(mLocation.getPais());
        mEditLocality.setText(mLocation.getEstado());
        mEditCity.setText(mLocation.getCiudad());
    }

    private void setupView(View dialogView) {

        mEditCountry = dialogView.findViewById(R.id.editLocationCountry);
        mEditLocality = dialogView.findViewById(R.id.editLocationLocality);
        mEditCity = dialogView.findViewById(R.id.editLocationCity);

        switch (mAction) {
            case CREATE:
                mMessage = "Añade una ubicación";
                break;
            case EDIT:
                mMessage = "Edita una ubicación";
                break;
        }
    }
}
