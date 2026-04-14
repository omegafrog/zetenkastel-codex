# Properties
doc_path: docs/design-docs/zetenkastel/note-management-program/event-storming.md
owner: Codex
status: draft
domain: zetenkastel
task: note-management-program
last_updated: 2026-04-14:09:13

# Event Storming
## UC-01 노트 생성
- Start Command
  - C-UC01 CreateNote
- Actors
  - Single User
- External Systems
  - Local File System
- Events
  - E-UC01 NoteCreated
- Policies
  - P-UC01-01 RejectDuplicateNoteId
- Follow-up Commands
  - C-UC05 LoadBacklinks (optional)
- Sync/Async Boundary
  - Sync (create + save)
- Cross-Context Interaction
  - Web UI Context -> Note Core Context

## UC-02 노트 조회/열람
- Start Command
  - C-UC02 GetNote
- Actors
  - Single User
- External Systems
  - Local File System
- Events
  - E-UC02 NoteLoaded
- Policies
  - P-UC02-01 RejectMissingNoteOnRead
- Follow-up Commands
  - C-UC05 LoadBacklinks
- Sync/Async Boundary
  - Sync
- Cross-Context Interaction
  - Web UI Context -> Note Core Context

## UC-03 노트 수정
- Start Command
  - C-UC03 UpdateNote
- Actors
  - Single User
- External Systems
  - Local File System
- Events
  - E-UC03 NoteUpdated
- Policies
  - P-UC03-01 RejectMissingNoteOnUpdate
  - P-UC03-02 OptionalAutoConnectOnUpsert
- Follow-up Commands
  - C-UC06 RecommendLinks
- Sync/Async Boundary
  - Sync
- Cross-Context Interaction
  - Web UI Context -> Note Core Context

## UC-04 노트 삭제
- Start Command
  - C-UC04 DeleteNote
- Actors
  - Single User
- External Systems
  - Local File System
- Events
  - E-UC04 NoteDeleted
  - E-UC04-2 DeletedLinksPruned
- Policies
  - P-UC04-01 RemoveDeletedLinks
- Follow-up Commands
  - C-UC08 LoadGraph
- Sync/Async Boundary
  - Sync
- Cross-Context Interaction
  - Note Core Context -> Note Discovery Context

## UC-05 백링크 확인
- Start Command
  - C-UC05 LoadBacklinks
- Actors
  - Single User
- External Systems
  - Local File System
- Events
  - E-UC05 BacklinksLoaded
- Policies
  - P-UC05-01 IncludeReferencingNotesOnly
- Follow-up Commands
  - 없음
- Sync/Async Boundary
  - Sync query
- Cross-Context Interaction
  - Web UI Context -> Note Discovery Context

## UC-06 링크 자동 연결 또는 추천
- Start Command
  - C-UC06 RecommendLinks
- Actors
  - Single User
- External Systems
  - Local File System
- Events
  - E-UC06 RecommendationsCalculated
- Policies
  - P-UC06-01 RecommendOnlyUnlinkedNotes
  - P-UC06-02 ExcludeSelfReference
- Follow-up Commands
  - C-UC03 UpdateNote (user apply)
- Sync/Async Boundary
  - Sync query + optional sync update
- Cross-Context Interaction
  - Note Discovery Context -> Note Core Context

## UC-07 검색(태그/제목/내용)
- Start Command
  - C-UC07 SearchNotes
- Actors
  - Single User
- External Systems
  - Local File System
- Events
  - E-UC07 NotesSearched
- Policies
  - P-UC07-01 MatchByTagTitleContent
- Follow-up Commands
  - C-UC02 GetNote
- Sync/Async Boundary
  - Sync query
- Cross-Context Interaction
  - Web UI Context -> Note Discovery Context

## UC-08 그래프 뷰 탐색
- Start Command
  - C-UC08 LoadGraph
- Actors
  - Single User
- External Systems
  - Local File System
- Events
  - E-UC08 GraphBuilt
- Policies
  - P-UC08-01 IncludeOnlyExistingLinkTargets
- Follow-up Commands
  - C-UC02 GetNote
- Sync/Async Boundary
  - Sync query
- Cross-Context Interaction
  - Web UI Context -> Note Discovery Context

## UC-09 shadcn/ui 기반 UI 전환
- Start Command
  - C-UC09 RenderShadcnUI
- Actors
  - Single User
- External Systems
  - Browser Runtime
- Events
  - E-UC09 UiRenderedWithShadcn
- Policies
  - P-UC09-01 PreserveFeatureParityAfterUiMigration
- Follow-up Commands
  - C-UC01..C-UC08
- Sync/Async Boundary
  - Sync UI render
- Cross-Context Interaction
  - Web UI Context orchestration only

## UC-10 노트 타입별 폴드 탐색
- Start Command
  - C-UC10 ToggleNoteTypeFold
- Actors
  - Single User
- External Systems
  - Browser Runtime
- Events
  - E-UC10 NoteTypeFoldToggled
- Policies
  - P-UC10-01 GroupNotesByTypeInCollapsibleSections
- Follow-up Commands
  - C-UC02 GetNote (note selection)
- Sync/Async Boundary
  - Sync UI state update
- Cross-Context Interaction
  - Web UI Context -> Note Discovery Context

# Backlinks
- docs/product-specs/zetenkastel/note-management-program/use-cases.md
- docs/work-units/zetenkastel/note-management-program/index.md
