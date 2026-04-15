# Properties
doc_path: docs/design-docs/zetenkastel/note-panel-density-and-trait-auto-classification/detailed-design.md
owner: Codex
status: draft
domain: zetenkastel
task: note-panel-density-and-trait-auto-classification
last_updated: 2026-04-14:11:07

# Detailed Design
## Layer Mapping (ARCHITECTURE.md alignment)
- ui layer
  - Backend: `src/main/java/com/zetenkastel/core/ui/NoteController.java`
  - Frontend: `frontend/src/App.tsx`
- app layer
  - `src/main/java/com/zetenkastel/core/app/NoteService.java`
- domain layer
  - `src/main/java/com/zetenkastel/core/domain/*`
- port layer
  - `src/main/java/com/zetenkastel/core/port/NoteRepository.java`
- adapter layer
  - `src/main/java/com/zetenkastel/core/adapter/LocalFileNoteRepository.java`

## Backend Design
- `Note`에 `Map<String, String> metadata`를 추가한다.
- 저장 포맷은 YAML-like frontmatter(`---` ... `---`)로 직렬화한다.
- legacy file(`title/tags/links` header)도 계속 읽는다.
- `NoteService.create/update`에서 저장 전에 metadata를 normalize하고, inbox note면 classifier를 적용한다.
- classifier는 `docs/reference/property-union.json`을 읽어 canonical key 목록을 참고하고, `docs/reference/*.md`에서 규칙 신호를 읽는다.

## Classification Rule Design
- input signals
  - metadata keys, tags, title tokens, content tokens, source evidence
- primary rules
  - `literature-notes`: `author/source/url` 또는 출처 표현 강함
  - `fleeting-notes`: 짧은 메모/아이디어/질문, 출처 약함
  - `projects`: 기간/우선순위/작업 목록/블로커
  - `area`: 검토 주기/지속 책임/상태
  - `maps-of-content`: MOC 구조/지식 지도 표현
- resolution
  - 최고 점수 + 임계치 충족 시 분류
  - 동점 또는 낮은 점수면 `inbox` 유지
- AI strategy
  - 설계 문서에는 free local AI fallback만 명시
  - 코드 1차 구현은 rule-based only로 완료 가능해야 한다

## Frontend Design
- 좌측 패널 outer card/content padding을 줄이고 note list 내부 wrapper를 단순화한다.
- type heading과 note item density를 높이되 클릭 타깃은 유지한다.
- 중앙 편집 패널 `CardTitle`을 제거한다.
- metadata editor는 처음엔 최소 필드만 노출하고, `metadata`는 백엔드에서 자동 보강한다.

## Test Points
- legacy file deserialize compatibility
- frontmatter serialize/deserialize round-trip
- inbox note classification to fleeting/literature/project cases
- ambiguous note remains in inbox
- list panel and header UI regression

# Backlinks
- docs/design-docs/zetenkastel/note-panel-density-and-trait-auto-classification/bounded-context.md
- docs/work-units/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/index.md
