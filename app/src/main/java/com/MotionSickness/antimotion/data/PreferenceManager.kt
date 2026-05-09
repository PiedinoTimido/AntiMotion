package com.MotionSickness.antimotion.data

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("anti_motion_prefs", Context.MODE_PRIVATE)

    var isFeatureEnabled: Boolean
        get() = prefs.getBoolean("feature_enabled", false)
        set(value) = prefs.edit().putBoolean("feature_enabled", value).apply()

    var isAutoStartEnabled: Boolean
        get() = prefs.getBoolean("auto_start_enabled", true)
        set(value) = prefs.edit().putBoolean("auto_start_enabled", value).apply()
}
