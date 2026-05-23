# Determinism Contract v1 (Normative)

## 1. Purpose
This document defines reproducibility guarantees for current turn execution behavior.

## 2. Deterministic Context
1. Engine instances MUST be created through `TurnEngine.fixedContext(DeterministicContext, ScenarioDefinition)`.
2. `DeterministicContext.withSeed(long)` MUST preserve the seed value used for result reporting.
3. `TurnResult.seed` MUST equal context seed used to build the engine.

## 3. Canonical Snapshot Contract
1. Every `TurnResult` MUST include a non-empty `canonicalSnapshot` string.
2. Snapshot shape MUST follow current canonical format:

`turn=<turnNumber>,side=<activeSide>,units=[<unitId>@<x>,<y>;...]`

3. Units in snapshot MUST be sorted by `unit.id` ascending.

## 4. Semantic Equivalence Contract
`TurnEngine.areSemanticallyEquivalent(a, b)` MUST return true iff:
1. `a.state().equals(b.state())`
2. `a.phaseTrace().equals(b.phaseTrace())`
3. `a.canonicalSnapshot().equals(b.canonicalSnapshot())`

Null handling MUST follow:
- true when references are identical.
- false when exactly one argument is null.

## 5. Reproducibility Guarantee (Current Scope)
For identical input state, identical scenario definition, and identical seed:
1. Running one turn MUST produce identical canonical snapshots.
2. Results SHOULD be semantically equivalent under `areSemanticallyEquivalent`.

## 6. Time Measurement Non-Determinism
`TurnResult.timeMillis` measures elapsed wall-clock time and MUST NOT be treated as determinism evidence.

## 7. Non-Normative Notes
This contract does not yet define random combat outcomes because combat logic is not implemented in v1 scope.
