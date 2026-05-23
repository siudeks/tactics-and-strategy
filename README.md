# tactics-and-strategy

## Original game:
https://www.myabandonware.com/game/desert-rats-the-north-africa-campaign-al/play-al

## libGDX Hello World
This repository now contains a runnable libGDX starter app with a tactical screen layout:
- center map viewport (grid + sample units)
- right command panel for unit orders
- bottom status bar

### Modules
- `core` - shared game logic and `BattlefieldScreen`
- `lwjgl3` - desktop launcher with windowed UI
- `headless` - devcontainer-safe launcher (no GUI required)

### Run
Build all modules:
```bash
./gradlew build
```

Run desktop app (requires graphical session):
```bash
./gradlew lwjgl3:run
```

Run in devcontainer (headless smoke run):
```bash
./gradlew headless:run
```

## Documentation
- [Project Docs Index](docs/README.md)
- [Engine Specification Package v1](docs/engine/README.md)
- [Engine Mechanics Specification v1](docs/engine/engine-spec-v1.md)
- [Turn Semantics v1](docs/engine/turn-semantics-v1.md)
- [Scenario Runtime Contract v1](docs/engine/scenario-runtime-contract-v1.md)
- [Determinism Contract v1](docs/engine/determinism-contract-v1.md)
- [Traceability Matrix v1](docs/engine/traceability-matrix-v1.md)
- [Game Requirements v0](docs/requirements/game-requirements-v0.md)
- [Scenario Pack v0](docs/scenarios/scenarios-v0.md)
- [Graphics Asset Register v0](docs/assets/graphics-assets-register-v0.md)
- [Original Desert Rats Asset Pack](docs/assets/original-desert-rats-assets.md)

## Local Graphics Assets (Original Desert Rats Files)
- [Original Asset Folder](assets/original/desert-rats-zx)