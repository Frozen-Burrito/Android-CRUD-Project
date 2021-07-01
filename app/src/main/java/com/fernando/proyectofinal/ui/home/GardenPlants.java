package com.fernando.proyectofinal.ui.home;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fernando.proyectofinal.GardenDetailsActivity;
import com.fernando.proyectofinal.R;
import com.fernando.proyectofinal.ResourceType;
import com.fernando.proyectofinal.adapters.GardenPlantAdapter;
import com.fernando.proyectofinal.entities.Inventory;
import com.fernando.proyectofinal.entities.Item;
import com.fernando.proyectofinal.entities.Tag;

import java.util.List;

import static android.content.ContentValues.TAG;

public class GardenPlants extends Fragment {

    private long mGardenId;
    private GardenPlantAdapter mAdapter;
    List<Inventory> mGardenInventory;
    List<Item> mItemList;
    List<Tag> mTagList;

    public GardenPlants() {}

    public static GardenPlants newInstance(long gardenId) {
        GardenPlants fragment = new GardenPlants();
        Bundle args = new Bundle();
        args.putLong(GardenDetailsActivity.GARDEN_ID, gardenId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGardenId = getArguments().getLong(GardenDetailsActivity.GARDEN_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_garden_plants, container, false);

        getGardenInventory();
        getInventoryTags();

        displayPlants(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "Llamada a onResume");
        mAdapter.notifyDataSetChanged();
    }

    private void displayPlants(View view) {

        final RecyclerView inventoryRecycler = view.findViewById(R.id.itemsRecycler);
        inventoryRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new GardenPlantAdapter(mGardenInventory, mItemList, mTagList, getActivity());
        inventoryRecycler.setAdapter(mAdapter);
    }

    private void getGardenInventory() {
        Cursor inventoryCursor = GardenDetailsActivity.sDatabaseManager.findMany(
                Inventory.TABLE_NAME, Inventory.ALL_COLUMNS,
                Inventory.COLUMN_STORAGE + " = ?",
                String.valueOf(mGardenId)
        );

        mGardenInventory = Inventory.manyFromCursor(inventoryCursor);

        Cursor itemCursor = GardenDetailsActivity.sDatabaseManager.findMany(Item.TABLE_NAME, Item.ALL_COLUMNS);
        mItemList = Item.manyFromCursor(itemCursor);
    }

    private void getInventoryTags() {
        Cursor tagsCursor = GardenDetailsActivity.sDatabaseManager.findMany(
                Tag.TABLE_NAME,
                Tag.ALL_COLUMNS,
                Tag.COLUMN_CATEGORY + " = ?",
                String.valueOf(ResourceType.PLANT.getValue()));

        mTagList = Tag.manyFromCursor(tagsCursor);

        tagsCursor = GardenDetailsActivity.sDatabaseManager.findMany(
                Tag.TABLE_NAME,
                Tag.ALL_COLUMNS,
                Tag.COLUMN_CATEGORY + " = ?",
                String.valueOf(ResourceType.ARTICLE.getValue()));

        mTagList.addAll(Tag.manyFromCursor(tagsCursor));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case 0: // Editar
                long itemId = mAdapter.getIdInPos(item.getGroupId());
                mAdapter.editItem(itemId);
                return true;
            case 1: // Eliminar del jardin actual
                long inventoryId = mAdapter.getIdInPos(item.getGroupId());
                GardenDetailsActivity.sDatabaseManager.deleteEntity(Inventory.TABLE_NAME, inventoryId);
                mAdapter.getInventory().remove(item.getGroupId());
                mAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}