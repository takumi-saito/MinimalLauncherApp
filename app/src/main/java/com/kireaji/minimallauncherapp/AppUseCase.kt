package com.kireaji.minimallauncherapp

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.util.Log

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
            val resolveInfoList: List<ResolveInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.queryIntentActivities(
                    Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
                    PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
                )
            } else {
                context.packageManager.queryIntentActivities(
                    Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
                    PackageManager.GET_META_DATA
                )
            }
            val pm = context.packageManager
            return resolveInfoList
                .asSequence()
                .mapNotNull { it.activityInfo }
                .filter {
                    it.packageName != context.packageName
                }
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