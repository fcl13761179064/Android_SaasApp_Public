package com.ayla.hotelsaas.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtils {
    public final static String SP_NAME = "config";
    private static SharedPreferences sp;

    public static void saveBoolean(Context context, String key, boolean value)
    {

        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);

        sp.edit().putBoolean(key, value).commit();
    }

    public static void saveInt(Context context, String key, int value)
    {

        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        sp.edit().putInt(key, value).commit();
    }

    public static void saveLong(Context context, String key, long value)
    {

        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        sp.edit().putLong(key, value).commit();
    }

    public static long getLong(Context context, String key, int defValue)
    {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);

        return sp.getLong(key, defValue);
    }

    public static void saveString(Context context, String key, String value)
    {

        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);

        sp.edit().putString(key, value).commit();
    }

    public static int getInt(Context context, String key, int defValue)
    {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);

        return sp.getInt(key, defValue);
    }

    public static void saveFloat(Context context, String key, double value)
    {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        sp.edit().putFloat(key, (float) value).commit();
    }

    public static float getFloat(Context context, String key)
    {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getFloat(key, 0);
    }

    public static String getString(Context context, String key, String defValue)
    {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);

        return sp.getString(key, defValue);

    }

    public static boolean getBoolean(Context context, String key,
                                     boolean defValue)
    {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);

        return sp.getBoolean(key, defValue);
    }

    public static void remove(Context context, String key)
    {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        sp.edit().remove(key).commit();
    }

}
