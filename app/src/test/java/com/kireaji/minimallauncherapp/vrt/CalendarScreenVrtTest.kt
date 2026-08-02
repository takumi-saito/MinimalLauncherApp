package com.kireaji.minimallauncherapp.vrt

import android.app.Application
import com.kireaji.minimallauncherapp.data.model.CalendarUiState
import com.kireaji.minimallauncherapp.ui.compose.CalendarScreen
import com.kireaji.minimallauncherapp.ui.compose.previewCalendarUiState
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * カレンダー画面の VRT。
 *
 * 期待画像: vrt/expect/<device>/calendar-<state>.png
 *
 * CalendarViewModel は LocalDate.now() を呼ぶため、ViewModel 経由で撮ると期待画像が
 * 毎日腐る。必ず固定データ（previewCalendarUiState）を直接渡すこと。
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = Application::class)
class CalendarScreenVrtTest {

    private companion object {
        const val SCREEN = "calendar"
    }

    @Test
    fun ideal() = Vrt.capture(SCREEN, VrtState.IDEAL) {
        CalendarScreen(uiState = previewCalendarUiState())
    }

    @Test
    fun empty() = Vrt.capture(SCREEN, VrtState.EMPTY) {
        // CalendarUiState の既定値そのもの。
        CalendarScreen(uiState = CalendarUiState())
    }

    // VrtState.ERROR は意図的に未カバー。CalendarViewModel にエラー状態が無く、
    // CalendarScreen にもエラー分岐が無いため。
}
