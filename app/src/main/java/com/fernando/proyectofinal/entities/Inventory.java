package com.fernando.proyectofinal.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.fernando.proyectofinal.db.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class Inventory extends Entity {

    public static final String TABLE_NAME = "inventario";
    public static final String TAG = "InventoryEntity";

    public static final String COLUMN_CAPACITY = "capacidad";
    public static final String COLUMN_STORAGE = "id_almacen";
    public static final String COLUMN_ARTICLE = "id_articulo";

    public static final String[] ALL_COLUMNS = {
        DbHelper._ID,
        COLUMN_CAPACITY,
        COLUMN_STORAGE,
        COLUMN_ARTICLE
    };

    private long mId;
    private int mCapacity;
    private long mStorageId;
    private long mArticleId;

    public Inventory() {}

    private Inventory(long id, int capacity, long storageId, long articleId) {
        this.mId = id;
        this.mCapacity = capacity;
        this.mStorageId = storageId;
        this.mArticleId = articleId;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Inventory.COLUMN_CAPACITY, mCapacity);
        values.put(Inventory.COLUMN_STORAGE, mStorageId);
        values.put(Inventory.COLUMN_ARTICLE, mArticleId);

        return values;
    }

    public static Inventory fromCursor(Cursor c) {
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        Inventory inventory = new Inventory(
                c.getLong(0),
                c.getInt(1),
                c.getLong(2),
                c.getLong(3)
        );
        c.close();
        return inventory;
    }

    public static List<Inventory> manyFromCursor(Cursor c) {
        List<Inventory> inventoryList = new ArrayList<Inventory>();

        while (c.moveToNext()) {
            Inventory inventory = new Inventory(
                    c.getLong(0),
                    c.getInt(1),
                    c.getLong(2),
                    c.getLong(3)
            );
            inventoryList.add(inventory);
        }

        c.close();
        return inventoryList;
    }

    // region Get/Set
    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public int getCapacity() {
        return mCapacity;
    }

    public void setCapacity(int capacity) {
        mCapacity = capacity;
    }

    public long getStorageId() {
        return mStorageId;
    }

    public void setStorageId(long storageId) {
        mStorageId = storageId;
    }

    public long getArticleId() {
        return mArticleId;
    }

    public void setArticleId(long articleId) {
        mArticleId = articleId;
    }
    // endregion
}
