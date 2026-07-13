---
name: java-jspecify-boundary-nullability
description: 'Apply Java nullability policy with JSpecify + NullAway: assume non-null in @NullMarked code, validate only at external boundaries (JSON/DB/APIs), and avoid redundant Objects.requireNonNull checks. Keywords: java, nullability, jspecify, nullaway, errorprone, nullmarked, deserialization, json, database.'
argument-hint: 'Provide the target Java file(s), data boundary type (JSON/DB/API), and whether package nullability markers need updates.'
user-invocable: true
---

# Java JSpecify Boundary Nullability

## Outcome
Implement or review Java code so nullability is enforced by JSpecify type annotations and package markers, with runtime null checks only at trust boundaries.

## When To Use
- Adding or refactoring Java 21 source and tests in this repository.
- Handling deserialization or data mapping from JSON, database rows, network payloads, or other external inputs.
- Fixing NullAway or Error Prone findings without introducing redundant runtime checks.

## Policy Summary
- Assume values are non-null in `@NullMarked` code unless a type is explicitly annotated nullable.
- Use null checks at trust boundaries only (deserialization, external APIs, database reads, interop).
- Model nullability in types and annotations first.
- Never add `Objects.requireNonNull(...)` when non-null is already guaranteed by JSpecify annotations and context.

## Default Decisions
- Scope: workspace skill under `.github/skills`.
- `@NullMarked` markers: add by default when safe and package-consistent.
- Optional values: choose representation contextually based on existing team/module style.

## Procedure
1. Identify the trust boundary.
- External input path (JSON parser, DB mapper, wire format, framework callback): boundary checks are allowed.
- Internal domain/service flow under typed contracts: boundary checks are not needed.

2. Verify nullability context.
- Confirm package/class is under `@NullMarked` policy.
- If missing and appropriate for the package, add or align package marker by default.

3. Encode nullability in types.
- Mark truly optional values as nullable in signatures and fields.
- Keep required values non-null by default under `@NullMarked`.
- Prefer annotation-driven contracts over defensive runtime checks.

4. Apply checks only at external boundaries.
- Validate or normalize potentially null raw inputs where they enter the system.
- Convert boundary data into well-typed internal models before deeper logic runs.

5. Remove or avoid redundant checks.
- Do not add `Objects.requireNonNull(...)` for parameters/values already non-null by contract.
- Remove defensive null guards that conflict with or duplicate JSpecify guarantees.

6. Keep behavior and diagnostics clear.
- If boundary validation fails, fail fast with context-appropriate error handling.
- Keep nullability intent obvious from method signatures and annotations.

7. Verify with tooling.
- Run compile/static checks and ensure NullAway + Error Prone pass.
- Update tests when boundary behavior changes.

## Decision Points
- Is the value from an external world (JSON, DB, untyped API, interop)?
  - yes -> validate at ingress, then map to typed internal model
  - no -> rely on JSpecify contract and avoid runtime null checks

- Is a value truly optional in business semantics?
  - yes -> represent as nullable type (or existing project-preferred optional abstraction)
  - no -> keep non-null type and do not add defensive null checks

- Is `Objects.requireNonNull(...)` being considered?
  - yes -> only allow when proving non-null cannot be established via annotations/types at that boundary
  - no -> continue

## Quality Criteria
- Nullability intent is expressed in type annotations and package markers.
- Runtime null checks exist only at trust boundaries.
- No redundant `Objects.requireNonNull(...)` in non-null-guaranteed code paths.
- Static analysis (NullAway/Error Prone) passes without suppression-driven workarounds.
- Tests cover boundary null handling where applicable.

## Completion Checklist
- Trust boundary identified for each changed data path.
- `@NullMarked` context confirmed or updated where needed.
- Nullable vs non-null contracts reflected in signatures.
- Boundary validation added only where external data enters.
- Redundant `Objects.requireNonNull(...)` removed or avoided.
- Relevant tests/build checks pass.

## Example Prompts
- "Apply JSpecify nullability cleanup to this mapper; only keep checks at JSON boundary."
- "Refactor this service to remove redundant Objects.requireNonNull and rely on @NullMarked contracts."
- "Review this package for missing nullability markers and boundary-only null checks."
