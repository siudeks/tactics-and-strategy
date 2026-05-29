# Engine Specification Package

This package defines the normative engine mechanics specification for the currently implemented runtime behavior.

## Audience
- Engine developers
- QA and test authors
- Scenario tooling and runtime integration maintainers

## Scope Rule
This package is normative only for mechanics that are implemented in code and validated by tests.

Out-of-scope mechanics are listed explicitly as non-normative backlog items.

## Documents
- `engine-spec.md` - master normative specification and glossary.
- `turn-semantics.md` - formal one-turn transition contract.
- `scenario-runtime-contract.md` - scenario loading and validation contract.
- `determinism-contract.md` - reproducibility and semantic equivalence contract.
- `traceability-matrix.md` - requirement and evidence matrix.

## Versioning
- Version: v1
- Status: Active
- Baseline: repository `main` branch behavior as of 2026-05-23
