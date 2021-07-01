package com.fernando.proyectofinal.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.fernando.proyectofinal.entities.Inventory;
import com.fernando.proyectofinal.entities.Item;
import com.fernando.proyectofinal.entities.Tag;

import java.util.List;

public class GardenPlantAdapter extends RecyclerView.Adapter<GardenPlantAdapter.ItemViewHolder> {

    private List<Inventory> mGardenInventory;
    private List<Tag> mCategoryTags;
    private List<Item> mItemList;
    private Context mContext;

    public GardenPlantAdapter(List<Inventory> inventory, List<Item> items, List<Tag> tags, Context context)
    {
        this.mContext = context;
        this.mCategoryTags = tags;
        this.mItemList = items;
        this.mGardenInventory = inventory;
    }

    @NonNull
    @Override
    public GardenPlantAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_card_item, null, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Inventory inventoryItem = mGardenInventory.get(position);

        Item item = getItemById(inventoryItem.getArticleId());

        if (item != null) {
            holder.id = inventoryItem.getId();

            int iconResource = R.drawable.ic_plant;
            if (getTagCategoryById(item.getTag()) == ResourceType.ARTICLE.getValue()) {
                iconResource = R.drawable.ic_item;
            }

            holder.cardIcon.setImageResource(iconResource);
            holder.cardHeaderText.setText(item.getName());
            holder.cardSubheaderText.setText("En existencias: " + inventoryItem.getCapacity());
        }
    }

    @Override
    public int getItemCount() {
        return mGardenInventory.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        long id;
        TextView cardHeaderText;
        TextView cardSubheaderText;
        ImageView cardIcon;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            cardHeaderText = itemView.findViewById(R.id.cardHeader);
            cardSubheaderText = itemView.findViewById(R.id.cardSubheader);
            cardIcon = itemView.findViewById(R.id.cardIcon);

            itemView.setOnCreateContextMenuListener(this);

            itemView.setOnClickListener(item -> editItem(id));
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(this.getAdapterPosition(), 0, 0, R.string.menu_edit);
            contextMenu.add(this.getAdapterPosition(), 1, 1, R.string.menu_delete);
        }
    }

    public long getIdInPos(int position) {
        return mGardenInventory.get(position).getId();
    }

    public void editItem(long id) {

        Intent i = new Intent(mContext, CreateActivity.class);
        i.putExtra(CreateActivity.ACTION, ActionType.EDIT);
        i.putExtra(CreateActivity.RESOURCE, getInventoryResource(id));
        i.putExtra(DbHelper._ID, id);
        mContext.startActivity(i);
    }

    private ResourceType getInventoryResource(long inventoryId) {
        Inventory inventory = getInventoryById(inventoryId);
        Item item = getItemById(inventory.getArticleId());
        int tagCategory = (int) getTagCategoryById(item.getTag());

        int plantCategory = ResourceType.PLANT.getValue();

        ResourceType resource = tagCategory == plantCategory ? ResourceType.PLANT : ResourceType.ARTICLE;
        return resource;
    }

    private Inventory getInventoryById(long inventoryId) {
        for (Inventory inventory : mGardenInventory) {
            if (inventory.getId() == inventoryId) return inventory;
        }

        return null;
    }

    private Item getItemById(long itemId) {
        for (Item item : mItemList) {
            if (item.getId() == itemId) return item;
        }

        return null;
    }

    public List<Inventory> getInventory() {
        return this.mGardenInventory;
    }

    private long getTagCategoryById(long tagId) {
        Tag itemTag = null;

        for (Tag tag : mCategoryTags) {
            if (tag.getId() == tagId) {
                itemTag = tag;
                break;
            }
        }

        if (itemTag == null) {
            return -1;
        }

        return itemTag.getCategory();
    }
}
