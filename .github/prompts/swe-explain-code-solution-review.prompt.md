---
description: "Explain and review a code solution (including PR diffs) with rationale, trade-offs, risks, and test gaps using SWE agent."
name: "P Explain Code Solution Review"
argument-hint: "Provide files/symbols + what you want explained (e.g., design, logic, performance, tests)."
agent: SWE
model: GPT-5 (copilot)
---
Explain the selected code solution like a senior engineer review, focused on helping me understand why it was implemented this way.
If PR context or a diff is provided, prioritize review of the changed lines and expected behavioral impact.

Inputs to use:
- User question and goals
- Current selection in editor (if present)
- Referenced files/symbols/PR context
- Changed files and hunks from the active PR or provided diff (if available)

Behavior:
1. Start with a short plain-language summary of what the solution does.
2. Explain the implementation flow step-by-step, tied to concrete files and symbols.
3. Explain the rationale: constraints, assumptions, and trade-offs.
4. Call out risks, edge cases, and likely failure modes.
5. Assess test coverage quality and what is still unverified.
6. If reviewing a PR/diff, identify likely regressions, backward-compatibility concerns, and missing assertions.
7. Suggest 1-3 practical improvements or alternatives (with pros/cons), without rewriting everything unless asked.
8. If context is missing, ask only the minimum clarifying questions.

Required response format:
## What It Does
- 3-6 bullets in plain language.

## How It Works
- [path](path#Lx): explanation
- [path](path#Lx): explanation

## PR Review Findings (when diff/PR is provided)
- Severity: High | Medium | Low
- [path](path#Lx): finding, impact, and recommended fix

## Why This Approach
- Constraints:
- Trade-offs:
- Assumptions:

## Risks and Edge Cases
- ...

## Tests and Gaps
- Existing tests:
- Missing tests:

## Better Options (If Needed)
- Option A: ... (pros/cons)
- Option B: ... (pros/cons)

Style requirements:
- Prefer clarity over jargon.
- Keep explanations concrete and code-referenced.
- Distinguish facts from assumptions.
- If you are uncertain, say what to check next.
