# Properties
doc_path: docs/design-docs/zetenkastel/note-management-program/aggregate-design.md
owner: Codex
status: completed
last_updated: 2026-04-13:22:17

# Aggregate Design
- Aggregate: Note
- Identity: path + filename (relative path)
- State
- type
- title
- tags
- content
- links (parsed from content)

# Invariants
- note path는 저장 루트 내부여야 한다.
- type은 허용된 10개 타입 중 하나여야 한다.
- path 충돌(중복 파일)은 허용하지 않는다.
