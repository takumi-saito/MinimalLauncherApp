package com.kireaji.minimallauncherapp.vrt

import androidx.compose.runtime.Composable
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import org.robolectric.RuntimeEnvironment
import java.io.File

/**
 * VRT のキャプチャ本体。全撮影条件（VrtDevices.all）を 1 呼び出しで回す。
 *
 * 出力:
 *   vrt/expect/<device>/<screen>-<state>.png  期待画像（git 管理。vrtRecord で更新）
 *   vrt/actual/<device>/<screen>-<state>.png  現状画像（vrtCollect が配置）
 *   vrt/diff/<device>/<screen>-<state>.png    差分画像（vrtCollect が配置）
 *
 * record / compare のどちらで動くかは Roborazzi の Gradle タスクがシステムプロパティで
 * 決める。プロパティが無い通常の ./gradlew test では captureRoboImage が先頭で即 return
 * するため、VRT テストは実質 no-op になりファイルを一切書かない。
 */
object Vrt {

    private val root: File = File(
        requireNotNull(System.getProperty("vrt.root")) {
            "システムプロパティ 'vrt.root' が未設定です。" +
                "./gradlew :app:vrtVerify または :app:vrtRecord から実行してください。"
        }
    )

    /**
     * Roborazzi は期待画像の「ベース名のみ」から <name>_actual.png / <name>_compare.png を
     * 導出し、このディレクトリにフラットに書く。撮影条件ごとにサブディレクトリを掘らないと
     * pixel10/app-list-ideal.png と pixel10-night/app-list-ideal.png が同じ
     * "app-list-ideal_actual.png" を書いて上書きし合う。
     *
     * ここは中間生成物なので vrt/.compare/ に置き gitignore している。
     */
    private val compareRoot: File = File(root, ".compare")

    fun capture(screen: String, state: VrtState, content: @Composable () -> Unit) {
        VrtDevices.all.forEach { device ->
            // 修飾子は captureRoboImage より前に設定する。RoborazziComposeOptions.uiMode() は
            // ActivityScenario.launch の後に適用されるため、生成済み Activity への設定変更の
            // 伝播に依存してしまう。
            // ダークモードも含めて 1 回で渡す（"+night" の追い足しはサイズがずれる。VrtDevices 参照）。
            RuntimeEnvironment.setQualifiers(device.qualifier)

            captureRoboImage(
                // 絶対パスを渡すと Roborazzi のファイルパス戦略や roborazzi.output.dir の設定を
                // 一切経由せずここで指定した場所に書かれる。
                filePath = File(root, "expect/${device.name}/$screen-${state.slug}.png").absolutePath,
                roborazziOptions = RoborazziOptions(
                    compareOptions = RoborazziOptions.CompareOptions(
                        outputDirectoryPath = File(compareRoot, device.name).absolutePath
                    )
                ),
                content = content
            )
        }
    }
}
