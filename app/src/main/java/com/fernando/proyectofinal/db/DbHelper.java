package com.fernando.proyectofinal.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fernando.proyectofinal.entities.Action;
import com.fernando.proyectofinal.entities.Garden;
import com.fernando.proyectofinal.entities.Inventory;
import com.fernando.proyectofinal.entities.Item;
import com.fernando.proyectofinal.entities.Location;
import com.fernando.proyectofinal.entities.Person;
import com.fernando.proyectofinal.entities.Tag;
import com.fernando.proyectofinal.entities.User;
import com.fernando.proyectofinal.entities.Weather;

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 12;
    public static final String DATABASE_NAME = "plantas.db";
    public static final String TAG = "DbHelper";

    public static final String _ID = "id";

    // region Init Queries
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String PRIMARY_KEY = " INTEGER PRIMARY KEY AUTOINCREMENT,";

    private static final String SQL_CREATE_TAG_QUERY =
        CREATE_TABLE + Tag.TABLE_NAME + " (" +
        _ID  + PRIMARY_KEY +
        Tag.COLUMN_NAME + " TEXT," +
        Tag.COLUMN_CATEGORY + " INTEGER)";

    private static final String SQL_CREATE_LOCATION_QUERY =
        CREATE_TABLE + Location.TABLE_NAME + " (" +
        _ID + PRIMARY_KEY +
        Location.COLUMN_PAIS + " TEXT," +
        Location.COLUMN_ESTADO + " TEXT," +
        Location.COLUMN_CIUDAD + " TEXT)";

    private static final String SQL_CREATE_GARDEN_QUERY =
        CREATE_TABLE + Garden.TABLE_NAME + " (" +
        _ID + PRIMARY_KEY +
        Garden.COLUMN_NAME + " TEXT," +
        Garden.COLUMN_ADDRESS + " TEXT," +
        Garden.COLUMN_LOCATION + " INTEGER," +
        Garden.COLUMN_TAG + " INTEGER," +
        " FOREIGN KEY ("+ Garden.COLUMN_LOCATION +") REFERENCES "+ Location.TABLE_NAME +"("+ _ID +")," +
        " FOREIGN KEY ("+ Garden.COLUMN_TAG +") REFERENCES "+ Tag.TABLE_NAME +"("+ _ID +"))";

    private static final String SQL_CREATE_PERSON_QUERY =
        CREATE_TABLE + Person.TABLE_NAME + " (" +
        _ID + PRIMARY_KEY +
        Person.COLUMN_NAME + " TEXT," +
        Person.COLUMN_LASTNAME + " TEXT," +
        Person.COLUMN_PHONE + " TEXT," +
        Person.COLUMN_EMAIL + " TEXT," +
        Person.COLUMN_GARDEN + " INTEGER," +
        Person.COLUMN_TAG + " INTEGER," +
        " FOREIGN KEY ("+ Person.COLUMN_GARDEN +") REFERENCES "+ Garden.TABLE_NAME +"("+ _ID +")," +
        " FOREIGN KEY ("+ Person.COLUMN_TAG +") REFERENCES "+ Tag.TABLE_NAME +"("+ _ID +"))";

    private static final String SQL_CREATE_WEATHER_QUERY =
        CREATE_TABLE + Weather.TABLE_NAME + " (" +
        _ID + PRIMARY_KEY +
        Weather.COLUMN_DATE + " TEXT," +
        Weather.COLUMN_TEMP_MIN + " REAL," +
        Weather.COLUMN_TEMP_MAX + " REAL," +
        Weather.COLUMN_HUMIDITY + " REAL," +
        Weather.COLUMN_RAIN + " REAL," +
        Weather.COLUMN_LOCATION + " INTEGER," +
        Weather.COLUMN_TAG + " INTEGER," +
        " FOREIGN KEY ("+ Weather.COLUMN_LOCATION +") REFERENCES "+ Location.TABLE_NAME +"("+ _ID +")," +
        " FOREIGN KEY ("+ Weather.COLUMN_TAG +") REFERENCES "+ Tag.TABLE_NAME +"("+ _ID +"))";

    private static final String SQL_CREATE_ITEM_QUERY =
        CREATE_TABLE + Item.TABLE_NAME + " (" +
        _ID + PRIMARY_KEY +
        Item.COLUMN_NAME + " TEXT," +
        Item.COLUMN_DESCRIPTION + " TEXT," +
        Item.COLUMN_PRICE + " REAL," +
        Item.COLUMN_TAG + " INTEGER," +
        " FOREIGN KEY ("+ Item.COLUMN_TAG +") REFERENCES "+ Tag.TABLE_NAME +"("+ _ID +"))";

    private static final String SQL_CREATE_INVENTORY_QUERY =
        CREATE_TABLE + Inventory.TABLE_NAME + " (" +
        _ID + PRIMARY_KEY +
        Inventory.COLUMN_CAPACITY + " INTEGER," +
        Inventory.COLUMN_STORAGE + " INTEGER," +
        Inventory.COLUMN_ARTICLE + " INTEGER," +
        " FOREIGN KEY ("+ Inventory.COLUMN_STORAGE +") REFERENCES "+ Garden.TABLE_NAME +"("+ _ID +")," +
        " FOREIGN KEY ("+ Inventory.COLUMN_ARTICLE +") REFERENCES "+ Item.TABLE_NAME +"("+ _ID +"))";

    private static final String SQL_CREATE_USER_QUERY =
        CREATE_TABLE + User.TABLE_NAME + " (" +
        _ID + PRIMARY_KEY +
        User.COLUMN_USERNAME + " TEXT," +
        User.COLUMN_PASSWORD + " TEXT," +
        User. COLUMN_LEVEL+ " INTEGER)";

    private static final String SQL_CREATE_ACTION_QUERY =
        CREATE_TABLE + Action.TABLE_NAME + " (" +
        _ID + PRIMARY_KEY +
        Action.COLUMN_DATE + " TEXT," +
        Action.COLUMN_ACTION + " INTEGER," +
        Action.COLUMN_RESOURCE + " INTEGER," +
        Action.COLUMN_USER + " INTEGER," +
        " FOREIGN KEY ("+ Action.COLUMN_USER +") REFERENCES "+ User.TABLE_NAME +"("+ _ID +"))";

    private static final String SQL_DROP_PERSON = DROP_TABLE + Person.TABLE_NAME;
    private static final String SQL_DROP_GARDEN = DROP_TABLE + Garden.TABLE_NAME;
    private static final String SQL_DROP_WEATHER = DROP_TABLE + Weather.TABLE_NAME;
    private static final String SQL_DROP_ITEM = DROP_TABLE + Item.TABLE_NAME;
    private static final String SQL_DROP_INVENTORY = DROP_TABLE + Inventory.TABLE_NAME;
    private static final String SQL_DROP_TIPO = DROP_TABLE + Tag.TABLE_NAME;
    private static final String SQL_DROP_LOCATION = DROP_TABLE + Location.TABLE_NAME;
    private static final String SQL_DROP_ACTION= DROP_TABLE + Action.TABLE_NAME;
    private static final String SQL_DROP_USER = DROP_TABLE + User.TABLE_NAME;
    //endregion

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TAG_QUERY);
        db.execSQL(SQL_CREATE_LOCATION_QUERY);
        db.execSQL(SQL_CREATE_GARDEN_QUERY);
        db.execSQL(SQL_CREATE_PERSON_QUERY);
        db.execSQL(SQL_CREATE_WEATHER_QUERY);
        db.execSQL(SQL_CREATE_ITEM_QUERY);
        db.execSQL(SQL_CREATE_INVENTORY_QUERY);
        db.execSQL(SQL_CREATE_USER_QUERY);
        db.execSQL(SQL_CREATE_ACTION_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Updating the database");
        db.execSQL(SQL_DROP_ACTION);
        db.execSQL(SQL_DROP_USER);
        db.execSQL(SQL_DROP_PERSON);
        db.execSQL(SQL_DROP_GARDEN);
        db.execSQL(SQL_DROP_WEATHER);
        db.execSQL(SQL_DROP_ITEM);
        db.execSQL(SQL_DROP_INVENTORY);
        db.execSQL(SQL_DROP_TIPO);
        db.execSQL(SQL_DROP_LOCATION);
        onCreate(db);
    }
}
