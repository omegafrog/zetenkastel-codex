# Properties
doc_path: docs/design-docs/zetenkastel/note-management-program/aggregate-design.md
owner: Codex
status: draft
domain: zetenkastel
task: note-management-program
last_updated: 2026-04-14:09:13

# Aggregate Design
## Aggregate: NoteAggregate
- Responsibility
  - 노트 상태(제목/태그/내용/링크)와 경로 기반 식별자를 일관성 있게 유지한다.
- Commands Handled
  - CreateNote, UpdateNote, DeleteNote
- Events Produced
  - NoteCreated, NoteUpdated, NoteDeleted, DeletedLinksPruned
- Included Entities / Value Objects
  - Note, NoteId, NoteType, LinkMode
- Invariants
  - NoteId(`type/fileName`)는 고유해야 한다.
  - update/delete 대상 노트는 반드시 존재해야 한다.
  - 삭제된 노트를 가리키는 링크는 정리 대상이다.
- Transaction Boundary Rationale
  - 단일 노트 변경과 해당 링크 정리는 하나의 일관성 경계에서 처리한다.

## Aggregate: NoteDiscoveryProjection
- Responsibility
  - 검색/백링크/그래프/추천 계산에 필요한 조회 모델을 제공한다.
- Commands Handled
  - SearchNotes, LoadBacklinks, LoadGraph, RecommendLinks
- Events Produced
  - NotesSearched, BacklinksLoaded, GraphBuilt, RecommendationsCalculated
- Included Entities / Value Objects
  - GraphView, GraphNode, GraphEdge, ScoredNote
- Invariants
  - 그래프 간선은 존재하는 노트 ID만 대상으로 한다.
  - 추천 결과는 자기 자신과 기존 링크를 제외한다.
  - 검색은 태그/제목/내용 기준을 모두 고려한다.
- Transaction Boundary Rationale
  - 조회 연산은 읽기 전용으로 수행하고 쓰기 트랜잭션과 분리한다.

# Policy Placement
- Aggregate 내부 규칙
  - 중복 ID 차단, 업데이트 대상 존재 검증, 삭제 링크 정리
- Application orchestration
  - `AUTO_CONNECT` 모드에서 추천 결과 병합
- Query policy
  - 검색/그래프/백링크/추천 필터링 규칙

# Backlinks
- docs/design-docs/zetenkastel/note-management-program/event-storming.md
- docs/work-units/zetenkastel/note-management-program/index.md
