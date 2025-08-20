package com.iries.youtubealarm.presentation

import android.Manifest
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
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
            requestOverlayPermission()
        }

        // 2. Notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Runtime permission (API 33+)
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        } else {
            // API 31–32: no runtime request, only check if master switch is off
            val notificationManager = getSystemService(NotificationManager::class.java)
            if (!notificationManager.areNotificationsEnabled()) {
                // Open app notification settings screen
                startActivity(
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                )
            }
        }
    }

    private fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:$packageName".toUri()
            )
            startActivity(intent)
        }
    }
}
