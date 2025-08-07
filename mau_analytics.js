#!/usr/bin/env node

/**
 * MAU (Monthly Active Users) データ取得スクリプト
 * Google Analytics 4 API を使用して直近1ヶ月のMAUを取得
 */

const { google } = require('googleapis');
const path = require('path');

class MAUAnalytics {
  constructor() {
    this.propertyId = process.env.GA_PROPERTY_ID || '250425868';
    this.serviceAccountEmail = process.env.GOOGLE_CLIENT_EMAIL;
    this.privateKey = process.env.GOOGLE_PRIVATE_KEY;
    
    if (!this.serviceAccountEmail || !this.privateKey) {
      console.error('⚠️  Google Analytics認証情報が設定されていません');
      console.error('必要な環境変数: GOOGLE_CLIENT_EMAIL, GOOGLE_PRIVATE_KEY');
      process.exit(1);
    }
  }

  async authenticate() {
    try {
      // サービスアカウント認証を設定
      this.auth = new google.auth.GoogleAuth({
        credentials: {
          client_email: this.serviceAccountEmail,
          private_key: this.privateKey.replace(/\\n/g, '\n'),
        },
        scopes: ['https://www.googleapis.com/auth/analytics.readonly'],
      });

      this.analytics = google.analyticsdata('v1beta');
      console.log('✅ Google Analytics認証完了');
      return true;
    } catch (error) {
      console.error('❌ 認証エラー:', error.message);
      return false;
    }
  }

  async getMAUData() {
    try {
      console.log('📊 MAUデータを取得中...');
      
      // 直近1ヶ月の期間を設定
      const endDate = new Date();
      const startDate = new Date();
      startDate.setMonth(startDate.getMonth() - 1);

      const request = {
        property: `properties/${this.propertyId}`,
        requestBody: {
          dimensions: [
            { name: 'month' },
          ],
          metrics: [
            { name: 'activeUsers' },
            { name: 'newUsers' },
            { name: 'sessions' },
          ],
          dateRanges: [
            {
              startDate: startDate.toISOString().split('T')[0],
              endDate: endDate.toISOString().split('T')[0],
            },
          ],
        },
        auth: this.auth,
      };

      const response = await this.analytics.properties.runReport(request);
      return this.formatMAUResults(response.data, startDate, endDate);
      
    } catch (error) {
      console.error('❌ MAUデータ取得エラー:', error.message);
      
      // 詳細なエラー情報を表示
      if (error.code === 403) {
        console.error('🔒 アクセス許可エラー - サービスアカウントにGoogle Analyticsプロパティへのアクセス権限がありません');
      } else if (error.code === 404) {
        console.error('🔍 プロパティが見つかりません - Property ID:', this.propertyId);
      }
      
      throw error;
    }
  }

  formatMAUResults(data, startDate, endDate) {
    const results = {
      期間: `${startDate.toISOString().split('T')[0]} ～ ${endDate.toISOString().split('T')[0]}`,
      プロパティID: this.propertyId,
      データ: {},
      合計: {
        アクティブユーザー: 0,
        新規ユーザー: 0,
        セッション数: 0
      }
    };

    if (data.rows && data.rows.length > 0) {
      data.rows.forEach(row => {
        const month = row.dimensionValues[0].value;
        const activeUsers = parseInt(row.metricValues[0].value) || 0;
        const newUsers = parseInt(row.metricValues[1].value) || 0;
        const sessions = parseInt(row.metricValues[2].value) || 0;

        results.データ[month] = {
          アクティブユーザー: activeUsers,
          新規ユーザー: newUsers,
          セッション数: sessions
        };

        results.合計.アクティブユーザー += activeUsers;
        results.合計.新規ユーザー += newUsers;
        results.合計.セッション数 += sessions;
      });
    }

    return results;
  }

  async run() {
    console.log('🚀 MAU Analytics 開始');
    console.log('📅 対象期間: 直近1ヶ月');
    console.log('🏢 プロパティID:', this.propertyId);
    console.log('---');

    if (await this.authenticate()) {
      try {
        const mauData = await this.getMAUData();
        
        console.log('📈 MAU取得結果:');
        console.log(JSON.stringify(mauData, null, 2));
        
        return mauData;
      } catch (error) {
        console.error('💥 MAUデータ取得に失敗しました');
        return null;
      }
    }
  }
}

// スクリプトが直接実行された場合
if (require.main === module) {
  const mauAnalytics = new MAUAnalytics();
  mauAnalytics.run()
    .then(data => {
      if (data) {
        console.log('✅ MAUデータ取得完了');
        process.exit(0);
      } else {
        console.log('❌ MAUデータ取得失敗');
        process.exit(1);
      }
    })
    .catch(error => {
      console.error('💥 予期しないエラー:', error);
      process.exit(1);
    });
}

module.exports = MAUAnalytics;