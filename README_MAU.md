# MAU (Monthly Active Users) データ取得機能

## 概要

MinimalLauncherAppの直近1ヶ月のMAU（月間アクティブユーザー）を取得するための機能です。

## 使用方法

### 1. ローカル実行

```bash
# 依存関係をインストール
npm install

# MAUデータを取得
npm run mau
```

### 2. 必要な環境変数

以下の環境変数を設定してください：

```bash
export GOOGLE_CLIENT_EMAIL="your-service-account@project.iam.gserviceaccount.com"
export GOOGLE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n"
export GA_PROPERTY_ID="your-ga4-property-id"
```

## 出力結果例

```json
{
  "期間": "2024-07-07 ～ 2024-08-07",
  "プロパティID": "250425868",
  "データ": {
    "202407": {
      "アクティブユーザー": 1250,
      "新規ユーザー": 380,
      "セッション数": 2100
    },
    "202408": {
      "アクティブユーザー": 1100,
      "新規ユーザー": 320,
      "セッション数": 1850
    }
  },
  "合計": {
    "アクティブユーザー": 2350,
    "新規ユーザー": 700,
    "セッション数": 3950
  }
}
```

## GitHub Actions での実行制限について

### 実行可能な条件

✅ **動作する場合:**
- GitHub Secretsに適切な認証情報が設定されている
- サービスアカウントがGoogle Analytics 4プロパティへのアクセス権限を持っている
- `claude.yml`で`mcp-server-google-analytics`が適切に設定されている

### 実行できない理由

❌ **動作しない可能性がある理由:**

1. **認証情報の不備**
   - `GOOGLE_CLIENT_EMAIL`, `GOOGLE_PRIVATE_KEY_BASE64`, `GA_PROPERTY_ID`のいずれかがSecretに設定されていない
   - Base64エンコードされた秘密鍵のデコードに失敗

2. **権限エラー**
   - サービスアカウント`ga4-claude-access@minimal-launcher-c1598.iam.gserviceaccount.com`にGoogle Analytics 4プロパティ（ID: 250425868）の読み取り権限がない
   - IAMロールが適切に設定されていない

3. **ネットワーク制限**
   - GitHub Actionsランナーから Google Analytics API への接続が制限されている（稀）

4. **MCPサーバーの問題**
   - `mcp-server-google-analytics`パッケージのインストールに失敗
   - MCPサーバーとの通信エラー

### 推奨される対処法

1. **認証情報の確認:**
   ```bash
   # GitHub Secretsに以下が設定されているか確認
   - GOOGLE_CLIENT_EMAIL
   - GOOGLE_PRIVATE_KEY_BASE64 (Base64エンコード済み)
   - GA_PROPERTY_ID
   ```

2. **Google Cloud Console での権限確認:**
   - サービスアカウントがGoogle Analytics 4プロパティにアクセス可能か確認
   - 必要な権限: "Analytics Viewer" 以上

3. **手動テスト:**
   - ローカル環境で`npm run mau`を実行してテスト
   - 認証と権限が正常に動作することを確認

## ファイル構成

- `mau_analytics.js` - メインのMAU取得スクリプト
- `package.json` - Node.js依存関係管理
- `.github/workflows/claude.yml` - GitHub Actions設定（MCP設定含む）