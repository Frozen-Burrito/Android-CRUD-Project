package com.fernando.proyectofinal.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.fernando.proyectofinal.db.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class Item extends Entity {

    public static final String TABLE_NAME = "articulo";
    public static final String TAG = "ItemEntity";

    public static final String COLUMN_NAME = "nombre";
    public static final String COLUMN_DESCRIPTION = "descripcion";
    public static final String COLUMN_PRICE = "precio";
    public static final String COLUMN_TAG = "id_tag";

    public static final String[] ALL_COLUMNS = {
        DbHelper._ID,
        COLUMN_NAME,
        COLUMN_DESCRIPTION,
        COLUMN_PRICE,
        COLUMN_TAG
    };

    private long mId;
    private String mName;
    private String mDescription;
    private double mPrice;
    private long mTag;

    private List<String> mErrors;

    public Item() {}

    private Item(long id, String name, String description, double price, long tagId) {
        this.mId = id;
        this.mName = name;
        this.mDescription = description;
        this.mPrice = price;
        this.mTag = tagId;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Item.COLUMN_NAME, mName);
        values.put(Item.COLUMN_DESCRIPTION, mDescription);
        values.put(Item.COLUMN_PRICE, mPrice);
        values.put(Item.COLUMN_TAG, mTag);

        return values;
    }

    public boolean validar() {
        this.mErrors = new ArrayList<>();
        if (this.mName.equals("")) mErrors.add("El nombre está vacío");
        if (this.mPrice == 0) mErrors.add("El precio no debe ser 0");
        if (this.mTag < 0) mErrors.add("Crea una etiqueta para el artículo");

        return this.mErrors.size() == 0;
    }


    public static Item fromCursor(Cursor c) {
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        Item item = new Item(
            c.getLong(0),
            c.getString(1),
            c.getString(2),
            c.getDouble(3),
            c.getLong(4)
        );
        c.close();
        return item;
    }

    public static List<Item> manyFromCursor(Cursor c) {
        List<Item> itemList = new ArrayList<Item>();

        while (c.moveToNext()) {
            Item item = new Item(
                c.getLong(0),
                c.getString(1),
                c.getString(2),
                c.getDouble(3),
                c.getLong(4)
            );
            itemList.add(item);
        }

        c.close();
        return itemList;
    }

    // region Get/Set
    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double price) {
        mPrice = price;
    }

    public long getTag() {
        return mTag;
    }

    public void setTag(long tag) {
        mTag = tag;
    }

    public List<String> getErrors() {
        return mErrors;
    }
    // endregion
}
