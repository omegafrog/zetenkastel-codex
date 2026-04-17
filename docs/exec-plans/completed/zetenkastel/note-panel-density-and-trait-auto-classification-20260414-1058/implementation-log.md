# Properties
doc_path: docs/exec-plans/completed/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/implementation-log.md
status: completed
domain: zetenkastel
task: note-panel-density-and-trait-auto-classification-20260414-1058
last_updated: 2026-04-14:11:30

# Summary
- note 저장 포맷을 metadata frontmatter로 확장하고, inbox note 저장 시 reference/property-union 기반 규칙 분류를 적용했다.
- 좌측 패널 밀도를 높이고 중앙 편집 패널 상단의 `노트 편집` 헤더를 제거했다.

# Implemented Scope
- `Note` domain 모델에 `metadata` 맵 추가
- `LocalFileNoteRepository` frontmatter 저장 및 legacy header read 호환
- `LocalClassificationRulesProvider`, `NoteClassificationService` 추가
- `NoteService`에 save-time classification / move / metadata-aware search 적용
- `NoteController`와 프런트 타입에 metadata 계약 추가
- `App.tsx` compact list panel, header removal, classified save response 처리
- repository/controller/e2e 테스트 추가

# File Changes
## Created
- src/main/java/com/zetenkastel/core/adapter/LocalClassificationRulesProvider.java
- src/main/java/com/zetenkastel/core/app/NoteClassificationService.java
- src/main/java/com/zetenkastel/core/port/ClassificationRulesProvider.java
- src/test/java/com/zetenkastel/core/adapter/LocalFileNoteRepositoryTest.java
- docs/exec-plans/active/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/implementation-log.md

## Modified
- src/main/java/com/zetenkastel/core/domain/Note.java
- src/main/java/com/zetenkastel/core/adapter/LocalFileNoteRepository.java
- src/main/java/com/zetenkastel/core/app/NoteService.java
- src/main/java/com/zetenkastel/core/ui/NoteController.java
- src/test/java/com/zetenkastel/core/ui/NoteControllerTest.java
- frontend/src/App.tsx
- frontend/src/lib/types.ts
- e2e/tests/note-management.spec.ts
- src/main/resources/static/index.html
- src/main/resources/static/assets/index-BOhj5KF1.css
- src/main/resources/static/assets/index-BazouerV.js

## Deleted / Renamed
- src/main/resources/static/assets/index-Dz1oMfEf.css
- src/main/resources/static/assets/index-YKGx7BzN.js

# Code-to-Plan Mapping
- Progress 1: 완료 (`frontend/src/App.tsx`)
- Progress 2: 완료 (`Note`, `NoteController`, `frontend/src/lib/types.ts`)
- Progress 3: 완료 (`LocalFileNoteRepository`)
- Progress 4: 완료 (`LocalClassificationRulesProvider`, `NoteClassificationService`, `NoteService`)
- Progress 5: 완료 (`NoteControllerTest`, `LocalFileNoteRepositoryTest`)
- Progress 6: 완료 (`e2e/tests/note-management.spec.ts`)
- Progress 7: 완료 (`./gradlew test`)
- Progress 8: 완료 (`./gradlew e2eTest`)
- Progress 9: 완료 (verification/work-unit/doc sync)

# External Contract Changes
- `/api/notes*` 응답/요청에 `metadata` 필드가 추가되었다.

# Policy / Domain Rule Changes
- inbox note는 저장 시 source evidence / planning evidence / review evidence를 기준으로 분류 점수를 계산한다.
- 최고 점수가 임계치를 넘고 동점이 아니면 target note type으로 저장된다.
- 근거가 약하면 inbox 유지 + `classification_review: needs-review`로 기록한다.

# Architectural Impact
- ARCHITECTURE.md의 계층 방향은 유지했다.
- `NoteService`는 port(`ClassificationRulesProvider`, `NoteRepository`)에만 의존한다.

# Documentation Updates
- base-task 설계 문서와 active plan 작성
- pre/post doc verify, test gate, closure 보고서 작성

# Validation Summary
- `./gradlew test` PASS
- `(frontend) npm run build` PASS
- `./gradlew e2eTest` PASS

# Remaining Gaps
- free local AI fallback은 설계에만 반영했고 코드 기본 경로에는 넣지 않았다.
- metadata 수동 편집 UI는 아직 최소화 상태다.

# Risks & Follow-ups
- `property-union.json`이 바뀌면 저장되는 blank metadata 키 집합도 함께 바뀐다.
- 실제 note type enum은 reference 템플릿 전체를 완전히 표현하지 않으므로 unsupported template type은 후속 범위다.

# Discovery Hints (grep)
- `grep -R "classification_review" src/main/java/`
- `grep -R "metadata" src/main/java/com/zetenkastel/core/ui/ frontend/src/`
- `grep -R "frontmatter" src/test/java/`

# Backlinks
- docs/work-units/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/index.md
- docs/exec-plans/completed/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/plan.md
