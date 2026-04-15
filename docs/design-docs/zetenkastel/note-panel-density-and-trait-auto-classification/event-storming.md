# Properties
doc_path: docs/design-docs/zetenkastel/note-panel-density-and-trait-auto-classification/event-storming.md
owner: Codex
status: draft
domain: zetenkastel
task: note-panel-density-and-trait-auto-classification
last_updated: 2026-04-14:11:07

# Event Storming
## UC-01 좌측 리스트 패널 밀도 개선
- Start Command
  - C-UC01 CompactListPanelLayout
- Actors
  - Single User
- External Systems
  - Browser Runtime
- Events
  - E-UC01 ListPanelCompacted
- Policies
  - P-UC01-01 PreferSingleContainerBorder
  - P-UC01-02 PreserveFoldAndSelectionActions
- Follow-up Commands
  - 없음
- Sync/Async Boundary
  - Sync UI render
- Cross-Context Interaction
  - Web UI Context only

## UC-02 중앙 편집 패널 헤더 문구 제거
- Start Command
  - C-UC02 RemoveEditorHeaderTitle
- Actors
  - Single User
- External Systems
  - Browser Runtime
- Events
  - E-UC02 EditorHeaderSimplified
- Policies
  - P-UC02-01 PreserveEditorSpacingWithoutTitle
- Follow-up Commands
  - 없음
- Sync/Async Boundary
  - Sync UI render
- Cross-Context Interaction
  - Web UI Context only

## UC-03 reference 규칙 + 속성 합산 데이터 기반 자동 분류
- Start Command
  - C-UC03 SaveInboxNote
- Actors
  - Single User
- External Systems
  - Local File System
  - Reference Docs under `docs/reference/*`
- Events
  - E-UC03 MetadataCanonicalized
  - E-UC03-2 ClassificationScored
  - E-UC03-3 NoteClassified
- Policies
  - P-UC03-01 NormalizeMetadataKeys
  - P-UC03-02 PreferScriptRulesAsPrimaryClassifier
  - P-UC03-03 KeepInboxWhenEvidenceIsAmbiguous
- Follow-up Commands
  - C-UC03A PersistMetadataFrontmatter
- Sync/Async Boundary
  - Sync save path, sync classification
- Cross-Context Interaction
  - Web UI Context -> Note Core Context

## UC-04 무료 AI 모델 vs 스크립트 방식 판단
- Start Command
  - C-UC04 EvaluateClassificationStrategy
- Actors
  - Product Decision Maker
- External Systems
  - Local Runtime Only
- Events
  - E-UC04 StrategyDecided
- Policies
  - P-UC04-01 ScriptFirstAIFallbackOnly
- Follow-up Commands
  - C-UC03 SaveInboxNote
- Sync/Async Boundary
  - Sync design/runtime decision
- Cross-Context Interaction
  - Note Core Context only

## UC-05 분류 방식 의사결정의 1차 결론 기록
- Start Command
  - C-UC05 MaterializePropertyUnionPolicy
- Actors
  - Product Decision Maker
- External Systems
  - `docs/reference/property-union.json`
- Events
  - E-UC05 CanonicalMetadataSchemaAdopted
- Policies
  - P-UC05-01 PersistCanonicalKeys
  - P-UC05-02 PreserveLegacyReadCompatibility
- Follow-up Commands
  - C-UC03 SaveInboxNote
- Sync/Async Boundary
  - Sync parse and persist
- Cross-Context Interaction
  - Note Core Context only

# Backlinks
- docs/product-specs/zetenkastel/note-panel-density-and-trait-auto-classification/use-cases.md
- docs/work-units/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/index.md
