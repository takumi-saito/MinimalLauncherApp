package com.kireaji.minimallauncherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.kireaji.minimallauncherapp.data.model.CalendarCell
import com.kireaji.minimallauncherapp.data.model.CalendarUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState

    private val dayOfWeekOrder = listOf(
        DayOfWeek.SUNDAY,
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    )

    companion object {
        private const val ADDITIONAL_WEEK_COUNT = 6
    }

    init {
        refreshCalendar()
    }

    fun refreshCalendar() {
        val cells = buildCalendarCells()
        _uiState.value = CalendarUiState(cells = cells)
    }

    private fun buildCalendarCells(): List<CalendarCell> {
        val cells = mutableListOf<CalendarCell>()
        var currentDate = LocalDate.of(LocalDate.now().year, LocalDate.now().month, 1)
        var isFirstDay = currentDate.dayOfMonth == 1

        for (week in 1..ADDITIONAL_WEEK_COUNT) {
            if (isFirstDay) {
                addMonthHeader(currentDate, cells)
                addDayOfWeekHeader(cells)
                isFirstDay = false
            }

            for (dayOfWeek in dayOfWeekOrder) {
                if (!isFirstDay && dayOfWeek == currentDate.dayOfWeek) {
                    cells.add(CalendarCell.DateCell(currentDate.dayOfMonth, dayOfWeek))
                    currentDate = currentDate.plusDays(1)
                    if (currentDate.dayOfMonth == 1) {
                        isFirstDay = true
                    }
                } else {
                    cells.add(CalendarCell.Empty)
                }
            }
        }

        return cells
    }

    private fun addMonthHeader(date: LocalDate, cells: MutableList<CalendarCell>) {
        if (date.month == Month.JANUARY) {
            cells.add(CalendarCell.YearHeader(date.year))
            cells.add(CalendarCell.MonthHeader(date.month.toString().substring(0, 3)))
        } else {
            cells.add(CalendarCell.MonthHeader(date.month.toString().substring(0, 3)))
            cells.add(CalendarCell.Empty)
        }
        // 残り5セルを空で埋める（7列グリッド用）
        repeat(5) {
            cells.add(CalendarCell.Empty)
        }
    }

    private fun addDayOfWeekHeader(cells: MutableList<CalendarCell>) {
        dayOfWeekOrder.forEach { dayOfWeek ->
            cells.add(CalendarCell.DayOfWeekHeader(dayOfWeek))
        }
    }
}
