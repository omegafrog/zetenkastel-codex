# Properties
doc_path: docs/design-docs/zetenkastel/note-management-program/detailed-design.md
owner: Codex
status: draft
domain: zetenkastel
task: note-management-program
last_updated: 2026-04-14:09:13

# Detailed Design
## Layer Mapping (ARCHITECTURE.md alignment)
- ui layer
  - Backend: `src/main/java/com/zetenkastel/core/ui/NoteController.java`
  - Frontend: `frontend/src/App.tsx`, `frontend/src/components/graph-view.tsx`
- app layer
  - `src/main/java/com/zetenkastel/core/app/NoteService.java`
- domain layer
  - `src/main/java/com/zetenkastel/core/domain/Note.java`
  - `src/main/java/com/zetenkastel/core/domain/NoteId.java`
  - `src/main/java/com/zetenkastel/core/domain/NoteType.java`
  - `src/main/java/com/zetenkastel/core/domain/LinkMode.java`
- port layer
  - `src/main/java/com/zetenkastel/core/port/NoteRepository.java`
- adapter layer
  - `src/main/java/com/zetenkastel/core/adapter/LocalFileNoteRepository.java`

## Command / Event / Policy Models
- Commands
  - CreateNote, UpdateNote, DeleteNote
  - SearchNotes, LoadBacklinks, RecommendLinks, LoadGraph
  - RenderShadcnUI, ToggleNoteTypeFold
- Events
  - NoteCreated, NoteUpdated, NoteDeleted, DeletedLinksPruned
  - NotesSearched, BacklinksLoaded, RecommendationsCalculated, GraphBuilt
  - UiRenderedWithShadcn, NoteTypeFoldToggled
- Policy implementation location
  - Aggregate 내부: 중복 ID 차단, 존재 검증, 링크 정리
  - App orchestration: AUTO_CONNECT 추천 병합
  - UI state policy: 타입별 폴드 상태 토글 및 그룹 렌더

## DTO / Request / Response Model
- Backend request DTO
  - `NoteController.UpsertNoteRequest`
- Backend response DTO
  - `NoteController.NoteResponse`
  - `NoteController.RecommendationResponse`
  - `NoteService.GraphView`, `GraphNode`, `GraphEdge`
- Frontend model
  - `frontend/src/lib/types.ts` (`Note`, `Recommendation`, `GraphView`)

## Frontend Component Design (shadcn/ui)
- ui primitives
  - `frontend/src/components/ui/button.tsx`
  - `frontend/src/components/ui/input.tsx`
  - `frontend/src/components/ui/textarea.tsx`
  - `frontend/src/components/ui/select.tsx`
  - `frontend/src/components/ui/card.tsx`
  - `frontend/src/components/ui/collapsible.tsx`
- screen composition
  - `App.tsx`: 검색, 타입별 폴드 목록, CRUD 폼, 백링크/추천 패널, 그래프 탭
  - `graph-view.tsx`: `/api/graph` 렌더
- API adapter
  - `frontend/src/lib/api.ts`

## Test Points
- Architecture style test
  - `src/test/java/com/zetenkastel/main/ArchitectureRulesTest.java`
- API behavior tests
  - `src/test/java/com/zetenkastel/core/ui/NoteControllerTest.java`
- E2E flow
  - `e2e/tests/note-management.spec.ts`
- UI policy checks
  - 타입별 폴드 토글 시 목록 즉시 반영
  - 기존 기능(CRUD/검색/백링크/추천/그래프) 기능 유지

## Design Tension
- Spring static 경로(`src/main/resources/static`)에 Vite 산출물을 배치하는 운영 방식 유지.

# Backlinks
- docs/design-docs/zetenkastel/note-management-program/bounded-context.md
- docs/work-units/zetenkastel/note-management-program/index.md
