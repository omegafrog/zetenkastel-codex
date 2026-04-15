# Orchestrate-Plan Document Templates

This folder provides reusable markdown templates aligned with the workflow contract.

Core rules:
- Use `doc_path` (project-root-relative path including filename) in `# Properties`.
- Do not use document ids.
- Prefer grep-based discovery over backlinks.
- Once oracle/doc_writer starts, create the full canonical document set for the run-scoped `<domain>/<task>` path.
- If a canonical document is not substantively applicable, keep the file and write an explicit placeholder such as `No functional use case change in this work unit` or `N/A for this work unit`.

Recommended grep patterns:
```bash
grep -R "^# Properties" docs/use-case-harvests/<domain>/<task>/
grep -R "^# Properties" docs/product-specs/<domain>/<task>/
grep -R "^# Properties" docs/design-docs/<domain>/<task>/
grep -R "^# Properties" docs/exec-plans/active/<domain>/<task>/
```
