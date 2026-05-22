# Scenario Pack v0

## Scenario Template
- ID
- Title
- Theater Context
- Primary Objectives
- Secondary Objectives
- Failure Conditions
- Operational Constraints
- Completion Trigger
- Requirements Trace IDs

---

## S01 - Border at Dawn
- Theater Context: Frontier opening operation.
- Primary Objectives:
  - Hold two designated border control points.
- Secondary Objectives:
  - Keep losses below scenario threshold.
- Failure Conditions:
  - Both border points lost at the same time.
- Operational Constraints:
  - Limited operational depth in early turns.
- Completion Trigger:
  - Survive required number of turns while maintaining objective control.
- Requirements Trace IDs:
  - REQ-LOOP-001, REQ-MAP-001, REQ-SCEN-001, REQ-HUD-001

## S02 - Convoy Intercept
- Theater Context: Mobile interdiction operation.
- Primary Objectives:
  - Capture or neutralize enemy supply convoy.
- Secondary Objectives:
  - Extract at least one reconnaissance element.
- Failure Conditions:
  - Convoy exits battlefield operational zone.
- Operational Constraints:
  - Time pressure and route uncertainty.
- Completion Trigger:
  - Convoy status resolved before extraction window closes.
- Requirements Trace IDs:
  - REQ-TURN-001, REQ-SUP-001, REQ-SCEN-001

## S03 - Supply Corridor
- Theater Context: Mid-campaign logistics defense.
- Primary Objectives:
  - Maintain continuous supply corridor between base and front sector.
- Secondary Objectives:
  - Reclaim one contested waypoint.
- Failure Conditions:
  - Supply corridor interrupted beyond allowed tolerance.
- Operational Constraints:
  - Multi-box control dependency.
- Completion Trigger:
  - Corridor continuity preserved through scenario duration.
- Requirements Trace IDs:
  - REQ-SUP-001, REQ-MAP-001, REQ-UNIT-001

## S04 - Night Counterstroke
- Theater Context: Limited-visibility recapture attempt.
- Primary Objectives:
  - Recover lost sector from previous operation.
- Secondary Objectives:
  - End mission with morale advantage.
- Failure Conditions:
  - Sector not recovered before turn limit.
- Operational Constraints:
  - Reduced visibility and reaction windows.
- Completion Trigger:
  - Sector control verified at end-of-turn validation.
- Requirements Trace IDs:
  - REQ-CBT-001, REQ-SCEN-001, REQ-HUD-001

## S05 - Break the Line
- Theater Context: Late-campaign breakthrough action.
- Primary Objectives:
  - Break defensive line and secure key terrain node.
- Secondary Objectives:
  - Preserve core formation combat readiness.
- Failure Conditions:
  - No breakthrough within operational limit.
- Operational Constraints:
  - High resistance and stacking pressure.
- Completion Trigger:
  - Key terrain captured and held through validation phase.
- Requirements Trace IDs:
  - REQ-STACK-001, REQ-CBT-001, REQ-SCEN-001

## S06 - Final Sector
- Theater Context: End-state control operation.
- Primary Objectives:
  - Secure three strategic sectors simultaneously.
- Secondary Objectives:
  - Prevent command-chain disruption.
- Failure Conditions:
  - Two sectors lost or supply collapse event.
- Operational Constraints:
  - Concurrent objective defense.
- Completion Trigger:
  - Simultaneous control state confirmed at mission close.
- Requirements Trace IDs:
  - REQ-SUP-001, REQ-MAP-001, REQ-SCEN-001, REQ-HUD-001

---

## Traceability Index
- REQ-LOOP-001: Core gameplay loop
- REQ-TURN-001: Turn sequence and validation
- REQ-MAP-001: Hex map, terrain, control points
- REQ-UNIT-001: Formation-based units and states
- REQ-SUP-001: Supply continuity and penalties
- REQ-STACK-001: Unit concentration constraints
- REQ-CBT-001: Contextual combat resolution
- REQ-SCEN-001: Scenario objective/failure model
- REQ-HUD-001: Operational readability and HUD state

---

## Runtime And Validation Notes (T07, 2026-05-22)

### Runtime source of truth for rendered units
- Jednostki widoczne na polu bitwy pochodzą z runtime `CampaignState` (nie z hardcoded pozycji i nie z jednorazowej kopii konstruktora ekranu).
- Render pobiera stan przez `GameRuntime.getCurrentCampaignState()`, więc po zakończeniu tury widok odzwierciedla aktualny stan kampanii.

### Coordinate mapping used by renderer
- Scenariusze definiują pozycje jako `tileX`, `tileY` (top-origin dla osi Y).
- Przeliczenie pozycji ikony 2x2 tile odbywa się przez tile -> world -> screen:
  - `iconWorldX = tileX * 16`
  - `iconWorldY = (mapHeight - tileY - 2) * 16`
  - `iconScreenX = panelX + iconWorldX - cameraX`
  - `iconScreenY = panelY + iconWorldY - cameraY`
- Szczegółowy kontrakt i przykłady: `docs/plan/20260522-scenario-unit-rendering/T04-rendering-coordinate-spec.md`.

### Fail-fast scenario bounds validation
- Loader waliduje współrzędne wszystkich jednostek względem `map.width` i `map.height` podczas ładowania.
- Współrzędne poza zakresem powodują natychmiastowy wyjątek (`IllegalArgumentException`) z kontekstem:
  - `Scenario <scenarioId> has unit <unitId> at tile (<x>,<y>) outside bounds [0..%d] x [0..%d]`
  - gdzie `%d` to inkluzywne górne granice: `maxX = mapWidth - 1`, `maxY = mapHeight - 1`
  - przykład: `Scenario invalid-coordinates has unit bad-unit at tile (5,-1) outside bounds [0..4] x [0..3]`
- Dzięki temu błędne scenariusze są odrzucane na etapie load, a nie dopiero podczas runtime.

### Preserved flow contract
- Zachowany kontrakt uruchomienia scenariusza: `MainMenuScreen -> ScenarioLoader.loadFromResource(...) -> BattlefieldScreen(Game, LoadedScenario)`.
- Brak zmian w publicznych kontraktach konstruktorów i rekordów domenowych dla tego przepływu.

### Verification commands
- `./gradlew test`
- `./gradlew headless:run`

### Known residual test risk
- Istnieje nadal ograniczone pokrycie wizualnego E2E dla `lwjgl3` (pixel-level assertions); obecne testy weryfikują kontrakty i obliczenia renderingu, ale nie pełny rendering desktopowy przy wielu konfiguracjach viewportu.
