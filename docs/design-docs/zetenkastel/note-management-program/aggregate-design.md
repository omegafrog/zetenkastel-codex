# Properties
doc_path: docs/design-docs/zetenkastel/note-management-program/aggregate-design.md
owner: Codex
status: draft
domain: zetenkastel
task: note-management-program
last_updated: 2026-04-14:10:12

# Aggregate Design
## Aggregate: NoteAggregate
- Responsibility
  - 노트 생성/수정/삭제와 경로 기반 식별자 일관성 유지
- Commands Handled
  - CreateNote, UpdateNote, DeleteNote
- Events Produced
  - NoteCreated, NoteUpdated, NoteDeleted, DeletedLinksPruned
- Included Entities / Value Objects
  - Note, NoteId, NoteType, LinkMode
- Invariants
  - `type/fileName` path key는 고유
  - update는 대상 path key가 존재할 때만 허용
  - 삭제 후 dangling link는 정리
- Transaction Boundary Rationale
  - 단일 note write와 링크 정리를 하나의 경계에서 처리

## Aggregate: EditorSessionProjection (UI-side)
- Responsibility
  - 제목/타입/태그/링크/내용 입력 상태와 derived fileName 계산
- Commands Handled
  - DeriveFileNameFromTypeAndTitle, CaptureTagInput, SaveNoteIntent, RenderActionIcons
- Events Produced
  - FileNameAutoDerived, TagChipCommitted, SaveBranchDecided, ActionIconsRendered
- Included Entities / Value Objects
  - FormState, TagChip, DerivedPathKey
- Invariants
  - 태그는 공백/중복 없이 유지
  - fileName은 title 기반 slug 규칙 유지
  - 단일 저장에서 create/update 분기 결과는 명확해야 함
- Transaction Boundary Rationale
  - UI state policy를 서버 저장 정책과 분리하되 path key는 동일 규칙으로 맞춤

# Policy Placement
- Aggregate 내부
  - 중복 ID 차단, update 존재 검증, 삭제 링크 정리
- Application orchestration
  - 추천 모드 병합, save 분기 호출(create vs update)
- UI policy
  - property compact layout, icon+tooltip, 3패널 폭 정책, Enter 태그 확정

# Backlinks
- docs/design-docs/zetenkastel/note-management-program/event-storming.md
- docs/work-units/zetenkastel/note-management-program-20260414-0959/index.md
