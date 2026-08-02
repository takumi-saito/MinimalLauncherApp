# ADR-0002: VRT に Roborazzi + Robolectric を採用する

**Status**: Accepted
**Date**: 2026-08-03
**Deciders**: @takumi-saito

## Context

UI の表示崩れを検知する手段が無く、崩れは人間が実機で気付くしかなかった。
検出したいのは「意図した UI 変更」と「考慮漏れ・表示崩れ」の切り分けであり、
期待画像（expect）と現状画像（actual）を比較する Visual Regression Testing が要る。

要件:

- 画面単位で実行できること
- 成果物から実行端末・画面名・状態名が判別できること（`vrt/<device>/<screen>-<state>.png`）
- 期待画像は git 管理し、意図した UI 変更のときだけ更新してコミットする
- 現状画像は PR 作成のタイミングで生成する
- CI は使わない。expect / actual ともローカル（macOS）で完結させる

決定時点のプロジェクト構成: AGP 8.10.1、Kotlin 1.9.24、compose compiler 1.5.14、
compose-bom 2022.10.00（Compose UI 1.3.0）、compileSdk / targetSdk 36、
Java 17（`jvmToolchain(17)`）、Hilt 2.51.1。

## Decision

**Roborazzi 1.47.0 + Robolectric 4.16.1** を採用し、JVM 上（Robolectric）で
スクリーンショットを撮る。エミュレータは使わない。

決め手は、Roborazzi がキャプチャごとに `golden_file_path` / `actual_file_path` /
`compare_file_path` を含む JSON マニフェストを出すこと。これがあるおかげで、
要件どおりの `vrt/expect|actual|diff/<device>/<screen>-<state>.png` レイアウトへ
Gradle タスク側で確実にマッピングできる。

**バージョンは 1.47.0 に固定する（最新は 1.70.0）。**
`roborazzi-compose` の Kotlin メタデータを確認したところ、1.48.0 以降は
kotlin-stdlib 2.0.21 でビルドされており（1.70.0 は `mv = {2, 3, 0}`）、
Kotlin 1.9.24 のコンパイラが弾く。1.47.0 は `mv = {1, 9, 0}` / kotlin-stdlib 1.9.22 で、
Kotlin 1.9.24 と互換な最終版。本計画で使う API は 1.47.0 と 1.70.0 で同一。

採用しなかった選択肢:

**Paparazzi 2.0.0-alpha05** — layoutlib で描画するため Compose のバージョン制約が緩く、
後述の Compose バンプを避けられる可能性があった。しかし alpha であること、AGP の
layoutlib と密結合していること、そして Kotlin 2.x メタデータで Roborazzi と同じ壁に
当たること（Kotlin 1.9 対応の最終版は AGP 8.10 をサポートしない）から見送った。
`verify` の出力も golden→actual→diff の対応を示すマニフェストが無く、ファイル名の
慣習からパスを再構成する必要がある。

**`com.android.compose.screenshot`（AGP 公式プラグイン）** — golden とレポートのパスが
FQCN + メソッド名から導出され固定であり、`vrt/<device>/<screen>-<state>.png` へ到達できない。
機械可読なパス一覧も出ないため後付けのリネームも困難。`@Preview` 関数しか描画しないので、
状態ごとに `main` へ Preview を増やす必要もある。

**instrumented テスト + Gradle Managed Devices** — 要件の「実行端末」を実機/エミュレータ
として最も素直に満たすが、8 枚の画像のためにエミュレータを起動するのは割に合わない。
10〜20 倍遅く、VRT が不安定になる典型的な原因でもある。

**前提として compose-bom を 2022.10.00 → 2024.06.00 に引き上げる。**
Roborazzi の描画は Compose UI 1.5 以上が前提で、1.3.0 では動かない。2024.06.00
（UI 1.6.8 / Material3 1.2.1）が Kotlin 1.9.24 + compose compiler 1.5.14 で使える上限
（Compose 1.7 以降は Kotlin 2.0 の Compose コンパイラプラグインを要求する）。

**Robolectric の SDK は 35 に固定する。**
`targetSdk` は 36 だが、Robolectric 4.16 で SDK 36 を動かすには JDK 21 が要る
（SDK 36 の android jar が Java 21 でコンパイルされているため）。本プロジェクトは
`jvmToolchain(17)` なのでテストは JDK 17 上で走る。

運用手順・ディレクトリ構成・撮影条件の設計は [docs/vrt.md](../vrt.md) に置く。

## Consequences

**Good**:
エミュレータ不要で、8 枚のキャプチャと比較が数秒で終わる。`gh pr create` に
PreToolUse フックを掛けて自動実行できる程度に軽い。
Robolectric の設定修飾子でダークモードを切り替えると、このアプリが持つ 2 系統の
ダーク判定（`AppTheme(darkTheme=)` と `values-night/` リソース）が同時に正しく
切り替わるため、`@Preview` を使う場合より実機に忠実な画像が撮れる。
副次的に、Compose を 2 年ぶんキャッチアップでき、compose compiler 1.5.14 と
runtime 1.3.0 という不整合も解消された。

**Bad**:
Compose のバンプが必須になり、Material3 1.0.0 → 1.2.1 のデフォルトトークン変更で
アプリの見た目が動くリスクを負った。
Roborazzi 1.47.0 から動かせない — 上げるには Kotlin 2.x への移行が要る。
期待画像は記録環境に固有になる。Robolectric のネイティブ描画はフォントのアンチ
エイリアスが OS / アーキテクチャで変わるため、macOS(arm64) + JDK 17 + SDK 35 以外で
`vrtVerify` すると全件差分になる。
Compose / Robolectric / JDK を上げるたびに期待画像の取り直しが発生する。

**Mitigations**:
Compose バンプは独立コミットにし、期待画像を記録する前に実機でライト / ダーク両方を
目視確認する。見た目が動いて困る場合は BOM 2023.08.00（UI 1.5.0 / Material3 1.1.1）に
フォールバックできる（Compose 1.5 以上という Roborazzi の下限は満たす）。
Roborazzi のバージョン固定は `build.gradle` と `app/build.gradle.kts` にコメントで
理由を残し、上げる際に直すべき箇所（結果 JSON のパスに variant セグメントが入る）も
併記した。
記録環境の固有性は、現状が単独開発なので受け入れる。開発者が増えたら記録環境を
一本化する必要があり、その旨を [docs/vrt.md](../vrt.md) に明記した。
依存更新のたびの取り直しは `vrtRecord` + コミットで済むため、緩和策は取らない。
