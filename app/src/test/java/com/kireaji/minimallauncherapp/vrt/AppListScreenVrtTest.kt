package com.kireaji.minimallauncherapp.vrt

import android.app.Application
import com.kireaji.minimallauncherapp.data.model.AppInfo
import com.kireaji.minimallauncherapp.ui.compose.AppListScreen
import com.kireaji.minimallauncherapp.ui.compose.previewAppList
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * アプリ一覧画面の VRT。
 *
 * 期待画像: vrt/expect/<device>/app-list-<state>.png
 *   ./gradlew :app:vrtVerify  差分チェック（vrt/actual/ と vrt/diff/ を生成）
 *   ./gradlew :app:vrtRecord  期待画像の更新（コミットすること）
 *
 * GraphicsMode.NATIVE は Roborazzi が実際のピクセルを描くために必須。
 * application を素の Application にしているのは @HiltAndroidApp の MyApplication を
 * 起動させないため。状態は引数で渡すので DI グラフは要らない。
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = Application::class)
class AppListScreenVrtTest {

    private companion object {
        const val SCREEN = "app-list"
    }

    @Test
    fun ideal() = Vrt.capture(SCREEN, VrtState.IDEAL) {
        AppListScreen(appInfoList = previewAppList(), onAppClick = {})
    }

    @Test
    fun empty() = Vrt.capture(SCREEN, VrtState.EMPTY) {
        // AppListViewModel.appInfoListStateFlow の初期値そのもの＝コールドスタート時の見た目。
        AppListScreen(appInfoList = emptyList<AppInfo>(), onAppClick = {})
    }

    // VrtState.ERROR は意図的に未カバー。AppListViewModel にエラー状態が無く、
    // AppListScreen にもエラー分岐が無いため。撮るためだけに本番へエラー UI を足さないこと。
    // 本番にエラー状態が入ったら以下を追加する:
    //   @Test fun error() = Vrt.capture(SCREEN, VrtState.ERROR) { AppListScreen(...) }
}
