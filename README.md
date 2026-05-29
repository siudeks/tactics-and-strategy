# tactics-and-strategy

[Original game](https://www.myabandonware.com/game/desert-rats-the-north-africa-campaign-al/play-al)  
[Remake](https://siudeks.itch.io/tactics-and-strategy)


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

Build a single runnable JAR release (requires Java 21 on user machine):
```bash
./gradlew lwjgl3:fatJar
```

Output file:
- `lwjgl3/build/libs/tactics-and-strategy-all.jar`

Run command:
- `java -jar lwjgl3/build/libs/tactics-and-strategy-all.jar`

Automated upload to itch.io is available via GitHub Actions workflow:
- `.github/workflows/release-windows-itch.yml` (manual `workflow_dispatch` trigger)

## Documentation
- [Project Docs Index](docs/README.md)
- [Engine Specification Package](docs/engine/README.md)
- [Engine Mechanics Specification](docs/engine/engine-spec.md)
- [Turn Semantics](docs/engine/turn-semantics.md)
- [Scenario Runtime Contract](docs/engine/scenario-runtime-contract.md)
- [Determinism Contract](docs/engine/determinism-contract.md)
- [Traceability Matrix](docs/engine/traceability-matrix.md)
- [Game Requirements v0](docs/requirements/game-requirements-v0.md)
- [Scenario Pack v0](docs/scenarios/scenarios-v0.md)
- [Graphics Asset Register v0](docs/assets/graphics-assets-register-v0.md)
- [Original Desert Rats Asset Pack](docs/assets/original-desert-rats-assets.md)

## Local Graphics Assets (Original Desert Rats Files)
- [Original Asset Folder](assets/original/desert-rats-zx)