---
name: java-coding-style
description: 'Apply Java 21 coding style when Java code is generated, refactored, or updated.'
argument-hint: 'Provide: task goal, changed files/scope, compatibility constraints, and test expectations.'
user-invocable: true
---

# Java Coding Style

## Outcome
Produce clear, maintainable Java 21 code that matches project conventions across new code, refactors, and incremental updates.

## When To Use
- Generating new Java code.
- Refactoring existing Java code.
- Updating existing Java code during bug fixes or feature work.
- Normalizing style in touched areas without changing behavior.

## Inputs To Collect
- Primary goal: feature, bug fix, refactor, or maintenance update.
- Java baseline/version (default: Java 21).
- Compatibility constraints: API, serialization, persistence, and framework expectations.
- Test scope and risk level for changed areas.

## Procedure
1. Classify the target type.
- Value object / DTO / config snapshot: candidate for `record`.
- Stateful component (service, repository, controller, runtime object): usually `class`.
- Framework constraints:
  - JPA `@Entity` types should remain classes (mutable lifecycle, no-arg constructor expectations).
  - Spring `@ConfigurationProperties` can be records in Spring Boot 2.6+.
  - Mockito support may affect whether records are practical in tests.

2. Decide `record` vs `class`.
- Use `record` for immutable value carriers with value-based identity.
- Use `class` when mutable lifecycle, framework proxying, or richer behavior is required.

3. Apply typing and readability rules.
- Use explicit types in public APIs and domain declarations.
- Use `var` for local variables when the right-hand side makes the type obvious.
- Prefer names that communicate intent over overly short identifiers.
- Keep methods focused and avoid unnecessary nesting.

4. Apply Java 21 patterns deliberately.
- Prefer records for immutable data carriers.
- For records with multiple creation parameters, prefer a static `of(...)` factory methods instead of multiple constructors.
- Use the canonical record constructor only to validate and enforce invariants.
- Use switch expressions, pattern matching, and sealed hierarchies where they improve clarity.
- Keep collection immutability explicit (`List.copyOf`, `Set.copyOf`, `Map.copyOf`) when required by the model.
- Preserve existing behavior and public contracts unless the task explicitly changes them.

5. Follow nullability policy.
- Treat non-null as default in `@NullMarked` code.
- Use `@Nullable` only when null is intentional.
- Avoid redundant internal `Objects.requireNonNull` checks where types already guarantee non-null.
- Keep runtime validation at trust boundaries (deserialization, external API input, interop).

6. Keep changes scoped and maintainable.
- Respect existing package structure and naming.
- Add comments only for non-obvious logic.
- Avoid introducing new dependencies unless justified by clear value.

7. Verify with tests.
- Update or add tests for behavior touched by the change.
- Prefer focused test runs first, then broader verification as needed.

## Decision Points
- Is the type immutable data with value semantics?
  - yes -> `record`
  - no -> `class`
- Is there a framework/runtime constraint (for example JPA proxies)?
  - yes -> prefer `class`
  - no -> keep prior choice
- Is local variable type obvious from assignment?
  - yes -> `var` allowed
  - no -> explicit local type
- Does the change alter a public contract or serialized shape?
  - yes -> preserve compatibility or include explicit migration
  - no -> proceed with internal cleanup

## Quality Criteria
- Code matches Java 21 and repository style.
- `record`/`class` choice is intentional and justified.
- Public API and serialization compatibility are respected.
- Nullability annotations and checks align with project policy.
- Tests cover the modified behavior.

## Completion Checklist
- Confirmed change type: generation, refactor, or update.
- Confirmed compatibility constraints.
- Applied style updates in touched scope.
- Kept behavior unchanged unless requested otherwise.
- Added or updated tests where needed.
- Verified compilation/tests for affected modules.

## Example Prompts
- "Generate a new Java service and DTOs following repository coding style."
- "Refactor this Java class to improve readability while preserving behavior."
- "Update this Java module and keep style consistent with Java 21 conventions."
