package com.fernando.proyectofinal.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fernando.proyectofinal.db.DbHelper;

import java.util.ArrayList;
import java.util.List;

public final class Location extends Entity {

    public static final String TABLE_NAME = "localizacion";
    public static final String TAG = "LocationEntity";

    public static final String COLUMN_PAIS = "codigo_pais";
    public static final String COLUMN_ESTADO = "estado";
    public static final String COLUMN_CIUDAD = "ciudad";

    public static final String[] ALL_COLUMNS = {
        DbHelper._ID,
        COLUMN_PAIS,
        COLUMN_ESTADO,
        COLUMN_CIUDAD
    };

    private long mId;
    private String mPais;
    private String mEstado;
    private String mCiudad;

    public Location() {}

    private Location(long id, String pais, String estado, String ciudad) {
        this.mId = id;
        this.mPais = pais;
        this.mEstado = estado;
        this.mCiudad = ciudad;
    }

    @NonNull
    @Override
    public String toString() {
        return mCiudad + ", " + mEstado + ", " + mPais;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Location.COLUMN_PAIS, mPais);
        values.put(Location.COLUMN_ESTADO, mEstado);
        values.put(Location.COLUMN_CIUDAD, mCiudad);

        return values;
    }

    public static Location fromCursor(Cursor c) {
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        Location location = new Location(
            c.getLong(0),
            c.getString(1),
            c.getString(2),
            c.getString(3)
        );
        c.close();
        return location;
    }

    public static List<Location> manyFromCursor(Cursor c) {
        List<Location> locationList = new ArrayList<Location>();

        while (c.moveToNext()) {
            Location newLocation = new Location(
                c.getInt(0),
                c.getString(1),
                c.getString(2),
                c.getString(3)
            );
            locationList.add(newLocation);
        }

        c.close();
        return locationList;
    }

    // region Get/Set
    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getPais() {
        return mPais;
    }

    public void setPais(String pais) {
        mPais = pais;
    }

    public String getEstado() {
        return mEstado;
    }

    public void setEstado(String estado) {
        mEstado = estado;
    }

    public String getCiudad() {
        return mCiudad;
    }

    public void setCiudad(String ciudad) {
        mCiudad = ciudad;
    }
    // endregion
}
