package com.fernando.proyectofinal.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fernando.proyectofinal.db.DbHelper;

import java.util.ArrayList;
import java.util.List;

public final class Tag extends Entity {

    public static final String TABLE_NAME = "tag";
    public static final String TAG = "TagEntity";

    public static final String COLUMN_NAME = "nombre";
    public static final String COLUMN_CATEGORY = "categoria";

    public static final String[] ALL_COLUMNS = {
        DbHelper._ID,
        COLUMN_NAME,
        COLUMN_CATEGORY,
    };

    private long mId;
    private String mName;
    private int mCategory;

    public Tag() {}

    private Tag(long id, String name, int category) {
        this.mId = id;
        this.mName = name;
        this.mCategory = category;
    }

    @NonNull
    @Override
    public String toString() {
        return mName;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Tag.COLUMN_NAME, mName);
        values.put(Tag.COLUMN_CATEGORY, mCategory);

        return values;
    }

    public static Tag fromCursor(Cursor c) {
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        Tag tag = new Tag(
            c.getLong(0),
            c.getString(1),
            c.getInt(2)
        );

        c.close();
        return tag;
    }

    public static List<Tag> manyFromCursor(Cursor c) {
        List<Tag> tagList = new ArrayList<Tag>();

        while (c.moveToNext()) {
            Tag tag = new Tag(
                c.getLong(0),
                c.getString(1),
                c.getInt(2)
            );
            tagList.add(tag);
        }

        c.close();
        return tagList;
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

    public int getCategory() {
        return mCategory;
    }

    public void setCategory(int category) {
        mCategory = category;
    }
    // endregion
}
