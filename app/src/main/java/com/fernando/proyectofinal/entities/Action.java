package com.fernando.proyectofinal.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.fernando.proyectofinal.db.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class Action extends Entity {

    public static final String TABLE_NAME = "logs";
    public static final String TAG = "LogEntity";

    public static final String COLUMN_DATE = "fecha";
    public static final String COLUMN_ACTION = "accion";
    public static final String COLUMN_RESOURCE = "entidad";
    public static final String COLUMN_USER = "id_usuario";

    public static final String[] ALL_COLUMNS = {
        DbHelper._ID,
        COLUMN_DATE,
        COLUMN_ACTION,
        COLUMN_RESOURCE,
        COLUMN_USER
    };

    private long mId;
    private String mDate;
    private int mActionType;
    private int mResource;
    private long mUser;

    private List<String> mErrors;

    public Action() {}

    public Action(String date, int actionType, int resource, long user) {
        mDate = date;
        mActionType = actionType;
        mResource = resource;
        mUser = user;
    }

    public Action(long id, String date, int actionType, int resource, long user) {
        mId = id;
        mDate = date;
        mActionType = actionType;
        mResource = resource;
        mUser = user;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Action.COLUMN_DATE, mDate);
        values.put(Action.COLUMN_ACTION, mActionType);
        values.put(Action.COLUMN_RESOURCE, mResource);
        values.put(Action.COLUMN_USER, mUser);

        return values;
    }

    public static Action fromCursor(Cursor c) {
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        Action action = new Action(
                c.getLong(0),
                c.getString(1),
                c.getInt(2),
                c.getInt(3),
                c.getLong(4)
        );
        c.close();
        return action;
    }

    public static List<Action> manyFromCursor(Cursor c) {
        List<Action> actionList = new ArrayList<Action>();

        while (c.moveToNext()) {
            Action action = new Action(
                c.getLong(0),
                c.getString(1),
                c.getInt(2),
                c.getInt(3),
                c.getLong(4)
            );
            actionList.add(action);
        }

        c.close();
        return actionList;
    }

    // region Get/Set
    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public int getActionType() {
        return mActionType;
    }

    public void setActionType(int actionType) {
        mActionType = actionType;
    }

    public int getResource() {
        return mResource;
    }

    public void setResource(int resource) {
        mResource = resource;
    }

    public long getUser() {
        return mUser;
    }

    public void setUser(long user) {
        mUser = user;
    }
    // endregion
}
