---
name: java-testing-junit5-assertj
description: 'Create and update Java tests with JUnit 5 and AssertJ, avoiding JUnit assertions and preferring a single assertThat-based check per test.'
argument-hint: 'Provide the behavior under test, target class or module, and any existing test conventions to follow.'
user-invocable: true
---

# Java Testing with JUnit 5 and AssertJ

## Outcome
Create clear, focused Java tests that follow the repository’s testing conventions and prefer AssertJ over JUnit assertions.

## When To Use
- Adding new Java tests.
- Updating existing Java tests.
- Refactoring tests without changing intent.
- Reviewing or generating test code for new behavior.

## Inputs To Collect
- Behavior or class to test.
- Module or package scope.
- Any existing test patterns already used in the repository.
- Whether the change is a new feature, bug fix, or regression test.

## Procedure
1. Choose the right test style.
- Use JUnit 5 Jupiter test annotations and structure.
- Prefer AssertJ assertions for all new assertions.
- Avoid JUnit assertion helpers such as `Assertions.assertEquals(...)` or similar.

2. Use AssertJ as the default assertion library.
- Import `assertThat` from AssertJ.
- Use a single assertion style such as `assertThat(actual).isEqualTo(expected)` whenever the behavior can be expressed as an object comparison.

3. Keep each test focused.
- Cover one behavior per test.
- Prefer one meaningful assertion over multiple assertions in the same test.
- If a test needs to check multiple independent properties, split it into separate tests.

4. Name tests clearly.
- Use descriptive names that describe the expected behavior.
- Favor names that make the scenario and expected outcome obvious.

5. Verify the test.
- Run the smallest relevant test target first.
- If the change affects broader behavior, run a wider related test set.

## Decision Points
- Is the test verifying a single behavior?
  - yes -> keep it as one focused test
  - no -> split it into separate tests
- Can the expected result be expressed as an object comparison?
  - yes -> prefer `assertThat(actual).isEqualTo(expected)`
  - no -> use the most specific AssertJ assertion that matches the behavior
- Is the test using JUnit assertion utilities?
  - yes -> replace them with AssertJ assertions
  - no -> continue

## Quality Criteria
- Tests use JUnit 5 and AssertJ.
- No JUnit-based assertion helpers are introduced in new tests.
- Tests remain focused and avoid multiple assertions for the same scenario.
- Assertions are clear, readable, and tied to the expected behavior.

## Completion Checklist
- Confirmed the target behavior and scope.
- Used JUnit 5 test structure.
- Replaced JUnit assertions with AssertJ assertions.
- Preferred a single `assertThat`-style assertion where appropriate.
- Kept the test focused and readable.
- Ran the relevant test suite.

## Example Prompts
- "Add a JUnit 5 test for this parser behavior using AssertJ assertions only."
- "Create a regression test for this bug fix and keep it focused on one assertion."
- "Refactor these tests to remove JUnit assertions and use AssertJ instead."
