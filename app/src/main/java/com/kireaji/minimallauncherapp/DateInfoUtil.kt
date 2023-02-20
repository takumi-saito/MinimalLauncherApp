package com.kireaji.minimallauncherapp

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

class DateInfoUtil {
    private val ADDITIONAL_COUNT_WEEK = 5
    private var localDate = LocalDate.of(LocalDate.now().year, LocalDate.now().month,1)
    private var dateInfoList = ArrayList<DateInfo>()
    private val emptyDateInfo = DateInfo("")
    private val dayOfWeekArray = arrayOf(DayOfWeek.SUNDAY,DayOfWeek.MONDAY,DayOfWeek.TUESDAY,DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY)

    private fun addMonthHeader(localDate: LocalDate , calenderStr : ArrayList<DateInfo>) {
        if (isJanuary(localDate)) {
            addYearToDateInfoList(calenderStr, localDate)
            addMonthToDateInfoList(calenderStr, localDate)
        } else {
            addMonthToDateInfoList(calenderStr, localDate)
            addEmptyInfoToDateInfoList(calenderStr)
        }
        addEmptyInfoToDateInfoList(calenderStr, 5)
    }

    private fun isJanuary(localDate: LocalDate) = localDate.month == Month.JANUARY

    private fun addYearToDateInfoList(calenderStr: ArrayList<DateInfo>, localDate: LocalDate) {
        calenderStr.add(DateInfo(localDate.year.toString()))
    }

    private fun addMonthToDateInfoList(calenderStr: ArrayList<DateInfo>, localDate: LocalDate) {
        calenderStr.add(DateInfo(localDate.month.toString().substring(0, 3)))
    }

    private fun addEmptyInfoToDateInfoList(calenderStr: ArrayList<DateInfo>, count: Int = 1) {
        for (i in 1..count) {
            calenderStr.add(emptyDateInfo)
        }
    }

    private fun addWeekHeader(calenderStr : ArrayList<DateInfo>) {
        for (i in dayOfWeekArray) {
            val dateInfo = DateInfo(i.toString().substring(0, 3))
            dateInfo.textColor = when(i) {
                DayOfWeek.SUNDAY -> ColorPalette.MINIMAL_RED
                DayOfWeek.SATURDAY -> ColorPalette.MINIMAL_BLUE
                else -> null
            }
            calenderStr.add(dateInfo)
        }
    }

    private fun addDateInfo(calenderStr : ArrayList<DateInfo>) {
        var isFirstDay = localDate.dayOfMonth == 1
        for (i in 1..ADDITIONAL_COUNT_WEEK) {
            if (isFirstDay) {
                addMonthHeader(localDate, calenderStr)
                addWeekHeader(calenderStr)
                isFirstDay = false
            }
            for (j in dayOfWeekArray) {
                if (!isFirstDay && j == localDate.dayOfWeek) {
                    val dateInfo = DateInfo(localDate.dayOfMonth.toString())
                    dateInfo.textColor = when(j) {
                        DayOfWeek.SUNDAY -> ColorPalette.MINIMAL_RED
                        DayOfWeek.SATURDAY -> ColorPalette.MINIMAL_BLUE
                        else -> null
                    }
                    calenderStr.add(dateInfo)
                    localDate = localDate.plusDays(1.toLong())
                    if (localDate.dayOfMonth == 1) {
                        isFirstDay = true
                    }
                } else {
                    calenderStr.add(emptyDateInfo)
                }
            }
        }
    }

    fun createDateInfoList() : ArrayList<DateInfo> {
        addDateInfo(dateInfoList)
        return dateInfoList
    }

    fun getDateInfoSize() : Int {
        return dateInfoList.size
    }

    fun addDateInfo() {
        addDateInfo(dateInfoList)
    }
}