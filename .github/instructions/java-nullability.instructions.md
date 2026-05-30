---
description: "Use when modifying Java source or Java tests in this repository. Applies the project's NullAway + Error Prone + JSpecify nullability policy and coding expectations. Keywords: nullaway, errorprone, jspecify, nullability, nullmarked, non-null."
name: "Java Nullability Policy"
applyTo: ["core/src/main/java/**/*.java", "core/src/test/java/**/*.java", "headless/src/main/java/**/*.java", "headless/src/test/java/**/*.java", "lwjgl3/src/main/java/**/*.java", "lwjgl3/src/test/java/**/*.java"]
---
# Java Nullability Policy

- Treat package-level `@NullMarked` as the default non-null contract.
- Use `@Nullable` only where null is intentional and required by behavior.
- Respect build-enforced null-safety: NullAway in JSpecify mode with Error Prone and `-Werror`.
- Avoid adding redundant `Objects.requireNonNull(...)` in internal code paths already proven non-null by the type contract.
- Keep explicit runtime null guards at trust boundaries (external input, deserialization, interop, dynamic maps/JSON, reflection).
- If introducing nullable flow, update annotations and tests so intent is explicit and checker-clean.
