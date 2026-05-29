---
description: "Implement requirement from REQ ID, update functional/non-functional docs, traceability matrix, and physically remove completed item from plan."
name: "Implement Requirement From Plan"
argument-hint: "Provide the REQ identifier from the plan (and optionally scope/notes)."
agent: "agent"
model: "GPT-5 (copilot)"
---
Implement a requirement based on the REQ identifier from the plan provided by the user.

Documentation and source context:
- [Requirements Plan](../../docs/requirements/game-requirements-plan.md)
- [Functional Requirements](../../docs/requirements/game-requirements-functional.md)
- [Non-Functional Requirements](../../docs/requirements/game-requirements-non-functional.md)
- [Traceability Matrix](../../docs/engine/traceability-matrix.md)
- [Java Traceability Instructions](../instructions/java-traceability.instructions.md)

Working rules:
1. Read the item in `game-requirements-plan.md` indicated by the REQ argument and extract:
   - REQ identifier(s),
   - expected behavior,
   - functional and non-functional constraints,
   - potential implementation and test locations.
2. If the REQ is ambiguous or does not exist, stop and ask the minimum set of clarifying questions.
3. Ensure existing plan items have assigned REQ IDs. If there are plan items without REQ IDs, assign the next available identifiers using a consistent numbering scheme.
4. Implement the change in code.
5. Add or update tests that cover the change (unit/integration, depending on context).
6. Update documentation:
   - `docs/requirements/game-requirements-functional.md` (what was implemented functionally),
   - `docs/requirements/game-requirements-non-functional.md` (non-functional impact),
   - `docs/engine/traceability-matrix.md` (REQ -> implementation -> tests),
   - `docs/requirements/game-requirements-plan.md` (physically remove the completed item from the To-Do plan).
7. Keep REQ identifiers consistent and do not create new IDs without justification.
8. Run appropriate tests and report the result.

Required response format:
## Requirement Understanding
- REQ from plan: ...
- Related REQs: ...
- Implementation scope: ...

## Code Changes
- [path](path): short description of the change

## Test Changes
- [path](path): covered scenarios

## Documentation Changes
- [docs/requirements/game-requirements-functional.md](../../docs/requirements/game-requirements-functional.md): ...
- [docs/requirements/game-requirements-non-functional.md](../../docs/requirements/game-requirements-non-functional.md): ...
- [docs/engine/traceability-matrix.md](../../docs/engine/traceability-matrix.md): ...
- [docs/requirements/game-requirements-plan.md](../../docs/requirements/game-requirements-plan.md): ...

## Validation Result
- Tests run: ...
- Status: ...
- Risks / gaps: ...

Quality and constraints:
- Do not assume missing data; if context is missing, ask.
- The plan contains only incomplete items, so after implementing a requirement physically remove the item from the plan.
- Priority: correct implementation + traceability + test evidence.
