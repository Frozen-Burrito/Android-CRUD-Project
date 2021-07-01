package com.fernando.proyectofinal.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.fernando.proyectofinal.ActionType;
import com.fernando.proyectofinal.CreateActivity;
import com.fernando.proyectofinal.R;
import com.fernando.proyectofinal.ResourceType;
import com.fernando.proyectofinal.db.DbHelper;
import com.fernando.proyectofinal.entities.Person;

import java.util.List;

import static android.content.ContentValues.TAG;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ContactViewHolder> {

    private List<Person> mPeople;
    private Context mContext;

    public PeopleAdapter(List<Person> people, Context context)
    {
        this.mPeople = people;
        this.mContext = context;
    }

    @NonNull
    @Override
    public PeopleAdapter.ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_item, null, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleAdapter.ContactViewHolder holder, int position) {
        holder.id = mPeople.get(position).getId();
        Log.i(TAG, Long.toString(mPeople.get(position).getId()));
        holder.txtNombre.setText(mPeople.get(position).getName() + " " + mPeople.get(position).getLastname());
        holder.txtCelular.setText(mPeople.get(position).getPhone());
        holder.txtEmail.setText(mPeople.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return mPeople.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        long id;
        TextView txtNombre;
        TextView txtCelular;
        TextView txtEmail;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtCelular = itemView.findViewById(R.id.txtCelular);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            ConstraintLayout personItem = itemView.findViewById(R.id.personItem);

            personItem.setOnCreateContextMenuListener(this);

            itemView.setOnClickListener(item -> {
                Intent i = new Intent(mContext, CreateActivity.class);
                i.putExtra(CreateActivity.ACTION, ActionType.EDIT);
                i.putExtra(CreateActivity.RESOURCE, ResourceType.PERSON);
                i.putExtra(DbHelper._ID, id);
                mContext.startActivity(i);
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(this.getAdapterPosition(), 0, 0, R.string.menu_edit);
            contextMenu.add(this.getAdapterPosition(), 1, 1, R.string.menu_delete);
        }
    }

    public long getIdInPos(int position) {
        return mPeople.get(position).getId();
    }

    public void editItem(int position) {
        long id = mPeople.get(position).getId();
        Intent i = new Intent(mContext, CreateActivity.class);
        i.putExtra(CreateActivity.ACTION, ActionType.EDIT);
        i.putExtra(CreateActivity.RESOURCE, ResourceType.PERSON);
        i.putExtra(DbHelper._ID, id);
        mContext.startActivity(i);
    }
}
