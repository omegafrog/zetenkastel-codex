# Properties
doc_path: docs/exec-plans/completed/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/plan.md
status: completed
title: 패널 밀도 개선 및 metadata 기반 자동 분류 구현
domain: zetenkastel
task: note-panel-density-and-trait-auto-classification-20260414-1058
source_use_case_harvest: ../../../use-case-harvests/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/use-case-harvest.md
coverage_gate: YES
last_updated: 2026-04-14:11:30

# Discovery Hints (grep)
- `grep -R "note-panel-density-and-trait-auto-classification-20260414-1058" docs/`
- `grep -R "property-union" docs/reference/`
- `grep -R "title: " data/notes src/main/java/`
- `grep -R "노트 편집" frontend/src/`

# Purpose / Big Picture
- 좌측 리스트 패널의 과한 래퍼와 패딩을 줄여 더 많은 노트를 같은 폭에서 볼 수 있게 한다.
- note 저장 포맷을 metadata-capable 구조로 바꾸고, inbox note 저장 시 reference 규칙 기반 자동 분류를 수행한다.
- 분류 전략은 script-first이며, free local AI는 설계상 fallback으로만 유지한다.

# Progress
- [x] (UC-01, UC-02) `frontend/src/App.tsx` 리스트 패널 밀도 개선 및 편집 패널 헤더 제거
- [x] (UC-03, UC-05) `Note`, controller DTO, API types에 metadata 계약 추가
- [x] (UC-03, UC-05) `LocalFileNoteRepository` frontmatter 저장/legacy read 호환 구현
- [x] (UC-03, UC-04, UC-05) reference/property-union 기반 rule classifier 구현
- [x] (Validation) backend/controller tests 확장
- [x] (Validation) e2e UI 회귀 검증 갱신
- [x] (Validation) `./gradlew test` 실행
- [x] (Validation) `./gradlew e2eTest` 실행
- [x] (Docs) implementation-log 및 verification/work-unit 동기화

# Surprises & Discoveries
- 기존 프론트는 metadata 입력 UI가 없으므로 1차는 자동 생성/자동 분류 위주로 설계한다.
- 현재 e2e는 `#noteList`와 저장 버튼의 접근성 이름에 의존한다.

# Decision Log
- metadata 저장 키는 `property-union.json`의 canonical mapping을 따른다.
- YAML frontmatter 형식을 사용하되 레거시 header read를 유지한다.
- AI fallback은 코드 1차 구현에서 호출하지 않고 문서 정책으로만 남긴다.

# Context and Orientation
- Frontend: `frontend/src/App.tsx`, `frontend/src/lib/types.ts`, `frontend/src/lib/api.ts`
- Backend UI/API: `src/main/java/com/zetenkastel/core/ui/NoteController.java`
- App orchestration: `src/main/java/com/zetenkastel/core/app/NoteService.java`
- Persistence: `src/main/java/com/zetenkastel/core/adapter/LocalFileNoteRepository.java`
- Tests: `src/test/java/com/zetenkastel/core/ui/NoteControllerTest.java`, `e2e/tests/note-management.spec.ts`

# Plan of Work
- domain/app/ui 계약에 metadata를 추가한다.
- repository를 frontmatter capable parser/writer로 바꾼다.
- rule-based classifier를 추가해 inbox note를 저장 시 재분류한다.
- frontend를 compact하게 조정하되 기존 create/update/delete/search flow는 유지한다.

# Concrete Steps
1. domain `Note`와 controller/api DTO에 `metadata` 추가
2. repository parser/writer 구현 + legacy compatibility test
3. classification policy/service 구현 + controller integration test
4. frontend compact layout, header removal, metadata 응답 수용
5. e2e spec 갱신
6. `./gradlew test`
7. `./gradlew e2eTest`

# Validation and Acceptance
- 기존 note 파일이 깨지지 않고 읽힌다.
- 새로 저장된 note 파일은 metadata frontmatter를 가진다.
- literature 근거가 있는 inbox note는 literature-notes로 저장된다.
- 애매한 inbox note는 inbox에 남는다.
- 좌측 패널이 기존보다 더 촘촘하게 렌더링된다.
- 중앙 편집 패널에서 `노트 편집` 텍스트가 사라진다.
- `./gradlew test` 및 `./gradlew e2eTest`가 성공한다.

# Idempotence and Recovery
- 동일 plan 기준 재실행 시 frontmatter 저장은 같은 canonical key 순서를 유지한다.
- classification rule이 맞지 않으면 inbox 유지가 안전 fallback이다.

# Documentation Impact
- `docs/product-specs/zetenkastel/note-panel-density-and-trait-auto-classification/*`
- `docs/design-docs/zetenkastel/note-panel-density-and-trait-auto-classification/*`
- `docs/exec-plans/completed/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/*`
- `docs/verification-reports/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/*`
- `docs/work-units/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/index.md`

# Change Log
- 2026-04-14:11:07 active execution plan drafted
- 2026-04-14:11:30 implementation, build, tests, and docs sync completed

# Backlinks
- docs/work-units/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/index.md
- docs/use-case-harvests/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/use-case-harvest.md
