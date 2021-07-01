package com.fernando.proyectofinal.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fernando.proyectofinal.ActionType;
import com.fernando.proyectofinal.CreateActivity;
import com.fernando.proyectofinal.CustomUtil;
import com.fernando.proyectofinal.R;
import com.fernando.proyectofinal.ResourceType;
import com.fernando.proyectofinal.db.DbHelper;
import com.fernando.proyectofinal.entities.Tag;
import com.fernando.proyectofinal.entities.Weather;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ContactViewHolder> {

    private static final String TAG = "WeatherAdapter";

    private List<Weather> mWeatherData;
    private List<Tag> mWeatherTypes;
    private final Context mContext;

    public WeatherAdapter(List<Weather> weather, List<Tag> weatherTypes, Context context)
    {
        this.mWeatherData = weather;
        this.mWeatherTypes = weatherTypes;
        this.mContext = context;
    }

    @NonNull
    @Override
    public WeatherAdapter.ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_card_item, null, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ContactViewHolder holder, int position) {
        Weather weather = mWeatherData.get(position);
        if (weather == null) return;

        holder.id = weather.getId();
        String temperatureRange = String.format("%s° - %s°", weather.getMinTemp(), weather.getMaxTemp());

        holder.temperatureText.setText(temperatureRange);
        holder.dateText.setText(mWeatherData.get(position).getDate());

        String weatherType = CustomUtil.getTypeString(mWeatherTypes, weather.getTag()).toLowerCase();
        int icon = 0;
        switch (weatherType) {
            case "lluvioso con sol":
                icon = R.drawable.ic_lluvia_sol;
                break;
            case "lluvia":
                icon = R.drawable.ic_lluvia;
                break;
            case "tormenta":
                icon = R.drawable.ic_tormenta;
                break;
            case "nubes":
                icon = R.drawable.ic_nubes;
                break;
            case "nieve":
                icon = R.drawable.ic_nieve;
                break;
            default:
                icon = R.drawable.ic_icon_sunny;
                break;
        }

        holder.weatherIcon.setImageResource(icon);
    }

    @Override
    public int getItemCount() {
        return mWeatherData.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        long id;
        TextView temperatureText;
        TextView dateText;
        ImageView weatherIcon;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            temperatureText = itemView.findViewById(R.id.cardHeader);
            dateText = itemView.findViewById(R.id.cardSubheader);
            weatherIcon = itemView.findViewById(R.id.cardIcon);

            itemView.setOnCreateContextMenuListener(this);

            itemView.setOnClickListener(item -> {
                Intent i = new Intent(mContext, CreateActivity.class);
                i.putExtra(CreateActivity.ACTION, ActionType.EDIT);
                i.putExtra(CreateActivity.RESOURCE, ResourceType.WEATHER);
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
        return mWeatherData.get(position).getId();
    }

    public void editItem(int position) {
        Log.i(TAG, String.valueOf(position));
        long id = mWeatherData.get(position).getId();
        Intent i = new Intent(mContext, CreateActivity.class);
        i.putExtra(CreateActivity.ACTION, ActionType.EDIT);
        i.putExtra(CreateActivity.RESOURCE, ResourceType.WEATHER);
        i.putExtra(DbHelper._ID, id);
        mContext.startActivity(i);
    }
}
