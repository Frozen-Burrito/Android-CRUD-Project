package com.fernando.proyectofinal.ui.people;

import android.database.Cursor;
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
import com.fernando.proyectofinal.adapters.PeopleAdapter;
import com.fernando.proyectofinal.db.DatabaseManager;
import com.fernando.proyectofinal.entities.Person;

import java.util.List;

public class PeopleFragment extends Fragment {

    private List<Person> mPeople;
    private PeopleAdapter mAdapter;
    private DatabaseManager dbManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dbManager = DatabaseManager.getInstance(getActivity());

        View view = inflater.inflate(R.layout.fragment_people, container, false);

        Cursor peopleCursor = dbManager.findMany(Person.TABLE_NAME, Person.ALL_COLUMNS);
        mPeople = Person.manyFromCursor(peopleCursor);


        // region Recycler
        RecyclerView personList = view.findViewById(R.id.peopleRecycler);
        personList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new PeopleAdapter(mPeople, getContext());
        personList.setAdapter(mAdapter);
        // endregion

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
                dbManager.deleteEntity(Person.TABLE_NAME, id);
                mPeople.remove(item.getGroupId());
                mAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}