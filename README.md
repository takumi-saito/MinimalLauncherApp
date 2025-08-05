# MinimalLauncherApp

Android 向けのミニマリストランチャーアプリケーション

## Pages Link
https://takumi-saito.github.io/MinimalLauncherApp/

## 🤖 Claude Code GitHub Actions

このリポジトリでは、AI Agent (Claude) による自動化された開発支援を提供する 2 つの専門化された GitHub Actions ワークフローを使用しています。

### ワークフロー概要

| ワークフロー | ファイル                                  | 用途 | トリガー |
|------------|---------------------------------------|------|---------|
| **Claude Developer** | `.github/workflows/claude-dev.yml`    | 開発実装 | `@claude-dev` または `needs-implementation` ラベル |
| **Claude Reviewer** | `.github/workflows/claude-review.yml` | PR レビュー・相談 | `@claude` または `needs-review` ラベル |

### 📝 Claude Developer (開発実装用)

#### 概要
Issue に対する新機能実装、バグ修正、リファクタリングなどの開発タスクを自動実行します。

#### トリガー方法
- Issue コメントで `@claude-dev` をメンション
- Issue に `needs-implementation` ラベルを付与
- `takumi-saito` を assignee に設定

#### 主な機能
- ✨ 新機能の実装
- 🐛 バグの修正
- ♻️ コードのリファクタリング
- 📚 ドキュメントの更新
- 🧪 テストコードの作成

#### 利用可能なツール
```yaml
- Bash (gradlew, git コマンド)
- Read/Write (ファイル読み書き)
- Edit/MultiEdit (ファイル編集)
- Grep/Glob/LS (ファイル検索)
```

#### 使用例
```markdown
@claude-dev 

CalendarFragment を Jetpack Compose に移行してください。
既存の機能は維持しつつ、モダンな実装にリファクタリングお願いします。
```

### 👀 Claude Reviewer (レビュー・相談用)

#### 概要
Pull Request のコードレビュー、品質チェック、技術相談を行います。PR テンプレートの内容を自動解析し、構造化されたレビューを提供します。

#### トリガー方法
- PR コメントで `@claude` をメンション（デフォルト）
- PR に `needs-review` ラベルを付与
- PR の作成、編集、同期時に自動実行

#### 主な機能
- 📋 PR テンプレート解析
- 🔍 コード品質レビュー
- ⚠️ 潜在的問題の検出
- 💡 改善提案の提供
- 📊 メトリクス分析

#### 利用可能なツール（読み取り専用）
```yaml
- Bash (gradlew test/lint, git diff/log)
- Read (ファイル読み取り)
- Grep/Glob/LS (ファイル検索)
```

#### レビュー項目
- Kotlin/Java コーディング規約
- Jetpack Compose ベストプラクティス
- Hilt DI の適切な使用
- メモリ効率とパフォーマンス
- Android SDK 互換性（API 26-33）

#### 使用例
```markdown
@claude

この PR のコードをレビューしてください。
特にパフォーマンスとメモリ使用量の観点から確認お願いします。
```

### 🎯 使い分けガイドライン

#### Claude Developer を使用する場合
- 新しい機能を実装したい
- バグを修正する必要がある
- コードをリファクタリングしたい
- テストを追加・修正したい
- 実際にコードを編集する必要がある

#### Claude Reviewer を使用する場合
- PR のコードレビューが必要
- コード品質のチェックをしたい
- 技術的なアドバイスが欲しい
- 実装方針について相談したい
- 潜在的な問題を発見したい

### ⚙️ セットアップ

#### 必要な Secrets
GitHub リポジトリの Settings > Secrets and variables > Actions で以下を設定：

```
CLAUDE_CODE_OAUTH_TOKEN: Claude Code の OAuth トークン
GITHUB_TOKEN: 自動的に提供される（設定不要）
```

#### PR テンプレート
`.github/pull_request_template.md` に Android アプリ向けの PR テンプレートが用意されています。このテンプレートは Claude Reviewer が自動的に解析し、構造化されたレビューを提供するために使用されます。

### 📊 品質保証プロセス

両ワークフローとも以下の品質チェックを実行：

1. **Lint チェック**: `./gradlew lint`
2. **ユニットテスト**: `./gradlew test`
3. **ビルド確認**: `./gradlew assembleDebug`
4. **カバレッジ分析**: `./gradlew jacocoTestReport`

最小カバレッジ要件：
- 全体: 40%
- 変更ファイル: 60%

### 🔒 セキュリティと権限

- **Claude Developer**: フル編集権限（Issue のみで動作）
- **Claude Reviewer**: 読み取り専用（PR のみで動作）

これにより、レビュー時の意図しない変更を防ぎ、実装時には必要な権限を確保しています。

---

## プロジェクト概要

MinimalLauncherApp は、シンプルで使いやすい Android ランチャーアプリです。最小限の機能に絞り込むことで、高速な動作と低メモリ消費を実現しています。

### 技術スタック
- **言語**: Kotlin
- **UI**: Jetpack Compose (移行中) + 従来の View システム
- **アーキテクチャ**: MVVM
- **DI**: Hilt
- **最小 SDK**: 26 (Android 8.0)
- **ターゲット SDK**: 33 (Android 13)

### ビルド方法
```bash
# デバッグビルド
./gradlew assembleDebug

# リリースビルド
./gradlew assembleRelease

# テスト実行
./gradlew test

# Lint チェック
./gradlew lint
```

