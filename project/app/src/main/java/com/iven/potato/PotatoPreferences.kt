package com.iven.potato

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import java.util.concurrent.TimeUnit

class PotatoPreferences : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.potato_battery_settings)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
    }

    override fun onBackPressed() {
        finishAndRemoveTask()
        super.onBackPressed()
    }

    class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.potato_preferences, rootKey)

            //set inputType = numbers
            if (context != null) {
                val editTextPreference =
                    preferenceManager.findPreference<EditTextPreference>(context!!.getString(R.string.title_time))
                editTextPreference!!.setOnBindEditTextListener {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                }
            }
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
            if (context != null && key == context?.resources?.getString(R.string.title_gradient) || key == context?.resources?.getString(
                    R.string.title_time
                )
            ) {
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
        fun isGradientEnabled(@NonNull context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.resources.getString(R.string.title_gradient), true)
        }

        //get refresh time
        fun getRefreshTime(@NonNull context: Context): Long {
            val refreshTime = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.resources.getString(R.string.title_time), 1.toString())
            return TimeUnit.SECONDS.toMillis(refreshTime!!.toLong())
        }
    }
}
