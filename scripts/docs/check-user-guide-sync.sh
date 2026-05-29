#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
REQ_FUN="$ROOT_DIR/docs/requirements/game-requirements-functional.md"
REQ_NFR="$ROOT_DIR/docs/requirements/game-requirements-non-functional.md"
GUIDE="$ROOT_DIR/docs/guides/user-guide.md"

for file in "$REQ_FUN" "$REQ_NFR" "$GUIDE"; do
  if [[ ! -f "$file" ]]; then
    echo "[docs-sync] Missing file: $file" >&2
    exit 1
  fi
done

extract_implemented_ids() {
  local file="$1"
  local prefix="$2"
  awk '/^## Planned /{exit} {print}' "$file" \
    | grep -oE "${prefix}-[A-Z0-9-]+-[0-9]{3}" \
    | sort -u || true
}

extract_planned_ids() {
  local file="$1"
  local prefix="$2"
  awk 'f;/^## Planned /{f=1}' "$file" \
    | grep -oE "${prefix}-[A-Z0-9-]+-[0-9]{3}" \
    | sort -u || true
}

extract_guide_ids() {
  grep -oE '(REQ|NFR)-[A-Z0-9-]+-[0-9]{3}' "$GUIDE" | sort -u || true
}

implemented_req_ids="$(extract_implemented_ids "$REQ_FUN" "REQ")"
planned_req_ids="$(extract_planned_ids "$REQ_FUN" "REQ")"
implemented_nfr_ids="$(extract_implemented_ids "$REQ_NFR" "NFR")"
planned_nfr_ids="$(extract_planned_ids "$REQ_NFR" "NFR")"
guide_ids="$(extract_guide_ids)"

implemented_all="$(printf '%s\n%s\n' "$implemented_req_ids" "$implemented_nfr_ids" | sed '/^$/d' | sort -u)"
planned_all="$(printf '%s\n%s\n' "$planned_req_ids" "$planned_nfr_ids" | sed '/^$/d' | sort -u)"
known_all="$(printf '%s\n%s\n' "$implemented_all" "$planned_all" | sed '/^$/d' | sort -u)"

status=0

while IFS= read -r id; do
  [[ -z "$id" ]] && continue
  if ! grep -q "\b${id}\b" "$GUIDE"; then
    echo "[docs-sync] Missing implemented ID in user guide: $id" >&2
    status=1
  fi
done <<< "$implemented_all"

while IFS= read -r id; do
  [[ -z "$id" ]] && continue
  if grep -q "\b${id}\b" "$GUIDE"; then
    echo "[docs-sync] Planned-only ID must not appear in user guide: $id" >&2
    status=1
  fi
done <<< "$planned_all"

while IFS= read -r id; do
  [[ -z "$id" ]] && continue
  if ! grep -qx "$id" <<< "$known_all"; then
    echo "[docs-sync] Unknown ID in user guide (not found in requirements): $id" >&2
    status=1
  fi
done <<< "$guide_ids"

if [[ "$status" -eq 0 ]]; then
  echo "[docs-sync] OK: user guide is synchronized with functional and non-functional requirements."
fi

exit "$status"
