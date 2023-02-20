package com.kireaji.minimallauncherapp

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

class AppUseCase {
    companion object {
        /**
         * アプリを起動させる
         */
        fun launch(context: Context, appInfo: AppInfo) {
            try {
                val intent = Intent(Intent.ACTION_MAIN).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    it.addCategory(Intent.CATEGORY_LAUNCHER)
                    it.component = appInfo.componentName
                }
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {

            }
        }

        /**
         * ランチャーに表示するアプリ一覧を取得する
         */
        fun getAppInfoList(context: Context): List<AppInfo> {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN).also {
                it.addCategory(Intent.CATEGORY_LAUNCHER)
            }

            return pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
                .asSequence()
                .mapNotNull { it.activityInfo }
                .filter { it.packageName != context.packageName }
                .map {
                    AppInfo(
                        icon = it.loadIcon(pm), // icon
                        label = it.loadLabel(pm).toString(),
                        componentName = ComponentName(it.packageName, it.name)
                    )
                }
                .sortedBy { it.label }
                .toList()
        }
    }
}