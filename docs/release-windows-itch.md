# Windows Release to itch.io

This runbook describes how to produce and upload a single runnable JAR. Users must have Java 21 installed.

## Prerequisites
- GitHub repository secrets:
  - `ITCH_IO_API_KEY`
  - `ITCH_IO_GAME_ID` (format: `username/game-slug`)
- Access to workflow dispatch on GitHub Actions

## Local Build
From repository root:

```bash
./gradlew lwjgl3:fatJar
```

Generated file:
- `lwjgl3/build/libs/tactics-and-strategy-all.jar`

Artifact type:
- Single runnable fat JAR.

## Publish Through GitHub Actions
1. Open Actions and run workflow `Release Windows Build to itch.io`.
2. Set `channel` (for example `windows`).
3. Set `version` (for example `0.1.0-test1`).
4. Start workflow.

The workflow runs on `windows-latest`, builds the runnable JAR, unpublishes the previous channel content, and uploads the JAR using butler.

## Tester Verification
1. Tester downloads or updates the JAR from itch channel.
2. Tester runs `java -jar tactics-and-strategy-all.jar`.
3. Game starts when Java 21 is installed.

## itch.io Page Text (copy/paste)
Use the snippet below as the release description on itch.io:

Requirements:
- Java 21 installed.

How to run:
1. Download `tactics-and-strategy-all.jar`.
2. Open terminal in the folder with the file.
3. Run: `java -jar tactics-and-strategy-all.jar`

If the game does not start:
1. Verify Java version: `java -version`
2. Ensure output contains version 21.
