# Game Non-Functional Requirements v1 (Implemented Baseline)

## Scope
This document defines non-functional requirements that reflect quality attributes implemented in the current baseline.

## Determinism
- NFR-DET-001: Runtime uses a deterministic engine context with fixed-seed support.
- NFR-DET-002: Turn simulation produces a canonical snapshot string suitable for semantic comparison.
- NFR-DET-003: Semantic equivalence is evaluated by state equality, phase trace equality, and canonical snapshot equality.

## Reliability and Robustness
- NFR-REL-001: Scenario and campaign data are loaded from bundled JSON resources in a stable, repeatable way.
- NFR-REL-002: Unit coordinates are validated against scenario bounds; out-of-range definitions are rejected during load.
- NFR-REL-003: Invalid movement orders are ignored silently, preserving simulation continuity.

## Testability
- NFR-TST-001: Core turn simulation behavior is structured to allow deterministic, repeatable verification.
- NFR-TST-002: Runtime exposes post-turn state in a way that supports semantic comparison between runs.

## Maintainability
- NFR-MNT-001: Domain identity and placement data are represented with immutable records to reduce mutation-related defects.
- NFR-MNT-002: Engine execution follows explicit named phases, improving traceability and diagnosability.

## Usability Baseline
- NFR-USAB-001: UI consistently exposes scenario name, turn number, and active side in the status bar.
- NFR-USAB-002: Selection visibility and camera behavior are designed to keep interaction state understandable during gameplay.

## Planned Non-Functional Requirements (Pending Implementation)
The following quality requirements are approved for next implementation increment and are not part of implemented v1 baseline yet.

- NFR-USAB-003: MOVE target planning must provide clear and persistent visual state (selected unit plus target flag marker).
- NFR-USAB-004: Target confirmation feedback (sound and focus transition) must be immediate and consistent.
- NFR-DET-004: Target assignment and movement-phase consumption must remain deterministic for identical inputs.

## Related Documents
- Functional requirements: [game-requirements-functional.md](game-requirements-functional.md)
- Deferred scope: [game-requirements-plan.md](game-requirements-plan.md)
- Determinism contract: [../engine/determinism-contract.md](../engine/determinism-contract.md)
- Traceability matrix: [../engine/traceability-matrix.md](../engine/traceability-matrix.md)
