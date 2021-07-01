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
import com.fernando.proyectofinal.R;
import com.fernando.proyectofinal.ResourceType;
import com.fernando.proyectofinal.db.DatabaseManager;
import com.fernando.proyectofinal.db.DbHelper;
import com.fernando.proyectofinal.entities.Inventory;
import com.fernando.proyectofinal.entities.Item;
import com.fernando.proyectofinal.entities.Tag;
import com.fernando.proyectofinal.ui.dialogs.AlertDialogResult;
import com.fernando.proyectofinal.ui.dialogs.TagDialogFragment;

import java.util.List;

public class ItemForm extends Fragment implements AlertDialogResult<ActionType> {

    public static final String TAG = "ItemForm";

    private Item mItem = new Item();
    private Inventory mInventoryItem = new Inventory();
    private List<Tag> mTagList;

    private ActionType mFormAction;
    private ResourceType mItemResource;
    private DatabaseManager mDatabaseManager;

    // Views
    private EditText mEditName;
    private EditText mEditDescription;
    private EditText mEditPrice;
    private EditText mEditQuantity;
    private Button mBtnSave;

    private ImageButton mBtnEditTag;
    private ImageButton mBtnClearTag;

    // Spinners
    private Spinner mTagSpinner;
    private ArrayAdapter<Tag> mTagArrayAdapter;

    public ItemForm() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_form, container, false);
        mDatabaseManager = DatabaseManager.getInstance(getActivity());

        Bundle bundle = getArguments();
        mFormAction = (ActionType) bundle.getSerializable(CreateActivity.ACTION);
        mItemResource = (ResourceType) bundle.getSerializable(CreateActivity.RESOURCE);
        long id = bundle.getLong(DbHelper._ID);

        bindViews(view, id);

        // region Tag Actions
        ImageButton btnAddTag = view.findViewById(R.id.btnAddTag);
        btnAddTag.setOnClickListener(root -> {
            new TagDialogFragment(ActionType.CREATE, mItemResource, 0, mDatabaseManager, this).show(getChildFragmentManager(), TagDialogFragment.TAG);
        });

        mBtnEditTag.setOnClickListener(root -> {
            long tagId = mTagList.get(mTagSpinner.getSelectedItemPosition()).getId();
            new TagDialogFragment(ActionType.EDIT, mItemResource, tagId, mDatabaseManager, this).show(getChildFragmentManager(), TagDialogFragment.TAG);
        });

        mBtnClearTag.setOnClickListener(root -> {
            long tagId = mTagList.get(mTagSpinner.getSelectedItemPosition()).getId();
            mDatabaseManager.deleteEntity(Tag.TABLE_NAME, tagId);
            updateSpinner(ResourceType.TAG);
        });
        // endregion

        return view;
    }

    private void saveChanges() {
        mItem.setName(mEditName.getText().toString());
        mItem.setDescription(mEditDescription.getText().toString());
        mItem.setPrice(CustomUtil.doubleParseDefault(mEditPrice.getText().toString()));

        long tag = mTagList.size() > 0 ? mTagList.get(mTagSpinner.getSelectedItemPosition()).getId() : -1;
        mItem.setTag(tag);

        if (!mEditQuantity.getText().toString().isEmpty()) {
            int quantity = Integer.parseInt(mEditQuantity.getText().toString());
            mInventoryItem.setCapacity(quantity);
        }

        mInventoryItem.setStorageId(GardenDetailsActivity.sGardenId);

        if (mItem.validar()) {
            switch(mFormAction) {
                case CREATE:
                    long insertedId = mDatabaseManager.insertEntity(Item.TABLE_NAME, mItem.getContentValues());
                    mInventoryItem.setArticleId(insertedId);
                    mDatabaseManager.insertEntity(Inventory.TABLE_NAME, mInventoryItem.getContentValues());
                    break;
                case EDIT:
                    mDatabaseManager.editEntity(Item.TABLE_NAME, mItem.getId(), mItem.getContentValues());
                    mDatabaseManager.editEntity(Inventory.TABLE_NAME, mInventoryItem.getId(), mInventoryItem.getContentValues());
                    break;
            }

            Intent i = new Intent(getActivity(), GardenDetailsActivity.class);
            i.putExtra(DbHelper._ID, GardenDetailsActivity.sGardenId);
            i.putExtra(GardenDetailsActivity.SELECTED_TAB, GardenDetailsActivity.ITEM_TAB_INDEX);
            startActivity(i);
        } else {
            for (String errorMsg : mItem.getErrors()) {
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadItemData(long id) {
        Cursor inventoryCursor = mDatabaseManager.findById(Inventory.TABLE_NAME, id);
        mInventoryItem = Inventory.fromCursor(inventoryCursor);

        if (mInventoryItem != null) {
            Cursor itemCursor = mDatabaseManager.findById(Item.TABLE_NAME, mInventoryItem.getArticleId());
            mItem = Item.fromCursor(itemCursor);

            if (mItem == null) mItem = new Item();
        } else {
            Log.w(TAG, "El Item del Inventario es nulo");
        }
    }

    private void bindViews(View view, long id) {
        mEditName = view.findViewById(R.id.editItemName);
        mEditDescription = view.findViewById(R.id.editItemDescription);
        mEditPrice = view.findViewById(R.id.editItemPrice);
        mEditQuantity = view.findViewById(R.id.editItemQuantity);

        mTagSpinner = view.findViewById(R.id.spinnerProductType);
        mBtnSave = view.findViewById(R.id.btnSave);
        mBtnSave.setOnClickListener(viewClick -> saveChanges());

        mBtnEditTag = view.findViewById(R.id.btnEditTag);
        mBtnClearTag = view.findViewById(R.id.btnClearTag);

        // Tag Data
        setupSpinners();

        if (mFormAction == ActionType.EDIT && id != -1) {
            loadItemData(id);

            mEditName.setText(String.valueOf(mItem.getName()));
            mEditDescription.setText(String.valueOf(mItem.getDescription()));
            mEditPrice.setText(String.valueOf(mItem.getPrice()));

            mEditQuantity.setText(String.valueOf(mInventoryItem.getCapacity()));

            mTagSpinner.setSelection(CustomUtil.getTagPositionById(mTagList, mItem.getTag()));
        }
    }

    private void getTagList() {
        Cursor tagCursor = mDatabaseManager.findMany(
            Tag.TABLE_NAME,
            Tag.ALL_COLUMNS,
            Tag.COLUMN_CATEGORY + " = ?",
            String.valueOf(mItemResource.getValue()));

        mTagList = Tag.manyFromCursor(tagCursor);
    }

    private void setupSpinners() {
        // Tags
        getTagList();
        updateSpinnerActions();

        mTagArrayAdapter = new ArrayAdapter<>(
            getActivity(),
            android.R.layout.simple_spinner_item,
            mTagList
        );

        mTagArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTagSpinner.setAdapter(mTagArrayAdapter);
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
        }
    }

    private void updateSpinnerActions() {
        boolean isTagListPopulated = mTagList.size() > 0;
        mTagSpinner.setEnabled(isTagListPopulated);
        mBtnEditTag.setEnabled(isTagListPopulated);
        mBtnClearTag.setEnabled(isTagListPopulated);
    }

    @Override
    public void alertDialogResult(ActionType result) {
        updateSpinner(ResourceType.TAG);
    }
}
