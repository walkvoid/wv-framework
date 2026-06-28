#!/usr/bin/env sh
# 自动移除 Co-authored-by 尾注（含 Cursor 自动注入）
msg_file="$1"
[ -f "$msg_file" ] || exit 0
tmp=$(mktemp)
grep -vi '^Co-authored-by:' "$msg_file" > "$tmp" || true
mv "$tmp" "$msg_file"
exit 0
