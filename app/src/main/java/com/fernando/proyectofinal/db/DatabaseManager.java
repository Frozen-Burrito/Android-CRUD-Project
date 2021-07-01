package com.fernando.proyectofinal.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fernando.proyectofinal.ActionType;
import com.fernando.proyectofinal.ResourceType;
import com.fernando.proyectofinal.entities.Action;
import com.fernando.proyectofinal.entities.Garden;
import com.fernando.proyectofinal.entities.Item;
import com.fernando.proyectofinal.entities.Location;
import com.fernando.proyectofinal.entities.Person;
import com.fernando.proyectofinal.entities.User;
import com.fernando.proyectofinal.entities.Weather;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseManager {

    public static final String TAG = "DatabaseManager";

    private static DatabaseManager sInstance = null;

    private static long sUserId = -1;
    private DbHelper mDbHelper;

    private static final List<String> excludedTables = Arrays.asList(Action.TABLE_NAME, User.TABLE_NAME);

    public DatabaseManager(Context context) {
        this.mDbHelper = new DbHelper(context);
    }

    public static DatabaseManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseManager(context);
        }

        return sInstance;
    }

    public static void setUserId(long id) {
        if (id < -1) throw new IllegalArgumentException();

        sUserId = id;
    }

    public long insertEntity(String tableName, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long resultId = db.insert(tableName,  null, values);

        logAction(tableName, ActionType.CREATE.getValue(), sUserId);

        return resultId;
    }

    public Cursor findById(String tableName, long id) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tableName + " WHERE id = " + id + " LIMIT 1";

        logAction(tableName, ActionType.READ.getValue(), sUserId);

        return db.rawQuery(selectQuery, null);
    }

    public Cursor findMany(String tableName, String[] projection) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        logAction(tableName, ActionType.READ.getValue(), sUserId);

        return db.query(
            tableName,
            projection,
            null,
            null,
            null,
            null,
            null
        );
    }

    public Cursor findMany(String tableName, String[] projection, String selection, String... selectionArgs) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        logAction(tableName, ActionType.READ.getValue(), sUserId);

        return db.query(
            tableName,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        );
    }

    public Cursor findUserByName(String username) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + User.TABLE_NAME + " WHERE " + User.COLUMN_USERNAME + " = '" + username + "' LIMIT 1";

        return db.rawQuery(selectQuery, null);
    }

    public long editEntity(String tableName, long entityId, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = DbHelper._ID + " = ?";
        String[] selectionArgs = { Long.toString(entityId) };

        long resultId = db.update(
            tableName,
            values,
            selection,
            selectionArgs
        );

        logAction(tableName, ActionType.EDIT.getValue(), sUserId);

        return resultId;
    }

    public void deleteEntity(String tableName, long id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = DbHelper._ID +" = ?";
        String[] selectionArgs = { Long.toString(id) };

        long resultId = db.delete(tableName, selection, selectionArgs);
        logAction(tableName, ActionType.DELETE.getValue(), sUserId);
    }

    private void logAction(String tableName, int actionType, long userId) {

        if (excludedTables.contains(tableName)) return;

        Date currentTime = Calendar.getInstance().getTime();
        String dateString = currentTime.toString();
        ResourceType resourceType;

        switch (tableName){
            case Person.TABLE_NAME:
                resourceType = ResourceType.PERSON;
                break;
            case Garden.TABLE_NAME:
                resourceType = ResourceType.GARDEN;
                break;
            case Weather.TABLE_NAME:
                resourceType = ResourceType.WEATHER;
                break;
            case Location.TABLE_NAME:
                resourceType = ResourceType.LOCATION;
                break;
            case Item.TABLE_NAME:
                resourceType = ResourceType.ARTICLE;
                break;
            default:
                resourceType = ResourceType.TAG;
        }

        Action action = new Action(dateString, actionType, resourceType.getValue(), userId);
        long actionId = this.insertEntity(Action.TABLE_NAME, action.getContentValues());

        if (actionId >= 0) Log.i(TAG, "ACCION REGISTRADA: " + tableName + ", hora: " + dateString);
    }
}
