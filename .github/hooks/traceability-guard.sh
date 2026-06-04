#!/usr/bin/env bash
set -euo pipefail

changed_files=${TRACEABILITY_GUARD_CHANGED_FILES:-$(git diff --name-only --relative HEAD --)}

if [[ -z "$changed_files" ]]; then
  exit 0
fi

if ! grep -q '^core/src/main/java/' <<< "$changed_files"; then
  exit 0
fi

if grep -q '^docs/engine/traceability-matrix.md$' <<< "$changed_files"; then
  exit 0
fi

cat <<'JSON'
{
  "stopReason": "Traceability matrix update required",
  "systemMessage": "Changes under core/src/main/java were detected, but docs/engine/traceability-matrix.md was not modified. Update the traceability matrix before ending the task."
}
JSON

exit 2
