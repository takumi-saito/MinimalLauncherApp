package com.kireaji.minimallauncherapp.data.model

import android.content.ComponentName
import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val icon: Drawable?,
    val label: String,
    val sourceDir: String,
    val componentName: ComponentName?
) {
    companion object {
        fun samples(): List<AppInfo> {
            return (0..50).map {
                AppInfo(
                    packageName = "packageName$it",
                    icon = null,
                    label = "label$it",
                    sourceDir = "sourceDir$it",
                    componentName = null
                )
            }
        }
    }
}