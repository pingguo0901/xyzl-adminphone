package com.stellarelite.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.stellarelite.admin.ui.theme.AdminAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        UpdateManager.setCurrentVersion(packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt())
        setContent {
            AdminAppTheme {
                App(
                appVersionName = packageManager.getPackageInfo(packageName, 0).versionName ?: "",
                onCheckUpdate = { UpdateManager.checkForUpdate() },
                    onRequestUpdate = { info ->
                        UpdateManager.downloadAndInstall(this, info.apkUrl)
                    }
                )
            }
        }
    }
}
