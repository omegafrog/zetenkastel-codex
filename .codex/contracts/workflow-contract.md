# Workflow Contract

## Canonical Agent IDs

- use_case_harvester
- oracle
- doc_writer
- doc_verify
- executor
- test_gate
- execute_writer
- closer

## Canonical Paths

- work-unit hub doc:
  docs/work-units/<domain>/<task>/index.md
- active exec docs:
  docs/exec-plans/active/<domain>/<task>/
- completed exec docs:
  docs/exec-plans/completed/<domain>/<task>/

## Invocation Unit Rule

- `use_case_harvester` runs are append-only at the task-path level.
- Each harvester invocation should create a new `<task>` identity for that run
  (for example `<prompt-summary-slug>-YYYYMMDD-HHMM`), instead of overwriting a prior run's task path.
- `<prompt-summary-slug>` must be a concise summary of the latest user prompt intent.
- Slug rules:
  - use lowercase kebab-case
  - allowed chars: `a-z`, `0-9`, `-`
  - no random suffix
  - recommended length: 3-8 words (max 60 chars)
- Downstream orchestration for that run updates or appends stage docs inside the same run-scoped `<task>` path.

## Path Reference Invariants

- During closure, exec docs may be moved from active to completed path.
- Document identity is represented by `doc_path` only.
- `doc_path` must be project-root-relative full path including filename
  (for example: `docs/exec-plans/active/<domain>/<task>/plan.md`).
- Do not use document id fields.
- Every orchestration unit must maintain a single hub doc at
  `docs/work-units/<domain>/<task>/index.md`.
- All stage output docs must include a backlink to the hub doc.
- Hub doc must list forward links to all stage output docs.
- Cross-document discovery uses `grep` patterns first, then hub backlinks.

## Canonical Status Enums

- coverage_gate: YES | PARTIAL | NO
- verification_verdict: PASS | FAIL | BLOCKED
- test_gate_verdict: PASS | FAIL | BLOCKED
- pr_readiness: PR_READY | NOT_READY | BLOCKED
- closure_verdict: COMPLETED | STOPPED

## Optional Shared Inputs

- AGENTS.md
- ARCHITECTURE.md

## Required Source of Truth

- .codex/config.toml
- .codex/openai.yaml
- .codex/agents/*
