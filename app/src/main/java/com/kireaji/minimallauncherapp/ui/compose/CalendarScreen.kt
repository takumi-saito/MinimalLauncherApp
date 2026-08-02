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
import androidx.compose.ui.unit.sp
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
    CalendarScreen(uiState = uiState.value)
}

/**
 * 状態を引数で受け取る版。VRT（CalendarScreenVrtTest）はこちらを撮影する。
 * 縦センタリングの Box はこの画面固有の見た目なので、CalendarGrid ではなくここを撮ること。
 */
@Composable
fun CalendarScreen(uiState: CalendarUiState) {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CalendarGrid(uiState = uiState)
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
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp
                )
            }
            is CalendarCell.DayOfWeekHeader -> {
                Text(
                    text = cell.dayOfWeek.toString().substring(0, 3),
                    color = getDayOfWeekColor(cell.dayOfWeek),
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp
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

// プレビュー用のサンプルデータは PreviewData.kt（previewCalendarUiState）に集約している。
// VRT と同じデータを使うことで、プレビューが通れば期待画像も同じ内容になる。

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun CalendarGridPreviewDark() {
    AppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            CalendarGrid(uiState = previewCalendarUiState())
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
            CalendarGrid(uiState = previewCalendarUiState())
        }
    }
}
