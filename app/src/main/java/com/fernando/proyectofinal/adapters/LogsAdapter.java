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
import com.fernando.proyectofinal.R;
import com.fernando.proyectofinal.ResourceType;
import com.fernando.proyectofinal.db.DbHelper;
import com.fernando.proyectofinal.entities.Action;
import com.fernando.proyectofinal.entities.User;

import java.util.HashMap;
import java.util.List;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.ContactViewHolder> {

    private static final String TAG = "LogsAdapter";

    private List<Action> mActivityLogData;
    private HashMap<Long, String> mUsernameMap;
    private Context mContext;

    public LogsAdapter(List<Action> activityLogs, HashMap<Long, String> usernameMap, Context context)
    {
        this.mActivityLogData = activityLogs;
        this.mUsernameMap = usernameMap;
        this.mContext = context;
    }

    @NonNull
    @Override
    public LogsAdapter.ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_card_item, null, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogsAdapter.ContactViewHolder holder, int position) {
        Action action = mActivityLogData.get(position);
        if (action == null) return;

        holder.id = action.getId();
        String resourceType = ResourceType.toString(action.getResource());

        holder.resourceText.setText(resourceType);
        holder.dateText.setText(action.getDate());
        holder.usernameText.setText(mUsernameMap.get(action.getUser()));

        int cardIconId = 0;
        ActionType actionType = ActionType.getActionFromInt(action.getActionType());

        switch (actionType) {
            case CREATE:
                cardIconId = R.drawable.ic_add;
                break;
            case READ:
                cardIconId = R.drawable.ic_read;
                break;
            case EDIT:
                cardIconId = R.drawable.ic_edit;
                break;
            case DELETE:
                cardIconId = R.drawable.ic_clear;
                break;
            default:
                cardIconId = R.drawable.ic_unknown;
                break;
        }

        holder.actionIcon.setImageResource(cardIconId);
    }

    @Override
    public int getItemCount() {
        return mActivityLogData.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        long id;
        TextView resourceText;
        TextView dateText;
        TextView usernameText;
        ImageView actionIcon;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            resourceText = itemView.findViewById(R.id.cardHeader);
            dateText = itemView.findViewById(R.id.cardSubheader);
            usernameText = itemView.findViewById(R.id.cardAltText);
            actionIcon = itemView.findViewById(R.id.cardIcon);
        }
    }
}
