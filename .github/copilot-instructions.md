# Project Guidelines — tactics-and-strategy

## Overview
Turn-based / real-time tactical strategy game (Desert Rats / North Africa theme) built with **Java 21 + libGDX**.  
Inspired by the original ZX Spectrum 128K Desert Rats game.  

## Build & Test

```bash
./gradlew build          # build all modules
./gradlew test           # run all tests
./gradlew headless:run   # smoke run (no GUI, devcontainer-safe)
./gradlew lwjgl3:run     # desktop run (requires graphical session)
```

Tests use **JUnit Jupiter 5**. Test class naming follows `*Test.java` and `*IntegrationTest.java`.  
Engine determinism is verified by `EngineDeterminismSmokeTest` — do not break it.

## Java Style
- Use `var` for local variable declarations when the type is clear from context.
- Target Java 21 idioms: records, sealed classes, pattern matching, text blocks.
- Java 21 records for all domain value types; no mutable state in domain layer.

## Conventions
- Implementation language: **English** for code, comments and docs.
