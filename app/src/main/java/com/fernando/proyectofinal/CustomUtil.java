package com.fernando.proyectofinal;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.fernando.proyectofinal.db.DatabaseManager;
import com.fernando.proyectofinal.db.DbHelper;
import com.fernando.proyectofinal.entities.Location;
import com.fernando.proyectofinal.entities.Tag;
import com.fernando.proyectofinal.entities.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class CustomUtil {

    private static final String TAG = "CustomUtil";

    public static int getTagPositionById(List<Tag> tagList, long tagId) {
        for (int i = 0; i < tagList.size(); i++) {
            if (tagList.get(i).getId() == tagId) {
                Log.i(TAG, "???: " + tagList.get(i).getId() + " = " + tagId);
                return i;
            }
        }

        return -1;
    }

    public static int getLocationPositionById(List<Location> locationList, long locationId) {
        for (int i = 0; i < locationList.size(); i++) {
            if (locationList.get(i).getId() == locationId) {
                return i;
            }
        }

        return -1;
    }

    public static String padLeftZeros(String originalString, int length) {
        if (originalString.length() >= length) return originalString;

        return String.format("%1$" + length + "s", originalString).replace(' ', '0');
    }

    public static String getTypeString(List<Tag> mTags, long id) {
        for(Tag weatherType : mTags) {
            if (weatherType.getId() == id) {
                Log.i(TAG, String.valueOf(weatherType.getCategory()));
                return weatherType.toString();
            }
        }

        return "";
    }

    public static double doubleParseDefault(String s) {
        if (s.isEmpty()) return 0.0;

        return Double.parseDouble(s);
    }

    public static User getUserFromPrefs(Context context) {
        User authUser = new User();
        SharedPreferences sharedPref = context.getSharedPreferences(User.USER_PREFS_KEY, Context.MODE_PRIVATE);
        authUser.setUsername(sharedPref.getString(User.USERNAME, User.DEFAULT_USERNAME));
        authUser.setLevel(sharedPref.getInt(User.LEVEL, -1));
        authUser.setId(sharedPref.getLong(DbHelper._ID, -1));

        return authUser;
    }

    public static void logoutUser(AppCompatActivity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(User.USER_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(User.USERNAME, User.DEFAULT_USERNAME);
        editor.putInt(User.LEVEL, -1);
        editor.putLong(DbHelper._ID, -1);
        editor.apply();

        DatabaseManager.setUserId(-1);
    }

    public static void storeUserInPrefs(User user, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(User.USER_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(User.USERNAME, user.getUsername());
        editor.putInt(User.LEVEL, user.getLevel());
        editor.putLong(DbHelper._ID, user.getId());
        editor.apply();
    }

    public static HashMap<Long, String> getUsernameHashMap(List<User> users) {
        HashMap<Long, String> usernameMap = new HashMap<Long, String>();

        usernameMap.put((long) -1, User.DEFAULT_USERNAME);
        for (User user : users) {
            usernameMap.put(user.getId(), user.getUsername());
        }

        return usernameMap;
    }
}
