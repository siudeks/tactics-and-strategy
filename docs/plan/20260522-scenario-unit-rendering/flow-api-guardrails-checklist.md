# T01 Flow And API Guardrails Checklist

Use this checklist as the non-regression contract for T02, T05, and T06.

## Transition Flow Invariants

- `MainMenuScreen(Game)` remains the entry constructor for the scenario-selection screen.
- `MainMenuScreen.launchSelected()` continues to resolve the selected entry and load the scenario via `ScenarioLoader.loadFromResource(entry.resourcePath())`.
- The selected scenario still transitions directly to `BattlefieldScreen(Game, LoadedScenario)`.
- `BattlefieldScreen` continues to initialize runtime state via `GameRuntime(LoadedScenario)`.

## API Stability Invariants

- `LoadedScenario` record shape remains unchanged: `LoadedScenario(ScenarioDefinition scenarioDefinition, CampaignState campaignState)`.
- No public domain record constructor or accessor changes are introduced in `CampaignState`, `ScenarioDefinition`, `Unit`, `Order`, or related domain types for this work.
- No constructor signature changes are introduced for `MainMenuScreen`, `BattlefieldScreen`, or `GameRuntime`.

## Rendering Scope Guardrail

- Rendering changes may alter how battlefield units are sourced or drawn internally, but must not bypass the existing `MainMenuScreen -> ScenarioLoader -> BattlefieldScreen -> GameRuntime` handoff.
- Runtime-backed rendering must be added without introducing an alternate scenario-loading path or widening domain APIs.