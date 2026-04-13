# Workflow Contract

## Canonical Agent IDs

- use_case_harvester
- oracle
- doc_writer
- doc_verify
- executor
- execute_writer
- closer

## Canonical Paths

- active exec docs:
  docs/exec-plans/active/<domain>/<task>/
- completed exec docs:
  docs/exec-plans/completed/<domain>/<task>/

## Path Reference Invariants

- During closure, exec docs may be moved from active to completed path.
- Document identity is represented by `doc_path` only.
- `doc_path` must be project-root-relative full path including filename
  (for example: `docs/exec-plans/active/<domain>/<task>/plan.md`).
- Do not use document id fields.
- Cross-document discovery uses `grep` patterns, not backlinks.

## Canonical Status Enums

- coverage_gate: YES | PARTIAL | NO
- verification_verdict: PASS | FAIL | BLOCKED
- pr_readiness: PR_READY | NOT_READY | BLOCKED
- closure_verdict: COMPLETED | STOPPED

## Optional Shared Inputs

- AGENTS.md
- ARCHITECTURE.md

## Required Source of Truth

- .codex/config.toml
- .codex/openai.yaml
- .codex/agents/*
