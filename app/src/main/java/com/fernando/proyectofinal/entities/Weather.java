package com.fernando.proyectofinal.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.fernando.proyectofinal.db.DbHelper;

import java.util.ArrayList;
import java.util.List;

public final class Weather extends Entity {

    public static final String TABLE_NAME = "clima";
    public static final String TAG = "WeatherEntity";

    public static final String COLUMN_DATE = "fecha";
    public static final String COLUMN_TEMP_MIN = "temp_min";
    public static final String COLUMN_TEMP_MAX = "temp_max";
    public static final String COLUMN_HUMIDITY = "humedad";
    public static final String COLUMN_RAIN = "prob_lluvia";
    public static final String COLUMN_LOCATION = "id_localizacion";
    public static final String COLUMN_TAG = "id_tag";

    public static final String[] ALL_COLUMNS = {
        DbHelper._ID,
        COLUMN_DATE,
        COLUMN_TEMP_MIN ,
        COLUMN_TEMP_MAX,
        COLUMN_HUMIDITY,
        COLUMN_RAIN,
        COLUMN_LOCATION,
        COLUMN_TAG
    };

    private long mId;
    private String mDate;
    private double mMinTemp;
    private double mMaxTemp;
    private double mHumidity;
    private double mRain;
    private long mLocation;
    private long mTag;

    private List<String> mErrors;

    public Weather() {}

    private Weather(long id, String date, double minTemp, double maxTemp, double humidity, double rain, long location, long tag) {
        this.mId = id;
        this.mDate = date;
        this.mMinTemp = minTemp;
        this.mMaxTemp = maxTemp;
        this.mHumidity = humidity;
        this.mRain = rain;
        this.mLocation = location;
        this.mTag = tag;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Weather.COLUMN_DATE, mDate);
        values.put(Weather.COLUMN_TEMP_MIN, mMinTemp);
        values.put(Weather.COLUMN_TEMP_MAX, mMaxTemp);
        values.put(Weather.COLUMN_HUMIDITY, mHumidity);
        values.put(Weather.COLUMN_RAIN, mRain);
        values.put(Weather.COLUMN_LOCATION, mLocation);
        values.put(Weather.COLUMN_TAG, mTag);

        return values;
    }

    public boolean validar() {
        this.mErrors = new ArrayList<>();
        if (this.mDate == null) mErrors.add("La fecha está vacía");
        if (this.mTag < 0) mErrors.add("Crea un tipo para el clima");
        if (this.mLocation < 0) mErrors.add("Crea una ubicación para el clima");

        return this.mErrors.size() == 0;
    }


    public static Weather fromCursor(Cursor c) {
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        Weather weather = new Weather(
            c.getLong(0),
            c.getString(1),
            c.getDouble(2),
            c.getDouble(3),
            c.getDouble(4),
            c.getDouble(5),
            c.getLong(6),
            c.getLong(7)
        );
        c.close();
        return weather;
    }

    public static List<Weather> manyFromCursor(Cursor c) {
        List<Weather> weatherList = new ArrayList<Weather>();

        while (c.moveToNext()) {
            Weather weather = new Weather(
                c.getLong(0),
                c.getString(1),
                c.getDouble(2),
                c.getDouble(3),
                c.getDouble(4),
                c.getDouble(5),
                c.getLong(6),
                c.getLong(7)
            );
            weatherList.add(weather);
        }

        c.close();
        return weatherList;
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

    public double getMinTemp() {
        return mMinTemp;
    }

    public void setMinTemp(double minTemp) {
        mMinTemp = minTemp;
    }

    public double getMaxTemp() {
        return mMaxTemp;
    }

    public void setMaxTemp(double maxTemp) {
        mMaxTemp = maxTemp;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public double getRain() {
        return mRain;
    }

    public void setRain(double rain) {
        mRain = rain;
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
