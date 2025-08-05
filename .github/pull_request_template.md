## 📋 概要
<!-- AI Agent 向け：変更の概要を 1-2 文で記載 -->

## 🎯 変更種別
<!-- 該当するものにチェック -->
- [ ] ✨ 新機能 (New Feature)
- [ ] 🐛 バグ修正 (Bug Fix)
- [ ] 🎨 UI/UX 改善 (UI/UX)
- [ ] ⚡ パフォーマンス改善 (Performance)
- [ ] ♻️ リファクタリング (Refactoring)
- [ ] 📚 ドキュメント (Documentation)
- [ ] 🔧 設定変更 (Configuration)
- [ ] ⬆️ 依存関係更新 (Dependencies)
- [ ] 🧪 テスト追加/修正 (Testing)

## 📝 変更詳細
<!-- AI Agent 向け：主要な変更点を箇条書きで記載 -->
### 主な変更ファイル
- 
- 
- 

### 実装内容
<!-- 実装の詳細を記載 -->

## 🔍 テスト内容
### 自動テスト
- [ ] ユニットテスト実行済み
- [ ] UI テスト実行済み
- [ ] Lint チェック通過

### 手動テスト
<!-- 実施した手動テストを記載 -->
- [ ] Android 8.0 (API 26) での動作確認
- [ ] Android 13 (API 33) での動作確認
- [ ] 
- [ ] 

### テストコマンド
```bash
# 実行したテストコマンド
./gradlew test
./gradlew connectedAndroidTest
```

## 📱 スクリーンショット/動画
<!-- UI 変更がある場合は必須 -->
| Before | After |
|--------|-------|
| スクリーンショット | スクリーンショット |

## ⚠️ 破壊的変更
<!-- 後方互換性に影響する変更がある場合 -->
- [ ] なし
- [ ] あり（詳細：）

## 📊 パフォーマンス影響
<!-- パフォーマンスへの影響を記載 -->
- [ ] 影響なし
- [ ] 改善（詳細：）
- [ ] 低下の可能性（詳細と対策：）

## 🚀 デプロイ前チェックリスト
- [ ] コードは自己レビュー済み
- [ ] コメント追加（複雑なロジック部分）
- [ ] ドキュメント更新（必要な場合）
- [ ] 新規依存関係の妥当性確認
- [ ] セキュリティ考慮事項の確認
- [ ] アクセシビリティ考慮（UI 変更時）

## 🔗 関連情報
<!-- 関連する Issue、PR、ドキュメントへのリンク -->
- Issue: #
- 関連 PR: #
- ドキュメント: 

## 💡 レビュアーへのコメント
<!-- レビュー時に特に確認してほしい点 -->

## 🤖 AI Agent メタデータ
<!-- AI が自動生成する構造化データ -->
```yaml
android:
  min_sdk: 26
  target_sdk: 33
  affected_modules:
    - app
  build_variants:
    - debug
    - release
  
testing:
  unit_tests_added: 0
  unit_tests_modified: 0
  coverage_change: "+0%"
  
dependencies:
  added: []
  updated: []
  removed: []

risk_level: low # low | medium | high
review_priority: normal # low | normal | high | critical
```

---
<!-- PR 作成後の自動チェック項目 -->
## ✅ GitHub Actions チェック
- [ ] Build ワークフロー
- [ ] Test ワークフロー
- [ ] JaCoCo カバレッジ（最小: 全体 40%、変更ファイル 60%）