package com.nhuhuy.numen.android.context

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.net.toUri

fun Context.openUrl(url: String) {
    startActivity(
        Intent(
            Intent.ACTION_VIEW,
            url.toUri()
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}

fun Context.openAppSetting(){
    startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}

fun Context.shareText(text: String) {
    startActivity(
        Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            },
            null
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}

// === APP INFO
val Context.versionName: String?
    get() = packageManager
        .getPackageInfo(packageName, 0)
        .versionName

val Context.versionCode: Long
    get() = PackageInfoCompat.getLongVersionCode(
        packageManager.getPackageInfo(
            packageName,
            0
        )
    )

// === CLIPBOARD
fun Context.hasNotificationPermission(): Boolean {
    return if (
        Build.VERSION.SDK_INT >=
        Build.VERSION_CODES.TIRAMISU
    ) {
        checkSelfPermission(
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

fun NotificationManager.createChannel(
    id: String,
    name: String,
    importance: Int =
        NotificationManager.IMPORTANCE_DEFAULT
) {
    createNotificationChannel(
        NotificationChannel(
            id,
            name,
            importance
        )
    )
}