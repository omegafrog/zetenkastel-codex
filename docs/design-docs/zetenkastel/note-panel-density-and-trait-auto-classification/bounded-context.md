# Properties
doc_path: docs/design-docs/zetenkastel/note-panel-density-and-trait-auto-classification/bounded-context.md
owner: Codex
status: draft
domain: zetenkastel
task: note-panel-density-and-trait-auto-classification
last_updated: 2026-04-14:11:07

# Bounded Context Finalization
## Note Core Context
- Responsibility
  - note CRUD, metadata persistence, legacy compatibility
- Included Aggregates
  - NoteAggregate

## Classification Context
- Responsibility
  - reference docs 로딩, canonical key normalization, rule scoring
- Included Aggregates / Policies
  - ClassificationPolicy

## Note Discovery Context
- Responsibility
  - 검색/백링크/추천/그래프 조회
- Included Aggregates / Projections
  - NoteDiscoveryProjection

## Web UI Context
- Responsibility
  - 리스트 패널 밀도 조정, 편집 패널 레이아웃, 사용자 입력
- Included Components
  - `frontend/src/App.tsx`, shadcn/ui primitives

# Relations
- Web UI Context -> Note Core Context: `/api/notes*` 저장/삭제/조회
- Note Core Context -> Classification Context: 저장 시 metadata classification
- Web UI Context -> Note Discovery Context: `/api/notes/search`, `/api/graph`, `/api/.../backlinks|recommendations`

# Backlinks
- docs/design-docs/zetenkastel/note-panel-density-and-trait-auto-classification/aggregate-design.md
- docs/work-units/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/index.md
