---
name: java-no-internal-requirenonnull
description: 'Prevent redundant Objects.requireNonNull generation in @NullMarked Java code. Allow runtime null checks only at trust boundaries (deserialization, DB/API adapters, interop). Keywords: java, jspecify, nullaway, errorprone, requireNonNull, nullability, nullmarked.'
argument-hint: 'Provide target Java files and classify each path as internal flow or trust boundary.'
user-invocable: true
---

# Java No Internal requireNonNull

## Outcome
Generate or review Java changes without adding redundant `Objects.requireNonNull(...)` in internal `@NullMarked` code paths.

## When To Use
- Any Java feature, bugfix, or refactor in this repository.
- Reviews where generated code added defensive null checks.
- NullAway/Error Prone cleanup under JSpecify mode.

## Policy Summary
- Internal typed flows under `@NullMarked`: do not add `Objects.requireNonNull(...)`.
- Trust boundaries: runtime null validation is allowed and often required.
- Express optionality in types (`@Nullable`) instead of internal defensive checks.
- Prefer compile-time nullability contracts over runtime checks.

## Trust Boundary Examples
- JSON or YAML deserialization and parsing adapters.
- Database row mapping or external storage adapters.
- HTTP/API request payload mapping.
- Reflection, plugin loading, JNI, or external interop.
- Untyped maps, dynamic payloads, and framework callbacks with weak contracts.

## Procedure
1. Classify each changed location.
- Boundary file/module: checks may be used while normalizing ingress data.
- Internal domain/service logic: checks are redundant and should not be added.

2. Confirm nullability context.
- Verify package or class is `@NullMarked`.
- Keep non-null by default and annotate only intentional nullable values.

3. Enforce generation rule.
- Reject adding `Objects.requireNonNull(...)` for internal parameters, record components, or local variables.
- If null appears possible internally, fix types/annotations instead of adding runtime guards.

4. Keep boundary checks purposeful.
- Validate once at ingress.
- Convert to typed internal models, then rely on type guarantees downstream.

5. Verify.
- Ensure NullAway + Error Prone remain clean.
- Update tests when boundary null-handling behavior changes.

## Decision Points
- Is this value entering from outside the typed system?
  - yes -> boundary validation is allowed
  - no -> do not add `Objects.requireNonNull(...)`

- Is null business-valid?
  - yes -> model with `@Nullable`
  - no -> keep non-null contract and remove defensive guard

- Is a generated check in internal flow present?
  - yes -> remove it and keep checker-clean annotations/types
  - no -> continue

## Quality Criteria
- No redundant `Objects.requireNonNull(...)` in internal `@NullMarked` code.
- Boundary checks are localized to ingress points.
- Nullability intent is visible in type annotations.
- Static analysis remains warning-clean under project settings.

## Completion Checklist
- Classified modified files as boundary or internal.
- Removed or avoided internal `Objects.requireNonNull(...)`.
- Kept boundary validation only where external data enters.
- Confirmed types/annotations encode optionality.
- Verified build/static checks for changed area.

## Example Prompts
- "Refactor generated Java code to remove internal Objects.requireNonNull and keep only boundary validation."
- "Review this service package for redundant requireNonNull usage under @NullMarked."
- "Apply nullability cleanup: trust-boundary checks only, annotations for optional values."