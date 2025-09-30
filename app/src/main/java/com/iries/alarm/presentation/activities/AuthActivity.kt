package com.iries.alarm.presentation.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.iries.alarm.BuildConfig
import com.iries.alarm.presentation.theme.IriesAlarmTheme

class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        login()

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    cancelAuth()
                }
            })

        enableEdgeToEdge()
        setContent {
            IriesAlarmTheme {
                AuthActivityScreen()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("AuthActivity", "New intent received")
        intent.data?.let { handleLoginCode(it) }
    }

    private fun login() {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        val loginUrl = ("https://secure.soundcloud.com/authorize?" +
                "client_id=${BuildConfig.client_id}" +
                "&redirect_uri=${BuildConfig.redirect_uri}" +
                "&response_type=code" +
                "&state=STATE").toUri()
        try {
            customTabsIntent.launchUrl(this, loginUrl)
        } catch (e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW, loginUrl))
        }
    }

    private fun handleLoginCode(data: Uri) {
        Log.d("AuthActivity", "Deep link received: $data")
        val resultIntent = Intent().apply {
            putExtra("code", data.getQueryParameter("code"))
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun cancelAuth() {
        setResult(RESULT_CANCELED, Intent())
        finish()
    }

    @Composable
    fun AuthActivityScreen() {
        Scaffold { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("SoundCloud login")
                    Button(onClick = { login() }) {
                        Text("Login")
                    }
                    Button(onClick = { cancelAuth() }) {
                        Text("Return")
                    }
                }
            }
        }
    }
}