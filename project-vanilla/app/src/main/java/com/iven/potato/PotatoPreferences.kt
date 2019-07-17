package com.iven.potato

import android.app.Activity
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import org.jetbrains.annotations.NotNull
import java.util.concurrent.TimeUnit

class PotatoPreferences : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.potato_battery_settings)
        fragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
    }

    override fun onBackPressed() {
        finishAndRemoveTask()
        super.onBackPressed()
    }

    class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.potato_preferences)

            //set refresh time preference value
            if (context != null) {
                val editTextPreference = preferenceManager.findPreference(context!!.getString(R.string.title_time))
                editTextPreference?.summary = TimeUnit.MILLISECONDS.toSeconds(getRefreshTime(context)).toString()
            }
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
            if (context != null && key == context?.resources?.getString(R.string.title_gradient) || key == context?.resources?.getString(
                    R.string.title_time
                )) {

                val intent = Intent(
                    WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER
                )
                intent.putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(context!!, PotatoBatteryLP::class.java)
                )

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NO_ANIMATION)

                startActivity(intent)
            }
        }

        override fun onResume() {
            super.onResume()
            if (preferenceManager != null)
                preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            if (preferenceManager != null)
                preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }
    }

    companion object {
        //is gradient enabled?
        fun isGradientEnabled(@NotNull context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.resources.getString(R.string.title_gradient), true)
        }

        //get refresh time
        fun getRefreshTime(@NotNull context: Context): Long {
            val refreshTime = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.resources.getString(R.string.title_time), 1.toString())
            return TimeUnit.SECONDS.toMillis(refreshTime!!.toLong())
        }
    }
}