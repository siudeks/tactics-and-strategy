# Project Guidelines — tactics-and-strategy

## Overview
Turn-based tactical strategy game (Desert Rats / North Africa theme) built with **Java 21 + libGDX**.  
Inspired by the original ZX Spectrum 128K Desert Rats game.  
See [docs/README.md](../docs/README.md) for full documentation index.

## Game Domain Rules

### Sides
- Two active sides: `ALLIES` and `AXIS`. `NEUTRAL` is reserved and must never be used as an active turn side.
- Turns alternate: ALLIES → AXIS → ALLIES → ...

### Turn Structure (4 phases)
1. **ISSUE_ORDERS** — players assign orders to units
2. **SIMULTANEOUS_MOVE** — all MOVE orders resolve at once
3. **COMBAT** — engagement resolution (contextual: terrain, position, unit state)
4. **END_TURN** — flip active side, increment turn counter, clear pending orders

### Units
- Units are **military formations** (not individual characters).
- Domain record: `Unit(id, side, type, size, tileX, tileY)` — all fields non-null.
- `UnitType` and `UnitSize` define role and scale of the formation.
- Unit operational states: **active**, **disrupted**, **eliminated**.

### Orders
- Supported order types (from `OrderType`): `MOVE`, `ASSAULT`, `HOLD`, `TRAVEL`, `FORTIFY`.
- Orders are issued before movement and cleared at end of turn.
- Invalid moves (out-of-bounds, VOID/WATER terrain) are silently ignored — unit stays in place.

### Map & Terrain
- Hex-based map defined by `ScenarioDefinition` (mapWidth × mapHeight).
- `TerrainType` values `VOID` and `WATER` are impassable.
- Strategic **control points** are objective-capable map nodes — their ownership drives scenario outcomes.

### Supply
- Supply status is tracked per unit/formation group.
- Supply disruption reduces operational effectiveness.
- Stacking limit: max **10 units per hex** (ZX Spectrum 128K variant).
- Loss of supply continuity triggers scenario penalties.

### Scenarios
- Each scenario must define: primary objectives, secondary objectives, failure conditions, turn/time constraints.
- Scenario files trace to requirements via IDs (e.g., `REQ-MAP-001`, `REQ-UNIT-001`).
- See [docs/scenarios/scenarios-v0.md](../docs/scenarios/scenarios-v0.md) for the current scenario pack.

### Victory / Failure
- Victory conditions are checked at **end-of-turn validation phase** after both sides complete their turns.
- Failure conditions (e.g., supply collapse, objective loss) can trigger mid-scenario.

## Architecture

| Module | Purpose |
|--------|---------|
| `core` | Domain model, game engine, scenario loading, screen logic |
| `lwjgl3` | Desktop launcher (requires graphical session) |
| `headless` | Devcontainer-safe launcher (no GUI) |

Key packages in `core`:
- `game.domain` — immutable value types (`Unit`, `Order`, `CampaignState`, etc.)
- `game.engine` — `TurnEngine`, `GameRuntime`, determinism context
- `game.scenario` — `ScenarioLoader`, `LoadedScenario`
- `game.screens` — libGDX screen implementations

Prefer **immutable records** for domain objects. `CampaignState` is the canonical game state passed between turns.

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
- `DeterministicContext` carries the RNG seed — all randomness must flow through it to preserve replay determinism.
- `TurnEngine.areSemanticallyEquivalent()` is the canonical equality check for turn results.
- Implementation language: **English** for code and comments; planning docs may be in Polish.
