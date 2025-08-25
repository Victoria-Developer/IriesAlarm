package com.iries.alarm.presentation.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.iries.alarm.BuildConfig

class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loginUrl = buildLoginUri()
        val customTabsIntent = CustomTabsIntent.Builder().build()

        try {
            customTabsIntent.launchUrl(this, loginUrl)
        } catch (e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW, loginUrl))
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { uri ->
            if (uri.scheme == "iriesalarm" && uri.host == "callback") {
                val code = uri.getQueryParameter("code")
                val resultIntent = Intent().apply {
                    putExtra("code", code)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    /** Can pass a code challenge along with verifier for security*/
    private fun buildLoginUri(): Uri {
        return ("https://secure.soundcloud.com/authorize?" +
                "client_id=${BuildConfig.client_id}" +
                "&redirect_uri=${BuildConfig.redirect_uri}" +
                "&response_type=code" +
                "&state=STATE" +
                "&display=popup").toUri()
    }
}