package com.kireaji.minimallauncherapp

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log

class AppUseCase(private val context: Context) {

    private val pm: PackageManager = context.packageManager

    /**
     * アプリを起動させる
     */
    fun launch(appInfo: AppInfo) {
        try {
            val intent = Intent(Intent.ACTION_MAIN).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                it.addCategory(Intent.CATEGORY_LAUNCHER)
                it.component = appInfo.componentName
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "ActivityNotFoundException", e)
        }
    }

    /**
     * ランチャーに表示するアプリ一覧を取得する
     */
    fun getAppInfoList(): List<AppInfo> {
        val packages: List<ApplicationInfo> = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val appInfoList = packages.filter {
            pm.getLaunchIntentForPackage(it.packageName) != null
        }.map { packageInfo ->
            Log.d(TAG, "Installed package :" + packageInfo.packageName)
            Log.d(TAG, "Launch Intent For Package :" + pm.getLaunchIntentForPackage(packageInfo.packageName))
            Log.d(TAG, "Application Label :" + pm.getApplicationLabel(packageInfo))
            Log.d(TAG, "Application Icon :" + pm.getApplicationIcon(packageInfo.packageName).toString())
            Log.d(TAG, "Source dir : " + packageInfo.sourceDir)
            Log.d(
                TAG,
                "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName)
            )
            AppInfo(
                packageName = packageInfo.packageName,
                icon = pm.getApplicationIcon(packageInfo.packageName), // icon
                label = pm.getApplicationLabel(packageInfo).toString(),
                componentName = pm.getLaunchIntentForPackage(packageInfo.packageName)?.component,
                sourceDir = packageInfo.sourceDir
            )
        }

        return appInfoList
            .filter { it.componentName != null }
            .asSequence()
            .sortedBy { it.label }
            .toList()
    }

    companion object {
        val TAG = AppUseCase::class.java.simpleName
    }
}