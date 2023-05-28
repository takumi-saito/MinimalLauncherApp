package com.kireaji.minimallauncherapp.data.model

import androidx.annotation.ColorInt


data class DateInfo(
    val date: String,
    @ColorInt var textColor: Int? = null
)