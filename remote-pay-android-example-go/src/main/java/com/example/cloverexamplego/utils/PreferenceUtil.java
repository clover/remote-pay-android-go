package com.example.cloverexamplego.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {
    private static final String PREF_NAME = "Preferences";

    private static SharedPreferences getSharedPref(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveString(Context context, String key, String value) {
        getSharedPref(context).edit().putString(key, value).commit();
    }

    public static void saveInt(Context context, String key, int value) {
        getSharedPref(context).edit().putInt(key, value).commit();
    }

    public static void saveFloat(Context context, String key, float value) {
        getSharedPref(context).edit().putFloat(key, value).commit();
    }

    public static void saveLong(Context context, String key, long value) {
        getSharedPref(context).edit().putLong(key, value).commit();
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        getSharedPref(context).edit().putBoolean(key, value).commit();
    }

    public static void remove(Context context, String key) {
        getSharedPref(context).edit().remove(key).commit();
    }

    public static boolean containsKey(Context context, String key) {
        return getSharedPref(context).contains(key);
    }

    public static String getStringValue(Context context, String key) {
        return getSharedPref(context).getString(key, null);
    }

    public static int getIntValue(Context context, String key) {
        return getSharedPref(context).getInt(key, -1);
    }

    public static float getFloatValue(Context context, String key) {
        return getSharedPref(context).getFloat(key, -1);
    }

    public static long getLongValue(Context context, String key) {
        return getSharedPref(context).getLong(key, -1);
    }

    public static boolean getBooleanValue(Context context, String key) {
        return getSharedPref(context).getBoolean(key, false);
    }

    public static void clearAll(Context context) {
        getSharedPref(context).edit().clear().commit();
    }

}
