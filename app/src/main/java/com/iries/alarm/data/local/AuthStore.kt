package com.iries.alarm.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.iries.alarm.domain.models.AuthData

class AuthStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_data", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    object Keys {
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
        const val TIME_STAMP = "time_stamp"
        const val EXPIRES_IN = "expires_in"
    }

    fun containsAuthData(): Boolean {
        val accessToken = prefs.getString(Keys.ACCESS_TOKEN, "")
        return !accessToken.isNullOrEmpty()
    }

    fun loadAuthData(): AuthData {
        return AuthData(
            accessToken = prefs.getString(Keys.ACCESS_TOKEN, "")!!,
            refreshToken = prefs.getString(Keys.REFRESH_TOKEN, "")!!,
            timeStamp = prefs.getLong(Keys.TIME_STAMP, 0),
            expiresIn = prefs.getLong(Keys.EXPIRES_IN, 0)
        )
    }

    fun saveAuthData(authData: AuthData) {
        editor.putString(Keys.ACCESS_TOKEN, authData.accessToken)
        editor.putString(Keys.REFRESH_TOKEN, authData.refreshToken)
        editor.putLong(Keys.TIME_STAMP, authData.timeStamp)
        editor.putLong(Keys.EXPIRES_IN, authData.expiresIn)

        val isSaved = editor.commit()
        if (isSaved) {
            Log.d("ConfigsReader", "User configs saved successfully")
        } else {
            Log.e("ConfigsReader", "Failed to save user configs")
        }
    }

}