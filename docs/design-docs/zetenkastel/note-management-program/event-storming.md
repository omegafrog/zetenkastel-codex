# Properties
doc_path: docs/design-docs/zetenkastel/note-management-program/event-storming.md
owner: Codex
status: draft
domain: zetenkastel
task: note-management-program
last_updated: 2026-04-14:10:12

# Event Storming
## UC-11 메타데이터 property 영역 축소 배치
- Start Command
  - C-UC11 ComposeEditorLayout
- Actors
  - Single User
- External Systems
  - Browser Runtime
- Events
  - E-UC11 PropertyPanelCompacted
- Policies
  - P-UC11-01 PrioritizeContentAreaVisibility
- Follow-up Commands
  - C-UC12 CaptureTagInput
  - C-UC14 SaveNote
- Sync/Async Boundary
  - Sync UI render
- Cross-Context Interaction
  - Web UI Context only

## UC-12 태그 엔터 확정 입력
- Start Command
  - C-UC12 CaptureTagInput
- Actors
  - Single User
- External Systems
  - Browser Runtime
- Events
  - E-UC12 TagChipCommitted
- Policies
  - P-UC12-01 RejectBlankOrDuplicateTag
- Follow-up Commands
  - C-UC14 SaveNote
- Sync/Async Boundary
  - Sync UI state update
- Cross-Context Interaction
  - Web UI Context only

## UC-13 파일명 자동 생성
- Start Command
  - C-UC13 DeriveFileNameFromTypeAndTitle
- Actors
  - Single User
- External Systems
  - Browser Runtime
- Events
  - E-UC13 FileNameAutoDerived
- Policies
  - P-UC13-01 SlugifyTitle
  - P-UC13-02 KeepPathKeyAsTypeSlashFileName
- Follow-up Commands
  - C-UC14 SaveNote
- Sync/Async Boundary
  - Sync compute
- Cross-Context Interaction
  - Web UI Context -> Note Core Context

## UC-14 단일 저장 버튼 기반 생성/수정 분기
- Start Command
  - C-UC14 SaveNote
- Actors
  - Single User
- External Systems
  - Local File System
- Events
  - E-UC14 NoteSavedAsCreated
  - E-UC14-2 NoteSavedAsUpdated
- Policies
  - P-UC14-01 BranchByCurrentPathKeyExistence
- Follow-up Commands
  - C-UC05 LoadBacklinks
  - C-UC06 RecommendLinks
- Sync/Async Boundary
  - Sync API call
- Cross-Context Interaction
  - Web UI Context -> Note Core Context

## UC-15 저장/삭제 아이콘 버튼 + 툴팁
- Start Command
  - C-UC15 RenderActionIcons
- Actors
  - Single User
- External Systems
  - Browser Runtime
- Events
  - E-UC15 ActionIconsRendered
- Policies
  - P-UC15-01 PreserveAccessibleLabels
- Follow-up Commands
  - C-UC14 SaveNote
  - C-UC04 DeleteNote
- Sync/Async Boundary
  - Sync UI render
- Cross-Context Interaction
  - Web UI Context only

## UC-16 본문 중심 3패널 폭 재조정
- Start Command
  - C-UC16 ApplyThreePanelWidthPolicy
- Actors
  - Single User
- External Systems
  - Browser Runtime
- Events
  - E-UC16 CenterPanelExpanded
- Policies
  - P-UC16-01 UseFullViewportWidth
  - P-UC16-02 NarrowSidePanels
- Follow-up Commands
  - 없음
- Sync/Async Boundary
  - Sync UI render
- Cross-Context Interaction
  - Web UI Context only

# Legacy UC Continuity
- UC-01..UC-10 흐름(CRUD/검색/백링크/추천/그래프/shadcn/fold)은 유지되며, 이번 설계는 편집 UX 정책을 추가한다.

# Backlinks
- docs/product-specs/zetenkastel/note-management-program/use-cases.md
- docs/work-units/zetenkastel/note-management-program-20260414-0959/index.md
