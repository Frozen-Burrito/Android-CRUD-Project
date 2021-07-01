package com.fernando.proyectofinal.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.fernando.proyectofinal.db.DbHelper;

import java.util.ArrayList;
import java.util.List;

public final class Garden extends Entity {

    public static final String TABLE_NAME = "jardin";
    public static final String TAG = "GardenEntity";

    public static final String COLUMN_NAME = "nombre";
    public static final String COLUMN_ADDRESS = "direccion";
    public static final String COLUMN_LOCATION = "id_localidad";
    public static final String COLUMN_TAG = "id_tipo";

    public static final String[] ALL_COLUMNS = {
        DbHelper._ID,
        COLUMN_NAME,
        COLUMN_ADDRESS,
        COLUMN_LOCATION,
        COLUMN_TAG
    };

    private long mId;
    private String mName;
    private String mAddress;
    private long mLocation;
    private long mTag;

    private List<String> mErrors;

    public Garden() {}

    public Garden(long id, String name, String address, long locationId, long tagId) {
        this.mId = id;
        this.mName = name;
        this.mAddress = address;
        this.mLocation = locationId;
        this.mTag = tagId;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Garden.COLUMN_NAME, mName);
        values.put(Garden.COLUMN_ADDRESS, mAddress);
        values.put(Garden.COLUMN_LOCATION, mLocation);
        values.put(Garden.COLUMN_TAG, mTag);

        return values;
    }

    public boolean validar() {
        this.mErrors = new ArrayList<>();
        if (this.mName.isEmpty()) mErrors.add("El nombre está vacío");
        if (this.mAddress.isEmpty()) mErrors.add("La dirección está vacía");
        if (this.mTag < 0) mErrors.add("Crea una etiqueta para el jardín");
        if (this.mLocation < 0) mErrors.add("Crea una ubicación para el jardín");

        return this.mErrors.size() == 0;
    }

    public static Garden fromCursor(Cursor c) {
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        Garden garden = new Garden(
            c.getLong(0),
            c.getString(1),
            c.getString(2),
            c.getLong(3),
            c.getLong(4)
        );
        c.close();
        return garden;
    }

    public static List<Garden> manyFromCursor(Cursor c) {
        List<Garden> gardenList = new ArrayList<Garden>();

        while (c.moveToNext()) {
            Garden garden = new Garden(
                c.getLong(0),
                c.getString(1),
                c.getString(2),
                c.getLong(3),
                c.getLong(4)
            );
            gardenList.add(garden);
        }

        c.close();
        return gardenList;
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

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public long getLocation() {
        return mLocation;
    }

    public void setLocation(long location) {
        mLocation = location;
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
