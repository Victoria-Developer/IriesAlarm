package com.iries.youtubealarm.presentation

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.iries.youtubealarm.domain.ConfigsReader
import com.iries.youtubealarm.domain.models.UserConfigs
import com.iries.youtubealarm.presentation.navigation.AppNavigation
import com.iries.youtubealarm.presentation.theme.IriesAlarmTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var configsReader: ConfigsReader

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        configsReader = ConfigsReader(this)
        val configs = configsReader.loadUserConfigs()
        val isFirstLaunch = configs.getIsFirstLaunch()

        enableEdgeToEdge()
        setContent {
            IriesAlarmTheme {
                AppNavigation(
                    navController = rememberNavController(),
                    isFirstLaunch = isFirstLaunch
                )
            }
        }

        checkPermissions()
    }

    override fun onStop() {
        val configs = UserConfigs()
        configs.setFirstLaunch(false)
        configsReader.saveUserConfigs(configs)
        super.onStop()
    }

    private fun checkPermissions() {
        // 1. Overlay permission
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:$packageName".toUri()
            )
            startActivity(intent)
        }
    }

}
