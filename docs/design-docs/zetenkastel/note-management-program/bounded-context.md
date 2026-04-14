# Properties
doc_path: docs/design-docs/zetenkastel/note-management-program/bounded-context.md
owner: Codex
status: draft
domain: zetenkastel
task: note-management-program
last_updated: 2026-04-14:09:02

# Bounded Context Finalization
## Note Core Context
- Responsibility
  - 노트 CRUD와 링크 데이터의 정합성 유지
- Included Aggregates
  - NoteAggregate
- Key Terms
  - Note, NoteId, NoteType, LinkMode
- Why This Boundary Exists
  - 쓰기 트랜잭션 규칙을 한 경계에 두기 위해

## Note Discovery Context
- Responsibility
  - 검색, 백링크, 추천, 그래프 조회 계산
- Included Aggregates / Projections
  - NoteDiscoveryProjection
- Key Terms
  - SearchQuery, Backlink, Recommendation, GraphNode, GraphEdge
- Why This Boundary Exists
  - 읽기 모델 최적화 규칙을 CRUD 경계와 분리하기 위해

## Web UI Context
- Responsibility
  - 사용자 입력/탐색 UX와 컴포넌트 구성
- Included Components
  - shadcn/ui 기반 form/list/collapsible/card
- Key Terms
  - CollapsibleSection, NoteListGroup, SelectedNote, FoldState
- Why This Boundary Exists
  - UI 프레임워크 전환(React+Vite)과 도메인 규칙을 분리하기 위해

# Relations
- Web UI Context -> Note Core Context: `/api/notes*` command 호출
- Web UI Context -> Note Discovery Context: `/api/notes/search`, `/api/graph`, `/api/.../backlinks|recommendations`

# Backlinks
- docs/design-docs/zetenkastel/note-management-program/aggregate-design.md
- docs/work-units/zetenkastel/note-management-program/index.md
