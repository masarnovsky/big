package com.masarnovsky.big.mvvm.view.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun getAppVersion(): String {
    val context = LocalContext.current
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "1.0"
    } catch (e: Exception) {
        "undefined"
    }
}