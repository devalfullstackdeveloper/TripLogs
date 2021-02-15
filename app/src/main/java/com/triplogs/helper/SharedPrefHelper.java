package com.triplogs.helper;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefHelper {

    // Shared pref file name
    public static final String PREF_NAME = "com.triplogs";

    public static final String PREF_AUTH_TOKEN= "auth_token";
    public static final String PREF_IS_CHECK = "pref_is_check";
    public static final String CONFIG_RESPONSE_BODY = "pref_config_response_body";
    public static final String API_CALL_DURATION = "pref_apiCallDuration";
    public static final String TRIP_ID = "pref_trip_id";


    private static com.triplogs.helper.SharedPrefHelper instance;
    // Shared Preferences
    public SharedPreferences sharedPreferences;
    // Editor for Shared preferences
    private SharedPreferences.Editor editor;


    /**
     * SharedPrefHelper constructor set in Myapplication class
     *
     * @param context
     */
    public SharedPrefHelper(Context context) {
        instance = this;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static  com.triplogs.helper.SharedPrefHelper getPrefsHelper() {
        return instance;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    /**
     * for clear sharedPreference data
     *
     * @param key string value
     */
    public void delete(String key) {
        if (sharedPreferences.contains(key)) {
            if (editor == null) {
                editor = sharedPreferences.edit();
            }
            editor.remove(key).commit();
        }
    }

    /**
     * for set any data from services or app
     *
     * @param key   string
     * @param value any object
     */
    public void setData(String key, Object value) {

        editor = sharedPreferences.edit();
        delete(key);
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        } else if (value != null) {
            throw new RuntimeException("Attempting to save non-primitive preference");
        }
        editor.apply();
        editor.commit();
    }

    @SuppressWarnings("unchecked")
    public <T> T getPref(String key) {
        return (T) sharedPreferences.getAll().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPref(String key, T defValue) {
        T returnValue = (T) sharedPreferences.getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }

    public boolean isPrefExists(String key) {
        return sharedPreferences.contains(key);
    }

}
