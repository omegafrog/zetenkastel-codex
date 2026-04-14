# Properties
doc_path: docs/design-docs/zetenkastel/note-management-program/detailed-design.md
owner: Codex
status: draft
domain: zetenkastel
task: note-management-program
last_updated: 2026-04-14:10:12

# Detailed Design
## Layer Mapping (ARCHITECTURE.md alignment)
- ui layer
  - Backend: `src/main/java/com/zetenkastel/core/ui/NoteController.java`
  - Frontend: `frontend/src/App.tsx`, `frontend/src/components/graph-view.tsx`
- app layer
  - `src/main/java/com/zetenkastel/core/app/NoteService.java`
- domain layer
  - `src/main/java/com/zetenkastel/core/domain/*`
- port layer
  - `src/main/java/com/zetenkastel/core/port/NoteRepository.java`
- adapter layer
  - `src/main/java/com/zetenkastel/core/adapter/LocalFileNoteRepository.java`

## Command / Event / Policy Models
- Commands
  - ComposeEditorLayout, CaptureTagInput, DeriveFileNameFromTypeAndTitle, SaveNote, DeleteNote
- Events
  - PropertyPanelCompacted, TagChipCommitted, FileNameAutoDerived, NoteSavedAsCreated/Updated, CenterPanelExpanded
- Policy implementation location
  - UI: property compact 구성, 아이콘+tooltip, 태그 Enter 처리, fileName derive
  - App/API: create/update/delete/backlink/recommendation 호출 및 오류 처리

## Frontend Component Design
- `App.tsx`
  - 좌측: 검색 + 타입별 폴드 목록
  - 중앙: 제목 + property compact + 본문
  - 우측: 백링크 + 추천
- property panel fields
  - 타입(select), 자동 file path(read-only), tags(chip + input), links(csv)
- actions
  - 저장/삭제 아이콘 버튼(hover title + aria-label)

## Test Points
- 단일 저장 버튼으로 신규 노트 저장(create) 검증
- 동일 path key 재저장(update) 검증
- Enter 입력 시 태그 칩 생성 검증
- 파일명 자동 생성 및 path key 표시 검증
- e2e로 백링크/그래프 회귀 검증

# Backlinks
- docs/design-docs/zetenkastel/note-management-program/bounded-context.md
- docs/work-units/zetenkastel/note-management-program-20260414-0959/index.md
