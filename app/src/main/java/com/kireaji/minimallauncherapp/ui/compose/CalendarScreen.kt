package com.kireaji.minimallauncherapp.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
}

@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    val uiState = viewModel.uiState.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CalendarGrid(uiState = uiState.value)
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
        contentAlignment = Alignment.CenterEnd
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
                Text(
                    text = cell.day.toString(),
                    color = getDayOfWeekColor(cell.dayOfWeek),
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light
                )
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

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun CalendarGridPreview() {
    val sampleCells = listOf(
        CalendarCell.MonthHeader("DEC"),
        CalendarCell.Empty,
        CalendarCell.Empty,
        CalendarCell.Empty,
        CalendarCell.Empty,
        CalendarCell.Empty,
        CalendarCell.Empty,
        CalendarCell.DayOfWeekHeader(DayOfWeek.SUNDAY),
        CalendarCell.DayOfWeekHeader(DayOfWeek.MONDAY),
        CalendarCell.DayOfWeekHeader(DayOfWeek.TUESDAY),
        CalendarCell.DayOfWeekHeader(DayOfWeek.WEDNESDAY),
        CalendarCell.DayOfWeekHeader(DayOfWeek.THURSDAY),
        CalendarCell.DayOfWeekHeader(DayOfWeek.FRIDAY),
        CalendarCell.DayOfWeekHeader(DayOfWeek.SATURDAY),
        CalendarCell.DateCell(1, DayOfWeek.SUNDAY),
        CalendarCell.DateCell(2, DayOfWeek.MONDAY),
        CalendarCell.DateCell(3, DayOfWeek.TUESDAY),
        CalendarCell.DateCell(4, DayOfWeek.WEDNESDAY),
        CalendarCell.DateCell(5, DayOfWeek.THURSDAY),
        CalendarCell.DateCell(6, DayOfWeek.FRIDAY),
        CalendarCell.DateCell(7, DayOfWeek.SATURDAY)
    )
    CalendarGrid(uiState = CalendarUiState(cells = sampleCells))
}
