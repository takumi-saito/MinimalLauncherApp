package com.kireaji.minimallauncherapp.data.model

import java.time.DayOfWeek

/**
 * カレンダーのセルを表すsealed class
 */
sealed class CalendarCell {
    data class YearHeader(val year: Int) : CalendarCell()
    data class MonthHeader(val month: String) : CalendarCell()
    data class DayOfWeekHeader(val dayOfWeek: DayOfWeek) : CalendarCell()
    data class DateCell(val day: Int, val dayOfWeek: DayOfWeek, val isToday: Boolean = false) : CalendarCell()
    object Empty : CalendarCell()
}

data class CalendarUiState(
    val cells: List<CalendarCell> = emptyList()
)
