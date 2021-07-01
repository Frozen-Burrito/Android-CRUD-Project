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
import com.fernando.proyectofinal.ResourceType;
import com.fernando.proyectofinal.db.DatabaseManager;
import com.fernando.proyectofinal.entities.Tag;

public class TagDialogFragment extends DialogFragment {

    private Tag mTag = new Tag();
    private ActionType mAction;
    private String mMessage;

    private EditText mEditName;

    private DatabaseManager mDbManager;
    private AlertDialogResult<ActionType> dialogResult;

    public static String TAG = "TagDialog";
    private ResourceType mResource;

    public TagDialogFragment() {}

    public TagDialogFragment(ActionType action, ResourceType resource, long tagId, DatabaseManager dbManager, final AlertDialogResult<ActionType> result) {
        mAction = action;
        mResource = resource;
        mTag.setId(tagId);
        mDbManager = dbManager;
        dialogResult = result;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.tag_dialog, null);

        setupView(dialogView);
        if (mAction == ActionType.EDIT) {
            Log.i(TAG, "Editar Tag con Id: " + mTag.getId());
            loadTag(mTag.getId());
        };

        builder.setView(dialogView).setMessage(mMessage);

        Button btnOk = dialogView.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(view -> saveTag());

        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(view -> dismiss());

        return builder.create();
    }

    private void saveTag() {

        mTag.setName(mEditName.getText().toString());
        Log.i(TAG, "Resource Category: " + mResource.getValue());
        mTag.setCategory(mResource.getValue());

        switch (mAction) {
            case CREATE:
                mDbManager.insertEntity(Tag.TABLE_NAME, mTag.getContentValues());
                dialogResult.alertDialogResult(ActionType.CREATE);
                break;
            case EDIT:
                mDbManager.editEntity(Tag.TABLE_NAME, mTag.getId(), mTag.getContentValues());
                dialogResult.alertDialogResult(ActionType.EDIT);
                break;
        }
        dismiss();
    }

    private void loadTag(long id) {
        Cursor c = mDbManager.findById(Tag.TABLE_NAME, id);
        mTag = Tag.fromCursor(c);

        if (mTag == null) return;
        Log.i(TAG, "Tag: " + mTag.toString());
        mEditName.setText(mTag.getName());
    }

    private void setupView(View dialogView) {

        mEditName = dialogView.findViewById(R.id.editLocationCountry);

        switch (mAction) {
            case CREATE:
                mMessage = "Crea una Etiqueta";
                break;
            case EDIT:
                mMessage = "Edita una Etiqueta";
                break;
        }
    }
}
