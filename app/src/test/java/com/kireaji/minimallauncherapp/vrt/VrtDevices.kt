package com.kireaji.minimallauncherapp.vrt

/**
 * VRT の撮影条件。1 条件 = 出力ディレクトリ 1 つ（vrt/<name>/...）。
 *
 * @param name 出力ディレクトリ名。端末名とダーク/ライトが判別できるようにする。
 * @param qualifier Robolectric の設定修飾子。画面サイズ・密度・ダークモードを全て含む。
 */
data class VrtDevice(
    val name: String,
    val qualifier: String
)

object VrtDevices {

    /**
     * Pixel 10: 1080 x 2424 px / 約 422ppi。
     *
     * 422ppi は 420dpi バケットに入るので density = 420 / 160 = 2.625。
     *   幅   dp = 1080 / 2.625 = 411.4 -> 411
     *   高さ dp = 2424 / 2.625 = 923.4 -> 923
     *   smallestWidth 411dp は [320, 600) なので "normal"
     *   アスペクト比 2424 / 1080 = 2.24 (>= 1.75) なので "long"
     *
     * RobolectricDeviceQualifiers に Pixel 10 のプリセットは無い（Roborazzi 1.47.0 は Pixel7 まで）
     * ため生の修飾子を書いている。この文字列は Roborazzi が生成する Pixel9 定数と完全に一致するが、
     * これは Pixel 9 と 10 が同じパネルを積んでいるためで、導出が正しいことの裏付けになる。
     *
     * ダークモードは night 修飾子として同じ文字列に埋め込む。setQualifiers を
     * 「基本の修飾子」→「+night」と 2 回に分けて呼ぶと、2 回目で Robolectric が現在の
     * Configuration から修飾子文字列を作り直す過程で dp が丸め落ちし、ライトとダークで
     * 画像サイズが 2px ずれる。1 回で渡すこと。
     */
    private fun pixel10(night: Boolean): String =
        "w411dp-h923dp-normal-long-notround-any-" +
            (if (night) "night" else "notnight") +
            "-420dpi-keyshidden-nonav"

    /**
     * 撮影条件を増やすときはここに 1 行足す（画像枚数とレビュー対象がその分増える）。
     *
     * ダークモードをファイル名ではなくディレクトリ側に持たせているのは、このアプリが
     * ダーク判定を 2 系統持っているため:
     *   1. AppTheme(darkTheme = isSystemInDarkTheme()) が background/surface を切り替える
     *   2. colorResource(R.color.base_text) が values/ と values-night/ を引き分ける
     *      （CalendarScreen の todayBackgroundColor() も同様）
     * Robolectric の night は設定修飾子なので、これを立てれば Configuration が一度で切り替わり
     * 両方が同時に正しく追従する。ファイル名側に持たせて AppTheme(darkTheme = true) だけを
     * 明示すると、values-night/ が未選択のまま「暗い背景に暗い文字」という、アプリでは
     * 決して起こらない状態を撮ることになる。
     */
    val all: List<VrtDevice> = listOf(
        VrtDevice(name = "pixel10", qualifier = pixel10(night = false)),
        VrtDevice(name = "pixel10-night", qualifier = pixel10(night = true))
    )
}
