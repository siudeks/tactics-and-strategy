# Windows Release to itch.io

This runbook describes how to produce and upload a Windows build that includes a bundled Java runtime, so testers do not need to install Java.

## Prerequisites
- GitHub repository secrets:
  - `ITCH_IO_API_KEY`
  - `ITCH_IO_GAME_ID` (format: `username/game-slug`)
- Access to workflow dispatch on GitHub Actions

## Local Build (Windows only)
From repository root:

```bash
./gradlew lwjgl3:packageWindowsBundledRelease
```

Generated file:
- `lwjgl3/build/distributions/tactics-and-strategy-windows-x64.zip`

ZIP layout:
- `runtime/` embedded Java runtime
- `app/lib/` application jars and dependencies
- `app/bin/tactics-and-strategy.bat` app launcher
- `start-game.bat` convenience starter

## Publish Through GitHub Actions
1. Open Actions and run workflow `Release Windows Build to itch.io`.
2. Set `channel` (for example `windows`).
3. Set `version` (for example `0.1.0-test1`).
4. Start workflow.

The workflow runs on `windows-latest`, builds the bundled ZIP, and uploads it using butler.

## Tester Verification
1. Tester downloads the ZIP from itch.io page.
2. Tester extracts archive.
3. Tester runs `start-game.bat`.
4. Game starts without any system Java installation.
