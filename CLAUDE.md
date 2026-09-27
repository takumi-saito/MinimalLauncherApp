# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## プロジェクト概要

MinimalLauncherApp は Android 向けのミニマリストランチャーアプリケーションです。Jetpack Compose と従来の View システムを組み合わせたハイブリッド構成で、Hilt による依存性注入を採用しています。

## アーキテクチャ上の意思決定（ADR）

**設計方針に関わる作業をする前に [docs/adr/README.md](docs/adr/README.md) の索引を確認すること。**

`docs/adr/` に「なぜそうしたか」「何を採らなかったか」が記録されている。コードからは読み取れない情報なので、以下の場合は必ず参照する。

- ライブラリやツールの選定を提案するとき（既に検討して見送られている可能性がある）
- テスト戦略、ディレクトリ構成、状態管理の方針に触れるとき
- 「なぜこうなっているのか」を問われたとき（実装を読む前に索引を見る）

ADR に書かれた決定に反する提案をする場合は、その ADR を読んだうえで「ADR-000N ではこう決まっているが、〜の理由で見直す価値がある」と、既存の決定を踏まえた形で提示する。

あとで覆すのに数日かかる決定をしたときは `/adr` スキルで記録する。数分で戻せる決定は書かない。

## 開発コマンド

### ビルド

```bash
# アプリのビルド
./gradlew build

# デバッグビルド
./gradlew assembleDebug

# リリースビルド
./gradlew assembleRelease

# クリーンビルド
./gradlew clean build
```

### テスト実行

```bash
# ユニットテスト実行
./gradlew test

# テストレポート生成付きでテスト実行
./gradlew test jacocoTestReport

# 特定のテストのみ実行
./gradlew test --tests "com.kireaji.minimallauncherapp.*"
```

### VRT（Visual Regression Testing）

```bash
# 期待画像と比較する（差分があれば失敗。vrt/actual と vrt/diff を生成）
./gradlew :app:vrtVerify

# 期待画像を再生成する（意図した UI 変更のとき。結果をコミットする）
./gradlew :app:vrtRecord
```

`gh pr create` 時に PreToolUse フックが `vrtVerify` を自動実行し、差分があれば PR 作成をブロックする。差分レビューから期待画像の更新までは `/vrt` スキルで進める。運用の詳細は [docs/vrt.md](docs/vrt.md)、採用理由は [ADR-0002](docs/adr/0002-roborazzi-for-vrt.md)。

### その他の開発タスク

```bash
# 依存関係の確認
./gradlew dependencies

# Firebase Crashlytics マッピングファイル ID 注入
./gradlew injectCrashlyticsMappingFileIdDebug
./gradlew injectCrashlyticsMappingFileIdRelease
```

## アーキテクチャ構成

### レイヤー構造

```
app/src/main/java/com/kireaji/minimallauncherapp/
├── ui/                  # プレゼンテーション層
│   ├── compose/         # Jetpack Compose 画面
│   ├── viewmodel/       # ViewModel クラス
│   └── Fragment/Activity # 従来の View システム
├── usecase/             # ビジネスロジック層
├── data/                # データ層
│   └── model/           # データモデル
└── di/                  # Hilt 依存性注入設定
```

### 主要コンポーネント

- **FullscreenActivity**: メインアクティビティ。ViewPager でカレンダーとアプリリストを切り替え
- **AppListFragment/AppListScreen**: インストール済みアプリ一覧表示（Compose）。インデックスバーによる高速ナビゲーション対応
- **CalenderFragment/CalendarScreen**: カレンダー表示（Compose）。Fragment のクラス名は `Calender`（綴り違い）なので検索時に注意
- **AppUseCase**: アプリケーション情報の取得・管理ロジック
- **Hilt DI**: `@HiltAndroidApp`、`@AndroidEntryPoint`、`@Module` によるモジュール構成

### 技術スタック

- **UI**: Jetpack Compose（compose-bom 2024.06.00 / Compose UI 1.6.8 / Material3 1.2.1）+ 従来の View システム（ViewPager）
- **DI**: Hilt 2.51.1（KSP）
- **ビルドツール**: Gradle 8.14.3、AGP 8.10.1、Kotlin 1.9.24、KSP 1.9.24-1.0.20、compose compiler 1.5.14
- **最小 SDK**: 26 / ターゲット SDK: 36 / コンパイル SDK: 36
- **Java バージョン**: 17（`jvmToolchain(17)`）
- **テスト**: JUnit 4.13.2、Mockito 3.12.4、Robolectric 4.16.1、Roborazzi 1.47.0（VRT）
- **Firebase**: Analytics、Crashlytics、Performance Monitoring

Kotlin は 1.9 系のため、Compose は 2024.06.00、Roborazzi は 1.47.0 が上限（理由は [ADR-0002](docs/adr/0002-roborazzi-for-vrt.md)）。これらを上げるには Kotlin 2.x への移行が先に必要。

### CI/CD

GitHub Actions（`.github/workflows/`）。**PR でビルドやテストを実行するワークフローは無い**。テストと VRT はローカルで実行する。

| ワークフロー | トリガー | 内容 |
|---|---|---|
| `android-debug-build.yml` | develop への push | `assembleDebug` |
| `android-release-build.yml` | develop への push | `bundleRelease`、AAB をアーティファクト化 |
| `deploy-google-play-console.yml` | release への push | `bundleRelease` → Google Play の internal トラックへデプロイ |
| `lint-autofix.yml` | 毎日 / 手動 | `lintDebug` の結果から Issue を起票 |
| `claude-review.yml` / `claude-security-review.yml` | PR | Claude によるレビュー |
| `claude.yml` / `claude-dev.yml` | Issue・コメント | Claude による対応 |
| `notion-pr-connector.yml` | PR | Notion タスクのステータス連携 |
| `metrics.yml` | PR / 毎週 / 手動 | メトリクス計測 |

共通のセットアップ（JDK 17、`google-services.json` の復元）は `.github/actions/setup-android-build` にある。