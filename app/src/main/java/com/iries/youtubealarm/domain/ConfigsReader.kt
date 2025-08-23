package com.iries.youtubealarm.domain

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.iries.youtubealarm.domain.models.UserConfigs

class ConfigsReader(context: Context) {
    private val configsName = "alarm_settings"
    private val prefs: SharedPreferences =
        context.getSharedPreferences(configsName, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    object Keys {
        const val IS_FIRST_LAUNCH = "is_first_launch"
    }

    fun loadUserConfigs(): UserConfigs {
        val isFirstLaunch = prefs.getBoolean(Keys.IS_FIRST_LAUNCH, true)

        val userConfigs = UserConfigs()
        userConfigs.setFirstLaunch(isFirstLaunch)
        return userConfigs
    }

    fun saveUserConfigs(userConfigs: UserConfigs) {
        editor.putBoolean(Keys.IS_FIRST_LAUNCH, userConfigs.getIsFirstLaunch())

        val isSaved = editor.commit() // Use this to confirm
        if (isSaved) {
            Log.d("ConfigsReader", "User configs saved successfully")
        } else {
            Log.e("ConfigsReader", "Failed to save user configs")
        }
    }

}