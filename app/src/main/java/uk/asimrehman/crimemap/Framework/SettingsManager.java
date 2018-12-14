package uk.asimrehman.crimemap.Framework;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import uk.asimrehman.crimemap.R;

/**
 * Created by admin on 11/01/2017.
 */

public class SettingsManager {


    AppCompatActivity activity;
    public String PoliceAPILastUpdatedKey;
    public String CacheEnabledKey;

    public SettingsManager(AppCompatActivity activity)
    {
        this.activity = activity;
        PoliceAPILastUpdatedKey = activity.getString(R.string.LastUpdated);
        CacheEnabledKey = activity.getString(R.string.CacheEnabled);
    }

    public SharedPreferences GetSharedPrefs()
    {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public void UpdateSharedPreferences(SharedPreferences.Editor editor)
    {
        editor.commit();
    }


    public static boolean IsCachingEnabled(AppCompatActivity appCompatActivity)
    {
        SharedPreferences sharedPreferences = appCompatActivity.getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(appCompatActivity.getString(R.string.CacheEnabled), true);
    }
}
