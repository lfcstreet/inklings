package com.example.inklings

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("inklings_settings", Context.MODE_PRIVATE)

    var isTypewriterSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var timerDurationMinutes: Int
        get() = prefs.getInt(KEY_TIMER_DURATION, 30)
        set(value) = prefs.edit().putInt(KEY_TIMER_DURATION, value).apply()

    companion object {
        private const val KEY_SOUND_ENABLED = "typewriter_sound_enabled"
        private const val KEY_TIMER_DURATION = "timer_duration_minutes"
    }
}
