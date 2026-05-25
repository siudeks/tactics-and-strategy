---
name: modern-java-generation
description: 'Generate modern Java code for Java 21 projects with full workflow guidance. Use when requests mention Java code generation, records, immutable domain models, refactoring to modern syntax, or cleaner DTO/value-object design. Keywords: java, records, rekordy, immutable, java 21, var, type inference.'
argument-hint: 'Provide: task goal, domain context, API compatibility constraints, and test scope.'
user-invocable: true
---

# Modern Java Generation

## Outcome
Produce Java code that uses modern, maintainable Java 21 constructs with deterministic and unambiguous style decisions.

## When To Use
- Generating new Java domain models, DTOs, value objects, services, and mappers.
- Refactoring existing Java code to modern style.
- Requests that explicitly mention records, rekordy, Java 21, modern syntax, or immutability.

## Inputs To Collect
- Primary goal: feature, bugfix, refactor, or code template.
- Java baseline/version (default: Java 21).
- Type style policy (default in this skill): explicit types by default; constrained `var` in local scope only.
- Constraints: API compatibility, framework conventions, serialization needs, and testing scope.

## Procedure
1. Classify the target type.
- Value object / DTO / config snapshot: candidate for `record`.
- Stateful component (service, repository, screen/controller): usually `class`.
- Framework constraints:
  - JPA `@Entity` types should remain classes (mutable lifecycle, no-arg constructor expectations).
  - Spring `@ConfigurationProperties` can be records in Spring Boot 2.6+.
  - Mockito cannot mock records directly without inline mocking support.

2. Decide `record` vs `class`.
- Use `record` when data is immutable and identity is value-based.
- Use `class` when mutable lifecycle, inheritance-heavy behavior, framework proxying constraints, or no canonical constructor semantics are required.

3. Decide local typing strategy (strict order).
- This typing policy applies to all domains; there are no game-specific exceptions.
- Rule 1: Never use Lombok `val`.
- Rule 2: Use explicit types in public APIs and domain model declarations.
- Rule 3: In local variables inside method bodies, use `var` only when one of these is true:
  - right-hand side is a constructor call with explicit type arguments (for example `new HashMap<String, Integer>()`)
  - right-hand side is a typed factory where the target type is explicit from the call (for example `List.of(...)`, `Map.of(...)`, `Optional.of(...)`)
- Rule 4: Otherwise, use explicit types.

4. Apply modern Java patterns.
- Prefer records for immutable carriers.
- Prefer enums, sealed hierarchies, and pattern matching where they simplify branching.
- Keep domain objects null-safe with constructor validation (`Objects.requireNonNull`).
- In record compact constructors, defensively copy mutable collections (`List.copyOf`, `Set.copyOf`, `Map.copyOf`).
- For JSON DTO records, keep serialization compatibility (Jackson 2.12+ for records; preserve field names with `@JsonProperty` when needed).
- Preserve deterministic behavior and existing public APIs unless changes are requested.

5. Integrate with project conventions.
- Respect existing package layout and naming.
- Keep comments succinct and only where logic is non-obvious.
- Avoid introducing new libraries unless justified.

6. Add or update tests.
- Verify record invariants and constructor validation.
- Verify behavior-preserving refactors with focused unit tests.
- Ensure existing tests keep passing.

7. Run completion checks.
- Build compiles.
- Tests relevant to changed areas pass.
- Code uses modern structures intentionally (not mechanically).

## Decision Points
- Is the type primarily immutable data?
  - yes -> `record`
  - no -> `class`
- Is the type constrained by framework/runtime rules (for example JPA `@Entity`)?
  - yes -> `class`
  - no -> keep prior choice
- For local variables, does the right-hand side match an allowed `var` case?
  - yes -> `var` allowed
  - no -> explicit type
- Is the record used as JSON DTO?
  - yes -> preserve field mapping and Jackson compatibility
  - no -> continue
- Does the record contain mutable collections?
  - yes -> use defensive copies in compact constructor
  - no -> continue

## Quality Criteria
- Uses `record` for immutable data carriers when appropriate.
- Applies typing rules in order: no `val`, explicit for API/domain, selective `var` only for defined local cases.
- No unnecessary mutability, boilerplate, or framework friction.
- No API breakage unless explicitly requested.
- Includes or updates tests for changed behavior.

## Completion Checklist
- Confirmed Java version and type style policy.
- Chosen `record`/`class` with rationale.
- Chosen explicit type vs `var` strategy with rationale.
- If records are JSON DTOs, confirmed Jackson compatibility and field-name mapping (`@JsonProperty` when needed).
- If records contain collections, added defensive copies in compact constructor.
- Implemented code changes in project style.
- Added/updated tests.
- Build/test verification completed.

## Example Prompts
- "Generate a Java 21 immutable order summary model using records and validation."
- "Refactor this DTO layer to records and keep JSON compatibility."
- "Rewrite this method using modern Java with readable explicit typing and selective var."
