# Properties
doc_path: docs/design-docs/zetenkastel/note-management-program/bounded-context.md
owner: Codex
status: draft
domain: zetenkastel
task: note-management-program
last_updated: 2026-04-14:10:12

# Bounded Context Finalization
## Note Core Context
- Responsibility
  - 노트 CRUD와 링크 데이터 정합성 유지
- Included Aggregates
  - NoteAggregate

## Note Discovery Context
- Responsibility
  - 검색/백링크/추천/그래프 조회 모델
- Included Aggregates / Projections
  - NoteDiscoveryProjection

## Web UI Context
- Responsibility
  - 편집 화면 정보 구조(property panel), 태그 칩 입력, 단일 저장 UX, 3패널 레이아웃
- Included Components
  - `App.tsx`, shadcn/ui primitives, graph-view

# Relations
- Web UI Context -> Note Core Context: `/api/notes*` 저장/삭제
- Web UI Context -> Note Discovery Context: `/api/notes/search`, `/api/graph`, `/api/.../backlinks|recommendations`

# Backlinks
- docs/design-docs/zetenkastel/note-management-program/aggregate-design.md
- docs/work-units/zetenkastel/note-management-program-20260414-0959/index.md
