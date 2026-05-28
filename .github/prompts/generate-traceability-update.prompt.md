---
description: "Generate proposed traceability matrix updates from modified files. Use when you want draft entries or affected REQ mappings for docs/engine/traceability-matrix.md after code or test changes."
name: "Generate Traceability Update"
argument-hint: "Provide modified files, change summary, and known REQ IDs if any."
agent: "agent"
model: "GPT-5 (copilot)"
---
Generate a concise draft update for `docs/engine/traceability-matrix.md` based on the supplied modified files and change summary.

Required behavior:
- Identify likely affected requirement IDs from the provided context, nearby docs, tests, and current traceability matrix.
- Separate confident mappings from ambiguous mappings.
- For ambiguous mappings, ask focused follow-up questions instead of inventing REQ IDs.
- Produce output that is ready to paste into the traceability matrix or easy to convert into a final edit.

Output format:
## Affected Requirements
- One bullet per confidently mapped requirement ID with a one-line reason.

## Proposed Matrix Updates
- One bullet per requirement containing:
  - requirement ID
  - implementation evidence files
  - verification evidence files
  - short rationale

## Ambiguities
- List any files or behaviors that could not be mapped confidently.
- If there are no ambiguities, say `None`.

## Follow-up Questions
- Ask only the minimum questions needed to resolve ambiguous mappings.
- If none are needed, say `None`.

Constraints:
- Do not claim a requirement mapping without evidence.
- Prefer existing matrix wording and nearby requirement IDs over inventing new labels.
- Treat test-only changes as traceability-relevant when they affect verification evidence.
