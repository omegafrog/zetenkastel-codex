# Orchestrate-Plan Document Templates

This folder provides reusable markdown templates aligned with the workflow contract.

Core rules:
- Use `doc_path` (project-root-relative path including filename) in `# Properties`.
- Do not use document ids.
- Prefer grep-based discovery over backlinks.

Recommended grep patterns:
```bash
grep -R "^# Properties" docs/use-case-harvests/<domain>/<task>/
grep -R "^# Properties" docs/product-specs/<domain>/<task>/
grep -R "^# Properties" docs/design-docs/<domain>/<task>/
grep -R "^# Properties" docs/exec-plans/active/<domain>/<task>/
```
