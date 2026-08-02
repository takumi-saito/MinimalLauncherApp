package com.kireaji.minimallauncherapp.ui.compose

import com.kireaji.minimallauncherapp.data.model.AppInfo
import com.kireaji.minimallauncherapp.data.model.CalendarCell
import com.kireaji.minimallauncherapp.data.model.CalendarUiState
import java.time.DayOfWeek

/**
 * @Preview と VRT（app/src/test/java/.../vrt/）で共有するサンプルデータ。
 *
 * 実行するたびに同じ値を返す必要がある。ViewModel 経由のデータは使えない:
 * - CalendarViewModel は LocalDate.now() を呼ぶため期待画像が毎日腐る
 * - AppListViewModel は PackageManager 経由なので端末ごとに変わる
 *
 * internal だがユニットテストからは参照できる（AGP が debugUnitTest を main の
 * friend module として登録するため）。
 */

/** A〜Z 各文字で始まるアプリ名。インデックスバーの表示を一通り網羅する。 */
internal fun previewAppList(): List<AppInfo> {
    val labels = listOf(
        "Amazon", "Apple Music",
        "Bank App", "Browser",
        "Calculator", "Calendar", "Camera",
        "Discord", "Drive",
        "Email",
        "Facebook", "Files",
        "Gmail", "Google Maps",
        "Home",
        "Instagram",
        "Jira",
        "Kindle",
        "LinkedIn", "Line",
        "Maps", "Messages",
        "Netflix", "Notes",
        "Outlook",
        "Photos", "Play Store",
        "Reddit",
        "Spotify", "Settings",
        "Twitter", "TikTok",
        "Uber",
        "Venmo",
        "WhatsApp", "Weather",
        "YouTube",
        "Zoom"
    )
    return labels.map { label ->
        AppInfo(
            packageName = "com.example.${label.lowercase().replace(" ", "")}",
            icon = null,
            label = label,
            sourceDir = "",
            componentName = null
        )
    }.sortedBy { it.label }
}

/** 日曜始まりの 12 月。15 日を今日として扱い、土日の色分けと今日の丸背景を含む。 */
internal fun previewCalendarUiState(): CalendarUiState = CalendarUiState(cells = previewCalendarCells())

private fun previewCalendarCells(): List<CalendarCell> = listOf(
    // Month header row
    CalendarCell.MonthHeader("DEC"),
    CalendarCell.Empty,
    CalendarCell.Empty,
    CalendarCell.Empty,
    CalendarCell.Empty,
    CalendarCell.Empty,
    CalendarCell.Empty,
    // Day of week header row
    CalendarCell.DayOfWeekHeader(DayOfWeek.SUNDAY),
    CalendarCell.DayOfWeekHeader(DayOfWeek.MONDAY),
    CalendarCell.DayOfWeekHeader(DayOfWeek.TUESDAY),
    CalendarCell.DayOfWeekHeader(DayOfWeek.WEDNESDAY),
    CalendarCell.DayOfWeekHeader(DayOfWeek.THURSDAY),
    CalendarCell.DayOfWeekHeader(DayOfWeek.FRIDAY),
    CalendarCell.DayOfWeekHeader(DayOfWeek.SATURDAY),
    // Week 1: 1(Sun) - 7(Sat)
    CalendarCell.DateCell(1, DayOfWeek.SUNDAY),
    CalendarCell.DateCell(2, DayOfWeek.MONDAY),
    CalendarCell.DateCell(3, DayOfWeek.TUESDAY),
    CalendarCell.DateCell(4, DayOfWeek.WEDNESDAY),
    CalendarCell.DateCell(5, DayOfWeek.THURSDAY),
    CalendarCell.DateCell(6, DayOfWeek.FRIDAY),
    CalendarCell.DateCell(7, DayOfWeek.SATURDAY),
    // Week 2: 8(Sun) - 14(Sat)
    CalendarCell.DateCell(8, DayOfWeek.SUNDAY),
    CalendarCell.DateCell(9, DayOfWeek.MONDAY),
    CalendarCell.DateCell(10, DayOfWeek.TUESDAY),
    CalendarCell.DateCell(11, DayOfWeek.WEDNESDAY),
    CalendarCell.DateCell(12, DayOfWeek.THURSDAY),
    CalendarCell.DateCell(13, DayOfWeek.FRIDAY),
    CalendarCell.DateCell(14, DayOfWeek.SATURDAY),
    // Week 3: 15(Sun) - 21(Sat)
    CalendarCell.DateCell(15, DayOfWeek.SUNDAY, isToday = true),
    CalendarCell.DateCell(16, DayOfWeek.MONDAY),
    CalendarCell.DateCell(17, DayOfWeek.TUESDAY),
    CalendarCell.DateCell(18, DayOfWeek.WEDNESDAY),
    CalendarCell.DateCell(19, DayOfWeek.THURSDAY),
    CalendarCell.DateCell(20, DayOfWeek.FRIDAY),
    CalendarCell.DateCell(21, DayOfWeek.SATURDAY),
    // Week 4: 22(Sun) - 28(Sat)
    CalendarCell.DateCell(22, DayOfWeek.SUNDAY),
    CalendarCell.DateCell(23, DayOfWeek.MONDAY),
    CalendarCell.DateCell(24, DayOfWeek.TUESDAY),
    CalendarCell.DateCell(25, DayOfWeek.WEDNESDAY),
    CalendarCell.DateCell(26, DayOfWeek.THURSDAY),
    CalendarCell.DateCell(27, DayOfWeek.FRIDAY),
    CalendarCell.DateCell(28, DayOfWeek.SATURDAY),
    // Week 5: 29(Sun) - 31(Tue)
    CalendarCell.DateCell(29, DayOfWeek.SUNDAY),
    CalendarCell.DateCell(30, DayOfWeek.MONDAY),
    CalendarCell.DateCell(31, DayOfWeek.TUESDAY),
    CalendarCell.Empty,
    CalendarCell.Empty,
    CalendarCell.Empty,
    CalendarCell.Empty
)
