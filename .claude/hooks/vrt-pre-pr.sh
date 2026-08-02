#!/bin/bash
# PreToolUse(Bash) フック。`gh pr create` を検知したときだけ VRT を実行し、
# UI に差分があれば PR 作成をブロックする。
#
# 差分の有無に関わらず vrt/actual/ と vrt/diff/ は生成される。
# 詳細は docs/vrt.md、レビュー手順は /vrt スキル。

set -uo pipefail

input=$(cat)
command=$(printf '%s' "$input" | jq -r '.tool_input.command // ""')

# `gh pr create` 以外の Bash 呼び出しには一切干渉しない。
# \s は macOS の BSD 系ツールで効かないので [[:space:]] を使う。
if ! printf '%s' "$command" | grep -Eq 'gh[[:space:]]+pr[[:space:]]+create'; then
  exit 0
fi

repo_root=$(cd "$(dirname "$0")/../.." && pwd)
cd "$repo_root" || exit 0

if ! output=$(./gradlew :app:vrtVerify --console=plain 2>&1); then
  diffs=$(find vrt/diff -name '*.png' 2>/dev/null | sed "s|^vrt/diff/|  - |" | sort)
  reason="VRT で UI の差分を検出したため PR 作成を止めました。

差分のあった画面:
${diffs:-  (差分画像なし。下記の Gradle 出力を確認してください)}

vrt/diff/ の画像を Read で確認してください（左から 期待 | 差分 | 現状）。
  - 意図した UI 変更なら: ./gradlew :app:vrtRecord で vrt/expect/ を更新してコミットし、再度 PR を作成する
  - 表示崩れなら: 実装を修正する
レビュー手順は /vrt スキルを使うと楽です。

--- gradle 出力(末尾) ---
$(printf '%s' "$output" | tail -25)"

  jq -n --arg reason "$reason" '{
    hookSpecificOutput: {
      hookEventName: "PreToolUse",
      permissionDecision: "deny",
      permissionDecisionReason: $reason
    }
  }'
  exit 0
fi

exit 0
