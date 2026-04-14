# Properties
doc_path: docs/exec-plans/completed/zetenkastel/note-management-program-20260414-0959/plan.md
status: completed
coverage_gate: YES
title: 노트 편집 화면 정보 밀도 개선 및 단일 저장 UX 구현
domain: zetenkastel
task: note-management-program-20260414-0959
source_use_case_harvest: ../../../use-case-harvests/zetenkastel/note-management-program-20260414-0959/use-case-harvest.md
last_updated: 2026-04-14:10:09

# Discovery Hints (grep)
- `grep -R "note-management-program-20260414-0959" docs/`
- `grep -R "UC-1[1-6]" docs/design-docs/zetenkastel/note-management-program/`
- `grep -R "fileName" frontend/src/App.tsx`

# Purpose / Big Picture
- 편집 화면에서 본문 집중도를 높이기 위해 메타 필드를 compact property 영역으로 재배치한다.
- 생성/수정을 단일 저장 버튼으로 통합하고 파일명을 자동 계산한다.
- 저장/삭제 액션은 아이콘 + hover 설명으로 단순화한다.
- 레이아웃을 full viewport 기반 3패널로 조정해 중앙 본문 폭을 확장한다.

# Progress
- [x] (UC-11, UC-16) 편집 화면 3패널 레이아웃 재구성 및 중앙 본문 확장
- [x] (UC-11) 제목 아래 property panel(타입/태그/링크) compact 배치
- [x] (UC-12) 태그 Enter 입력을 칩 확정 방식으로 구현
- [x] (UC-13) `type/title` 기반 자동 fileName(path key) 계산 및 표시
- [x] (UC-14) 생성/수정 단일 저장 버튼 분기(create/update) 구현
- [x] (UC-15) 저장/삭제 아이콘 버튼 + tooltip/aria-label 적용
- [x] (Validation) `frontend` 빌드로 static 산출물 갱신
- [x] (Validation) `./gradlew test` 실행
- [x] (Validation) `./gradlew e2eTest` 실행
- [x] (Docs) implementation-log 및 work-unit/verification 문서 동기화

# Surprises & Discoveries
- update API는 path key 기반이라 제목/타입 변경 시 path key가 바뀌면 create 분기로 처리하는 정책이 필요하다.

# Decision Log
- 저장 분기는 현재 path key 존재 여부 기준으로 판단한다.
- fileName은 title을 slugify한 값으로 만들고 path key(`type/fileName`)를 read-only로 표시한다.

# Context and Orientation
- Frontend: `frontend/src/App.tsx`
- UI primitives: `frontend/src/components/ui/*`
- Backend API: `src/main/java/com/zetenkastel/core/ui/NoteController.java`
- E2E: `e2e/tests/note-management.spec.ts`

# Event Storming Summary
- ComposeEditorLayout -> PropertyPanelCompacted
- CaptureTagInput -> TagChipCommitted
- DeriveFileNameFromTypeAndTitle -> FileNameAutoDerived
- SaveNote -> NoteSavedAsCreated | NoteSavedAsUpdated
- ApplyThreePanelWidthPolicy -> CenterPanelExpanded

# Aggregate Design Summary
- NoteAggregate: path key 기반 create/update/delete 일관성
- EditorSessionProjection(UI): 태그 칩/자동 fileName/저장 분기 정책

# Bounded Context Summary
- Note Core Context
- Note Discovery Context
- Web UI Context

# Plan of Work
- `App.tsx` 편집 화면 구조를 좌/중/우 3패널로 재편한다.
- form state를 tags 칩 + links 입력 + auto fileName 파생 구조로 변경한다.
- 저장 버튼을 단일 아이콘 액션으로 바꾸고 create/update 분기를 구현한다.
- e2e 시나리오를 새 상호작용(단일 저장/태그 Enter)에 맞춰 갱신한다.

# Concrete Steps
1. `frontend/src/App.tsx` 상태/레이아웃/액션 재구성
2. 필요 시 `frontend/src/styles.css` 보조 스타일 추가
3. `e2e/tests/note-management.spec.ts` 사용자 흐름 업데이트
4. `(repo root/frontend)` `npm run build`
5. `(repo root)` `./gradlew test`
6. `(repo root)` `./gradlew e2eTest`

# Validation and Acceptance
- 태그 입력 후 Enter 시 칩이 생성된다.
- 파일명은 수동 입력 없이 자동 계산된다.
- 저장 버튼 1개로 신규/기존 노트 저장이 모두 동작한다.
- 삭제 버튼은 아이콘 + hover 설명으로 제공된다.
- 중앙 본문 폭이 좌/우 패널 대비 크게 렌더링된다.
- `./gradlew test` 및 `./gradlew e2eTest`가 성공한다.

# Idempotence and Recovery
- 프론트 빌드는 재실행 시 static 산출물을 덮어쓴다.
- e2e 실패 시 `App.tsx`와 e2e 스펙을 동기화한 뒤 재실행한다.

# Documentation Impact
- `docs/design-docs/zetenkastel/note-management-program/*`
- `docs/exec-plans/completed/zetenkastel/note-management-program-20260414-0959/implementation-log.md`
- `docs/verification-reports/zetenkastel/note-management-program-20260414-0959/*`
- `docs/work-units/zetenkastel/note-management-program-20260414-0959/index.md`

# Change Log
- 2026-04-14:10:02 초기 실행 계획 초안 작성
- 2026-04-14:10:09 구현/테스트(test,e2e) 완료 및 execute_writer 동기화

# Backlinks
- docs/work-units/zetenkastel/note-management-program-20260414-0959/index.md
- docs/use-case-harvests/zetenkastel/note-management-program-20260414-0959/use-case-harvest.md
