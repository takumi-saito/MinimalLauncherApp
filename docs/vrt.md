# VRT（Visual Regression Testing）

UI の「意図した変更」と「表示崩れ・考慮漏れ」を機械的に切り分けるための仕組み。
実機やエミュレータは不要で、JVM 上（Robolectric + Roborazzi）で数秒で走る。

## 画像の種類

| ディレクトリ | 中身 | git |
|---|---|---|
| `vrt/expect/` | **期待画像**。意図した UI 変更のときに更新してコミットする | 追跡する |
| `vrt/actual/` | **現状画像**。`vrtVerify` のたびに生成される | 無視 |
| `vrt/diff/` | **差分画像**。期待と現状が食い違ったものだけ生成される | 無視 |
| `vrt/.compare/` | Roborazzi の中間生成物 | 無視 |

ファイル名は `<device>/<screen>-<state>.png`。

```
vrt/expect/pixel10/app-list-ideal.png
vrt/expect/pixel10/app-list-empty.png
vrt/expect/pixel10/calendar-ideal.png
vrt/expect/pixel10/calendar-empty.png
vrt/expect/pixel10-night/...   （同じ4枚のダークモード版）
```

## コマンド

```bash
# 期待画像と比較する。vrt/actual/ と vrt/diff/ を生成し、差分があれば失敗する
./gradlew :app:vrtVerify

# 期待画像を再生成する（意図した UI 変更のとき。結果をコミットする）
./gradlew :app:vrtRecord

# 比較だけして失敗させない（画像は生成される）
./gradlew :app:vrtCollect
```

通常の `./gradlew test` では Roborazzi のシステムプロパティが立たないため、VRT テストは
実質何もせずファイルも書かない。

## 開発フロー

1. UI を変更する
2. PR を作る（`gh pr create`）
   → PreToolUse フック（`.claude/hooks/vrt-pre-pr.sh`）が `vrtVerify` を自動実行する
3. 差分が出たらフックが PR 作成をブロックするので `vrt/diff/` の画像を見る
   （`/vrt` スキルを使うとレビューから期待画像の更新までまとめて進む）
   - 差分画像は左から **Reference（期待） | Diff（赤が差分） | New（現状）** の3枚並び
4. **表示崩れだった** → 実装を直して 2 に戻る
5. **意図した UI 変更だった** → `./gradlew :app:vrtRecord` で期待画像を更新し、
   `vrt/expect/` をコミットしてから PR を作り直す

ロジックだけの変更なら差分は出ないはず。出たらまず「なぜ出たか」を疑うこと。

## 画面・状態・端末を追加する

すべて `app/src/test/java/com/kireaji/minimallauncherapp/vrt/` の中で完結する。

**画面を足す**: `<Screen>VrtTest.kt` を 1 ファイル追加する。`AppListScreenVrtTest.kt` を写して
`SCREEN` 定数と撮る composable を差し替えるだけ。

**状態を足す**: テストクラスにメソッドを 1 つ足す。

```kotlin
@Test
fun error() = Vrt.capture(SCREEN, VrtState.ERROR) {
    AppListScreen(uiState = ...)
}
```

**端末・外観を足す**: `VrtDevices.all` に 1 行足す。画像枚数とレビュー対象がその分増える。

追加したら `./gradlew :app:vrtRecord` で期待画像を作り、内容を目視してからコミットする。

## 設計上の決め事

### 撮るのは状態を引数で受け取る版の画面 composable

`AppListScreen(appInfoList, onAppClick)` と `CalendarScreen(uiState)` を撮る。ViewModel を
受け取る版や、内側の `AppList` / `CalendarGrid` ではない。

- ViewModel 経由だと非決定的になる。`CalendarViewModel` は `LocalDate.now()` を呼ぶので
  期待画像が毎日腐り、`AppListViewModel` は `PackageManager` 経由なので端末依存になる。
- 内側の composable を撮ると、画面固有のスキャフォールドが検証対象から漏れる。特に
  `CalendarScreen` の縦センタリング（`Box(contentAlignment = Center)`）は `CalendarGrid` には
  無く、923dp の画面で最も目立つ性質なのに撮り逃すことになる。
- テスト側で `AppTheme` / `Surface` / `Box` を再宣言すると「画面の見た目の定義」が二重化し、
  main を変えてもテストが落ちないまま乖離する。スキャフォールドは main に一箇所だけ置く。

サンプルデータは `ui/compose/PreviewData.kt` に `internal` で置き、`@Preview` と VRT で共有している。

### ダークモードは device セグメント側に持たせる

`pixel10`（ライト）と `pixel10-night`（ダーク）でディレクトリを分ける。ファイル名側に
`-dark` を付ける方式は採らない。

このアプリはダーク判定を 2 系統持っている:

1. `AppTheme(darkTheme = isSystemInDarkTheme())` が background / surface を切り替える
2. `colorResource(R.color.base_text)` が `values/` と `values-night/` を引き分ける
   （`CalendarScreen` の `todayBackgroundColor()` も直接 `isSystemInDarkTheme()` を見ている）

Robolectric の `night` は設定修飾子なので、これを立てれば Configuration が一度で切り替わり
両方が同時に追従する。ファイル名側に持たせて `AppTheme(darkTheme = true)` だけを明示すると
`values-night/` が未選択のまま「暗い背景に暗い文字」という、アプリでは決して起こらない状態を
撮ることになる。

### error 状態は現時点で未カバー

`AppListViewModel` / `CalendarViewModel` のどちらにもエラー状態が無く、画面にもエラー分岐が無い。
撮るためだけに本番へエラー UI を足すのは本末転倒なので、`VrtState.ERROR` は定義だけして未使用に
している。本番にエラー状態が入ったらテストにメソッドを 1 つ足せばよい。

### 端末の修飾子

`VrtDevices` の Pixel 10 は生の Robolectric 修飾子で定義している
（`RobolectricDeviceQualifiers` に Pixel 10 のプリセットが無いため）。

```
w411dp-h923dp-normal-long-notround-any-{night|notnight}-420dpi-keyshidden-nonav
```

Pixel 10 は 1080 x 2424 px / 約 422ppi → 420dpi バケット（density 2.625）。
幅 `1080 / 2.625 = 411.4 → 411dp`、高さ `2424 / 2.625 = 923.4 → 923dp`。
出力画像は `411 x 2.625 = 1078`、`923 x 2.625 = 2422` で **1078 x 2422** になる。
dp が整数なので実機の 1080 x 2424 にはぴったり一致しない。

ダークモードは同じ文字列に `night` として埋め込むこと。`setQualifiers` を
「基本の修飾子」→「`+night`」と 2 回に分けて呼ぶと、2 回目で Robolectric が現在の
Configuration から修飾子文字列を作り直す過程で dp が丸め落ちし、ライトとダークで画像サイズが
2px ずれる。

## 制約

### 期待画像は記録した環境に固有

Robolectric のネイティブ描画はフォントのアンチエイリアスが OS / アーキテクチャごとに変わる。
現在の期待画像は **macOS(arm64) + JDK 17 + Robolectric SDK 35** で記録されている。
別環境で `vrtVerify` すると全件差分になる。

開発者が増えたら、記録環境を一本化する（コンテナに閉じ込める、あるいは記録役を決める）必要がある。

### バージョンの固定

- **Roborazzi 1.47.0 から動かせない。** 1.48.0 以降は kotlin-stdlib 2.0.21 でビルドされており、
  Kotlin 1.9.24 のコンパイラが metadata version を弾く。Kotlin 2.x に上げるまでは 1.47.0 のまま。
  上げる際は `app/build.gradle.kts` の `vrtCollect` が読む結果 JSON のパスも直すこと
  （1.48.0 以降は `build/test-results/roborazzi/<variant>/results/` と variant セグメントが入る）。
- **Robolectric の SDK は 35 に固定**（`app/src/test/resources/robolectric.properties`）。
  `targetSdk` は 36 だが、Robolectric 4.16 で SDK 36 を動かすには JDK 21 が要る
  （SDK 36 の android jar が Java 21 でコンパイルされているため）。本プロジェクトは
  `jvmToolchain(17)` なのでテストは JDK 17 上で走る。
- **Compose は BOM 2024.06.00**。Roborazzi の描画は Compose UI 1.5 以上が前提で、
  Kotlin 1.9.24 + compose compiler 1.5.14 で使える上限が 2024.06.00。

依存を上げると期待画像も変わる（アンチエイリアスがずれる）。想定内なので `vrtRecord` して
コミットし直せばよい。
