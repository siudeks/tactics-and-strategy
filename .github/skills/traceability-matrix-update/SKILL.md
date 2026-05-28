---
name: traceability-matrix-update
description: 'Ensure documentation traceability is updated when code changes are made. Use when implementing features, bug fixes, refactors, or tests that affect requirements mapping. Keywords: traceability, traceability-matrix, requirements mapping, docs update, code change.'
argument-hint: 'Provide: changed files, requirement IDs (if known), and whether behavior changed or was only refactored.'
user-invocable: true
---

# Traceability Matrix Update

## Outcome
Ensure every code change is reflected in `docs/engine/traceability-matrix.md` so requirement-to-implementation mapping stays current and auditable.

## When To Use
- Any feature implementation.
- Any bug fix.
- Any refactor that changes structure, behavior, or test coverage.
- Any test addition/removal that changes requirement coverage evidence.

## Inputs To Collect
- List of changed files and short change intent.
- Requirement IDs already known from task context (for example `REQ-MAP-001`).
- Whether behavior changed or only internal structure changed.
- Whether existing traceability links were added, removed, or replaced.

## Procedure
1. Classify the change.
- Functional change: logic/output/flow changed.
- Structural change: architecture, naming, packaging, or decomposition changed.
- Verification change: tests updated with no production logic change.

2. Identify affected requirements.
- Parse existing requirement IDs from task description, docs, tests, and current matrix.
- If IDs are missing, infer candidate rows by reading nearby entries in the matrix and linked docs.
- If no confident mapping exists, stop and require user confirmation before proceeding.

3. Map code evidence.
- For each affected requirement, list current implementation evidence:
  - production code files
  - tests that verify it
  - relevant design/spec docs when applicable
- Remove stale file references no longer representing implementation.

4. Update `docs/engine/traceability-matrix.md`.
- Add or update rows for changed requirements.
- Keep existing table format, naming, and ordering conventions.
- Keep wording concise and verifiable.

5. Validate consistency.
- Every changed code area has at least one matching requirement mapping or explicit rationale.
- Every updated matrix reference points to existing paths.
- No duplicate or contradictory row entries.

6. Completion gate.
- Do not mark the task complete until matrix update is done, reviewed, and saved.
- If any changed file (including tests) cannot be mapped to a requirement with confidence, block completion and request explicit confirmation.

## Decision Points
- Does the code change alter behavior?
  - yes -> update requirement mapping and evidence.
  - no -> check if structural/test evidence still requires matrix updates.
- Is there an existing row for the requirement?
  - yes -> update row evidence.
  - no -> add a new row in the proper section.
- Is requirement mapping ambiguous?
  - yes -> halt completion and request confirmation.
  - no -> finalize update.

## Quality Criteria
- Matrix reflects the current codebase after the change.
- Requirement IDs are precise and non-duplicated.
- Evidence references are real, relevant, and not stale.
- Documentation change is delivered in the same change set as code.

## Completion Checklist
- Classified the change type.
- Confirmed affected requirement IDs.
- Updated `docs/engine/traceability-matrix.md`.
- Removed stale references and added current evidence.
- Checked for duplicates/inconsistencies.
- Verified that task is not finalized before documentation update.

## Example Prompts
- "Implement combat modifier calculation and update traceability matrix entries for affected requirements."
- "Refactor turn engine order resolution and synchronize docs/engine/traceability-matrix.md with new class and test locations."
- "Add tests for movement validation and update requirement evidence in traceability-matrix.md."
