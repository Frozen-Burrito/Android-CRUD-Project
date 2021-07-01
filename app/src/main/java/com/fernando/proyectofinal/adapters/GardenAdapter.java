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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fernando.proyectofinal.ActionType;
import com.fernando.proyectofinal.CreateActivity;
import com.fernando.proyectofinal.GardenDetailsActivity;
import com.fernando.proyectofinal.MainActivity;
import com.fernando.proyectofinal.R;
import com.fernando.proyectofinal.ResourceType;
import com.fernando.proyectofinal.db.DbHelper;
import com.fernando.proyectofinal.entities.Garden;
import com.fernando.proyectofinal.ui.forms.GardenForm;

import java.util.List;

import static android.content.ContentValues.TAG;

public class GardenAdapter extends RecyclerView.Adapter<GardenAdapter.GardenViewHolder> {

    private List<Garden> mGardens;
    private Context mContext;

    public GardenAdapter(List<Garden> gardenList, Context context)
    {
        this.mGardens = gardenList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public GardenAdapter.GardenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.garden_item, null, false);
        return new GardenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GardenAdapter.GardenViewHolder holder, int position) {
        holder.id = mGardens.get(position).getId();
        Log.i(TAG, Long.toString(mGardens.get(position).getId()));
        holder.txtName.setText(mGardens.get(position).getName());
        holder.txtAddress.setText(mGardens.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return mGardens.size();
    }

    public class GardenViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        long id;
        TextView txtName;
        TextView txtAddress;

        public GardenViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.gardenName);
            txtAddress = itemView.findViewById(R.id.gardenAddress);
            CardView gardenItem = itemView.findViewById(R.id.gardenCardView);

            gardenItem.setOnCreateContextMenuListener(this);

            itemView.setOnClickListener(item -> {
                Intent i = new Intent(mContext, GardenDetailsActivity.class);
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
        return mGardens.get(position).getId();
    }

    public void editItem(int position) {
        long id = mGardens.get(position).getId();
        Intent i = new Intent(mContext, CreateActivity.class);
        i.putExtra(CreateActivity.ACTION, ActionType.EDIT);
        i.putExtra(CreateActivity.RESOURCE, ResourceType.GARDEN);
        i.putExtra(DbHelper._ID, id);
        i.putExtra(GardenForm.RETURN_TO, MainActivity.TAG);
        mContext.startActivity(i);
    }
}
