package com.iries.youtubealarm.domain

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.iries.youtubealarm.domain.constants.Duration
import com.iries.youtubealarm.domain.constants.Order
import com.iries.youtubealarm.domain.models.UserConfigs

class ConfigsReader(context: Context) {
    private val configsName = "alarm_settings"
    private val prefs: SharedPreferences =
        context.getSharedPreferences(configsName, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    object Keys {
        const val IS_FIRST_LAUNCH = "is_first_launch"
        const val DURATION_ID_KEY = "duration_id"
        const val ORDER_ID_KEY = "order_id"
    }

    fun loadUserConfigs(): UserConfigs {
        val isFirstLaunch = prefs.getBoolean(Keys.IS_FIRST_LAUNCH, true)
        val orderId = prefs.getInt(Keys.ORDER_ID_KEY, 0)
        val durationId = prefs.getInt(Keys.DURATION_ID_KEY, 0)

        val userConfigs = UserConfigs()
        userConfigs.setFirstLaunch(isFirstLaunch)
        userConfigs.setOrder(Order.entries[orderId])
        userConfigs.setDuration(Duration.entries[durationId])
        return userConfigs
    }

    fun saveUserConfigs(userConfigs: UserConfigs) {
        editor.putBoolean(Keys.IS_FIRST_LAUNCH, userConfigs.getIsFirstLaunch())
        editor.putInt(Keys.ORDER_ID_KEY, userConfigs.getOrder().ordinal)
        editor.putInt(Keys.DURATION_ID_KEY, userConfigs.getDuration().ordinal)

        val isSaved = editor.commit() // Use this to confirm
        if (isSaved) {
            Log.d("ConfigsReader", "User configs saved successfully")
        } else {
            Log.e("ConfigsReader", "Failed to save user configs")
        }
    }

}