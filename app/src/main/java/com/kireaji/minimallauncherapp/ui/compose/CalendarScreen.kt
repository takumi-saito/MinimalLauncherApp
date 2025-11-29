package com.kireaji.minimallauncherapp.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kireaji.minimallauncherapp.R
import com.kireaji.minimallauncherapp.data.model.CalendarCell
import com.kireaji.minimallauncherapp.data.model.CalendarUiState
import com.kireaji.minimallauncherapp.ui.viewmodel.CalendarViewModel
import java.time.DayOfWeek

private object CalendarColors {
    val Saturday = Color(0xFF558FA7)  // MINIMAL_BLUE
    val Sunday = Color(0xFFF28C8F)    // MINIMAL_RED
    val TodayBackgroundDark = Color.White.copy(alpha = 0.2f)
    val TodayBackgroundLight = Color.Black.copy(alpha = 0.1f)
}

@Composable
private fun todayBackgroundColor(): Color {
    return if (isSystemInDarkTheme()) {
        CalendarColors.TodayBackgroundDark
    } else {
        CalendarColors.TodayBackgroundLight
    }
}

@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    val uiState = viewModel.uiState.collectAsState()
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CalendarGrid(uiState = uiState.value)
            }
        }
    }
}

@Composable
fun CalendarGrid(uiState: CalendarUiState) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(20.dp)
    ) {
        items(uiState.cells) { cell ->
            CalendarCellItem(cell = cell)
        }
    }
}

@Composable
fun CalendarCellItem(cell: CalendarCell) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        when (cell) {
            is CalendarCell.YearHeader -> {
                Text(
                    text = cell.year.toString(),
                    color = colorResource(id = R.color.base_text),
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light
                )
            }
            is CalendarCell.MonthHeader -> {
                Text(
                    text = cell.month,
                    color = colorResource(id = R.color.base_text),
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light
                )
            }
            is CalendarCell.DayOfWeekHeader -> {
                Text(
                    text = cell.dayOfWeek.toString().substring(0, 3),
                    color = getDayOfWeekColor(cell.dayOfWeek),
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light
                )
            }
            is CalendarCell.DateCell -> {
                val backgroundColor = todayBackgroundColor()
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .then(
                            if (cell.isToday) {
                                Modifier.background(
                                    color = backgroundColor,
                                    shape = CircleShape
                                )
                            } else {
                                Modifier
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cell.day.toString(),
                        color = getDayOfWeekColor(cell.dayOfWeek),
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light
                    )
                }
            }
            CalendarCell.Empty -> {
                // 空のセル - 何も表示しない
            }
        }
    }
}

@Composable
private fun getDayOfWeekColor(dayOfWeek: DayOfWeek): Color {
    return when (dayOfWeek) {
        DayOfWeek.SATURDAY -> CalendarColors.Saturday
        DayOfWeek.SUNDAY -> CalendarColors.Sunday
        else -> colorResource(id = R.color.base_text)
    }
}

private fun createSampleCells() = listOf(
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

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun CalendarGridPreviewDark() {
    AppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            CalendarGrid(uiState = CalendarUiState(cells = createSampleCells()))
        }
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun CalendarGridPreviewLight() {
    AppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            CalendarGrid(uiState = CalendarUiState(cells = createSampleCells()))
        }
    }
}
