package com.fernando.proyectofinal.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.fernando.proyectofinal.LoginActivity;
import com.fernando.proyectofinal.db.DatabaseManager;
import com.fernando.proyectofinal.db.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class User extends Entity {

    public static final String TABLE_NAME = "usuario";
    public static final String TAG = "UserEntity";

    public static final String USER_PREFS_KEY = "USER_DATA";
    public static final String USERNAME = "USERNAME";
    public static final String LEVEL = "LEVEL";

    public static final String DEFAULT_USERNAME = "Anónimo";
    public static final int USER_NORMAL = 0;
    public static final int USER_ADMIN = 1;

    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_LEVEL = "nivel";

    public static final String[] ALL_COLUMNS = {
        DbHelper._ID,
        COLUMN_USERNAME,
        COLUMN_PASSWORD,
        COLUMN_LEVEL
    };

    private long mId;
    private String mUsername;
    private String mPassword;
    private int mLevel;

    private List<String> mErrors;

    public User() {}

    public User(String username, String password, int level) {
        mUsername = username;
        mPassword = password;
        mLevel = level;
    }

    public User(long id, String username, String password, int level) {
        this(username, password, level);
        mId = id;
    }

    public boolean validar() {
        this.mErrors = new ArrayList<>();
        if (this.mUsername.isEmpty()) mErrors.add("El nombre de usuario está vacío");
        if (this.mPassword.isEmpty() || this.mPassword.length() < 8) {
            mErrors.add("La contraseña debe tener 8 caracteres");
        }

        return this.mErrors.size() == 0;
    }

    public boolean authenticate(DatabaseManager dbManager, String action) {
        Cursor userWithUsername = dbManager.findUserByName(this.getUsername());
        boolean userExists = userWithUsername.moveToFirst();

        switch (action) {
            case LoginActivity.LOGIN:
                if (userExists) {
                    User dbUser = User.fromCursor(userWithUsername);
                    this.mId = dbUser.getId();
                    this.mLevel = dbUser.getLevel();
                    boolean passwordMatch = dbUser.getPassword().equals(this.getPassword());

                    if (!passwordMatch) mErrors.add("La contraseña es incorrecta");
                } else {
                    mErrors.add("El usuario no existe");
                }
                break;
            case LoginActivity.REGISTER:
                if (!userExists) {
                    long resultId = dbManager.insertEntity(TABLE_NAME, this.getContentValues());
                    this.setId(resultId);
                } else {
                    mErrors.add("El nombre de usuario ya existe");
                }
                break;
            default: throw new UnsupportedOperationException();
        }

        userWithUsername.close();
        return this.mErrors.size() == 0;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(User.COLUMN_USERNAME, mUsername);
        values.put(User.COLUMN_PASSWORD, mPassword);
        values.put(User.COLUMN_LEVEL, mLevel);

        return values;
    }

    public static User fromCursor(Cursor c) {
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }

        User user = new User(
            c.getLong(0),
            c.getString(1),
            c.getString(2),
            c.getInt(3)
        );
        c.close();
        return user;
    }

    public static List<User> manyFromCursor(Cursor c) {
        List<User> userList = new ArrayList<User>();

        while (c.moveToNext()) {
            User user = new User(
                c.getLong(0),
                c.getString(1),
                c.getString(2),
                c.getInt(3)
            );
            userList.add(user);
        }

        c.close();
        return userList;
    }

    // region Get/Set
    public void setId(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int level) {
        mLevel = level;
    }

    public List<String> getErrors() {
        return mErrors;
    }
    // endregion
}