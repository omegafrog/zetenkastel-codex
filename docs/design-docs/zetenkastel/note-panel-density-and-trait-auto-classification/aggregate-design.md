# Properties
doc_path: docs/design-docs/zetenkastel/note-panel-density-and-trait-auto-classification/aggregate-design.md
owner: Codex
status: draft
domain: zetenkastel
task: note-panel-density-and-trait-auto-classification
last_updated: 2026-04-14:11:07

# Aggregate Design
## Aggregate: NoteAggregate
- Responsibility
  - 노트 저장/수정/삭제와 메타데이터 일관성을 보장한다.
- Commands Handled
  - CreateNote, UpdateNote, DeleteNote, ClassifyInboxNote
- Events Produced
  - NoteCreated, NoteUpdated, NoteDeleted, NoteClassified, DeletedLinksPruned
- Included Entities / Value Objects
  - Note, NoteId, NoteType, LinkMode, NoteMetadata
- Invariants
  - `type/fileName` path key는 유일하다
  - canonical metadata key만 저장한다
  - classification이 애매하면 원래 타입을 강제 변경하지 않는다
  - 기존 legacy note는 읽을 수 있어야 한다
- Transaction Boundary Rationale
  - 저장과 분류 결과 반영은 동일 note write 경계 안에서 처리한다

## Aggregate: ClassificationPolicy
- Responsibility
  - reference 규칙과 property-union을 이용해 분류 점수를 계산한다.
- Commands Handled
  - EvaluateClassification
- Events Produced
  - MetadataNormalized, ClassificationScored, ClassificationDeferred
- Included Entities / Value Objects
  - ClassificationRuleSet, ClassificationResult, MetadataSignal
- Invariants
  - literature는 출처 증거가 충분할 때만 승급한다
  - fallback AI는 primary score가 낮을 때만 호출 대상이다
  - 판단 근거는 테스트 가능해야 한다
- Transaction Boundary Rationale
  - 저장 전에 pure rule evaluation으로 계산해 side effect를 최소화한다

## Aggregate: EditorSessionProjection
- Responsibility
  - 좌측 리스트/중앙 편집기의 밀도와 입력 상태를 관리한다.
- Commands Handled
  - CompactListPanelLayout, RemoveEditorHeaderTitle, SaveNoteIntent
- Events Produced
  - ListPanelCompacted, EditorHeaderSimplified, SaveTriggered
- Included Entities / Value Objects
  - FormState, FoldState, DerivedPathKey
- Invariants
  - 좌측 패널은 과도한 중첩 여백 없이 렌더링된다
  - 편집 패널 상단 title label은 렌더링하지 않는다

# Backlinks
- docs/design-docs/zetenkastel/note-panel-density-and-trait-auto-classification/event-storming.md
- docs/work-units/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/index.md
