package com.fernando.proyectofinal.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fernando.proyectofinal.R;
import com.fernando.proyectofinal.adapters.GardenAdapter;
import com.fernando.proyectofinal.db.DatabaseManager;
import com.fernando.proyectofinal.entities.Garden;

import java.util.List;

public class HomeFragment extends Fragment {

    private List<Garden> mGardens;
    private GardenAdapter mAdapter;
    private DatabaseManager mDbManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mDbManager = DatabaseManager.getInstance(getActivity());

        mGardens = Garden.manyFromCursor(mDbManager.findMany(Garden.TABLE_NAME, Garden.ALL_COLUMNS));

        setupRecycler(view.findViewById(R.id.gardenRecycler));

        return view;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                mAdapter.editItem(item.getGroupId());
                return true;
            case 1:
                long id = mAdapter.getIdInPos(item.getGroupId());
                mDbManager.deleteEntity(Garden.TABLE_NAME, id);
                mGardens.remove(item.getGroupId());
                mAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void setupRecycler(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new GardenAdapter(mGardens, getContext());
        recyclerView.setAdapter(mAdapter);
    }
}