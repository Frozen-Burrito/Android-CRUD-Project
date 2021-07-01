package com.fernando.proyectofinal.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.fernando.proyectofinal.db.DbHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class Entity {

    public static final String TABLE_NAME = "entity_default";
    public static final String[] ALL_COLUMNS = { DbHelper._ID };

    protected long mId;

    public abstract ContentValues getContentValues();
}
