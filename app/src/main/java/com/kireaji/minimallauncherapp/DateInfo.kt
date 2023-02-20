package com.kireaji.minimallauncherapp

import androidx.annotation.ColorInt


data class DateInfo(
    val date: String,
    @ColorInt var textColor: Int? = null
)