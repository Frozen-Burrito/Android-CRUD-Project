package com.fernando.proyectofinal.ui.forms;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import com.fernando.proyectofinal.ActionType;
import com.fernando.proyectofinal.CreateActivity;
import com.fernando.proyectofinal.CustomUtil;
import com.fernando.proyectofinal.MainActivity;
import com.fernando.proyectofinal.R;
import com.fernando.proyectofinal.ResourceType;
import com.fernando.proyectofinal.db.DatabaseManager;
import com.fernando.proyectofinal.db.DbHelper;
import com.fernando.proyectofinal.entities.Person;
import com.fernando.proyectofinal.entities.Tag;
import com.fernando.proyectofinal.ui.dialogs.AlertDialogResult;
import com.fernando.proyectofinal.ui.dialogs.TagDialogFragment;

import java.util.List;

import static android.content.ContentValues.TAG;

public class PersonForm extends Fragment implements AlertDialogResult<ActionType> {

    private Person mPerson = new Person();
    private List<Tag> mTagList;

    private ActionType mFormAction;
    private DatabaseManager mDatabaseManager;

    // Views
    private EditText mEditName;
    private EditText mEditLastname;
    private EditText mEditPhone;
    private EditText mEditEmail;
    private Button mBtnSave;

    // Spinners
    private Spinner mRoleSpinner;
    private ArrayAdapter<Tag> mTagArrayAdapter;
    private ImageButton mBtnClearTag;
    private ImageButton mBtnEditTag;

    public PersonForm() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_form, container, false);
        mDatabaseManager = DatabaseManager.getInstance(getActivity());

        // Get person ID
        Bundle bundle = getArguments();
        mFormAction = (ActionType) bundle.getSerializable(CreateActivity.ACTION);
        long id = bundle.getLong(DbHelper._ID);

        bindViews(view, id);

        // region Tag Actions
        ImageButton btnAddTag = view.findViewById(R.id.btnAddTag);
        btnAddTag.setOnClickListener(root -> {
            new TagDialogFragment(ActionType.CREATE, ResourceType.PERSON, 0, mDatabaseManager, this).show(getChildFragmentManager(), TagDialogFragment.TAG);
        });

        mBtnEditTag.setOnClickListener(root -> {
            long tagId = mTagList.get(mRoleSpinner.getSelectedItemPosition()).getId();
            new TagDialogFragment(ActionType.EDIT, ResourceType.PERSON, tagId, mDatabaseManager, this).show(getChildFragmentManager(), TagDialogFragment.TAG);
        });

        mBtnClearTag.setOnClickListener(root -> {
            long tagId = mTagList.get(mRoleSpinner.getSelectedItemPosition()).getId();
            mDatabaseManager.deleteEntity(Tag.TABLE_NAME, tagId);
            updateSpinner(ResourceType.TAG);
        });
        // endregion

        return view;
    }

    private void saveChanges() {
        mPerson.setName(mEditName.getText().toString());
        mPerson.setLastname(mEditLastname.getText().toString());
        mPerson.setPhone(mEditPhone.getText().toString());
        mPerson.setEmail(mEditEmail.getText().toString());

        long tag = mTagList.size() > 0 ? mTagList.get(mRoleSpinner.getSelectedItemPosition()).getId() : -1;
        mPerson.setTag(tag);

        if (mPerson.validar()) {
            switch (mFormAction) {
                case CREATE:
                    mDatabaseManager.insertEntity(Person.TABLE_NAME, mPerson.getContentValues());
                    break;
                case EDIT:
                    mDatabaseManager.editEntity(Person.TABLE_NAME, mPerson.getId(), mPerson.getContentValues());
                    break;
            }

            Intent i = new Intent(getActivity(), MainActivity.class);
            i.putExtra(MainActivity.SELECTED_TAB, MainActivity.PERSON_TAB);
            startActivity(i);
        } else {
            for (String errorMsg : mPerson.getErrors()) {
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadPersonData(long id) {
        mPerson = Person.fromCursor(mDatabaseManager.findById(Person.TABLE_NAME, id));

        if (mPerson == null) {
            Log.w(TAG, "La persona es null");
        }
    }

    private void bindViews(View view, long id) {
        mEditName = view.findViewById(R.id.editGardenName);
        mEditLastname = view.findViewById(R.id.editDireccion);
        mEditPhone = view.findViewById(R.id.editCelular);
        mEditEmail = view.findViewById(R.id.editEmail);

        mBtnEditTag = view.findViewById(R.id.btnEditTag);
        mBtnClearTag = view.findViewById(R.id.btnClearTag);

        mRoleSpinner = view.findViewById(R.id.spinnerProductType);
        mBtnSave = view.findViewById(R.id.btnSave);
        mBtnSave.setOnClickListener(root -> saveChanges());

        setupSpinners();

        if (mFormAction == ActionType.EDIT && id != -1) {
            loadPersonData(id);

            mEditName.setText(mPerson.getName());
            mEditLastname.setText(mPerson.getLastname());
            mEditPhone.setText(mPerson.getPhone());
            mEditEmail.setText(mPerson.getEmail());

            mRoleSpinner.setSelection(CustomUtil.getTagPositionById(mTagList, mPerson.getTag()));
        }
    }

    private void getTagList() {
        Cursor tagCursor = mDatabaseManager.findMany(
            Tag.TABLE_NAME,
            Tag.ALL_COLUMNS,
            Tag.COLUMN_CATEGORY + " = ?",
            String.valueOf(ResourceType.PERSON.getValue()));

        mTagList = Tag.manyFromCursor(tagCursor);
    }

    private void setupSpinners() {
        getTagList();
        updateSpinnerActions();

        mTagArrayAdapter = new ArrayAdapter<>(
            getActivity(),
            android.R.layout.simple_spinner_item,
            mTagList
        );

        mTagArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRoleSpinner.setAdapter(mTagArrayAdapter);
    }

    private void updateSpinner(ResourceType resource) {
        if (resource == ResourceType.TAG) {
            getTagList();
            updateSpinnerActions();

            mTagArrayAdapter.clear();
            mTagArrayAdapter.addAll(mTagList);
            mTagArrayAdapter.notifyDataSetChanged();
        }
    }

    private void updateSpinnerActions() {
        boolean isTagListPopulated = mTagList.size() > 0;
        mRoleSpinner.setEnabled(isTagListPopulated);
        mBtnEditTag.setEnabled(isTagListPopulated);
        mBtnClearTag.setEnabled(isTagListPopulated);
    }

    @Override
    public void alertDialogResult(ActionType result) {
        updateSpinner(ResourceType.TAG);
        updateSpinner(ResourceType.GARDEN);
    }
}