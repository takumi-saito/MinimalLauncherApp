package com.kireaji.minimallauncherapp.vrt

/**
 * 撮影する画面の状態。ファイル名の後半になる（vrt/<device>/<screen>-<state>.png）。
 *
 * ERROR は画面によっては存在しない。現時点で AppListViewModel / CalendarViewModel の
 * どちらにもエラー状態が無く、画面側にもエラー分岐が無いため未使用。撮るためだけに
 * 本番コードへエラー UI を足すのは本末転倒なので、本番にエラー状態が入ってから使うこと。
 */
enum class VrtState {
    IDEAL,
    EMPTY,
    ERROR;

    val slug: String get() = name.lowercase()
}
