# Properties
doc_path: docs/exec-plans/completed/zetenkastel/note-management-program/plan.md
status: completed
coverage_gate: YES
title: React+Vite + shadcn/ui UI 전환 및 노트 타입 폴드 탐색 구현
domain: zetenkastel
task: note-management-program
source_use_case_harvest: ../../../use-case-harvests/zetenkastel/note-management-program/use-case-harvest.md
last_updated: 2026-04-14:09:20

# Discovery Hints (grep)
- `grep -R "^# Properties" docs/use-case-harvests/zetenkastel/note-management-program/`
- `grep -R "^# Properties" docs/work-units/zetenkastel/note-management-program/`
- `grep -R "^# Properties" docs/product-specs/zetenkastel/note-management-program/`
- `grep -R "^# Properties" docs/design-docs/zetenkastel/note-management-program/`
- `grep -R "^# Properties" docs/exec-plans/completed/zetenkastel/note-management-program/`

# Purpose / Big Picture
- 사용자는 기존 기능(CRUD/검색/백링크/추천/그래프)을 유지한 채 UI를 React+Vite + shadcn/ui로 사용한다.
- 노트 목록은 타입별 폴드 UI로 정리되어 빠르게 탐색할 수 있다.
- 성공 기준은 웹에서 주요 시나리오가 정상 동작하고 빌드/테스트가 통과하는 것이다.

# Progress
- [x] (UC-09) React + Vite 프로젝트 스캐폴딩 및 Spring static 연동 경로 구성
- [x] (UC-09) shadcn/ui 기반 공통 UI 컴포넌트(button/input/textarea/select/card/collapsible) 추가
- [x] (UC-10) 노트 타입별 폴드 목록 렌더링 및 토글 상태 관리 구현
- [x] (UC-01~UC-08) CRUD/검색/백링크/추천/그래프 API 연동 화면 구성
- [x] (UC-09) 정적 HTML 진입점을 Vite 빌드 산출물로 교체
- [x] (Validation) `./gradlew build` 실행
- [x] (Validation) `./gradlew test` 실행
- [x] (Validation) `./gradlew e2eTest` 실행
- [x] (Docs) implementation-log 및 work-unit index 동기화

# Surprises & Discoveries
- 기존 UI는 순수 정적 HTML이며 React 런타임이 없다.

# Decision Log
- Frontend stack은 사용자 승인값인 React + Vite를 사용한다.
- shadcn/ui는 Tailwind + Radix 기반 컴포넌트로 구성한다.

# Context and Orientation
- Backend API: `src/main/java/com/zetenkastel/core/ui/NoteController.java`
- Domain/Application: `src/main/java/com/zetenkastel/core/app/NoteService.java`
- Current static UI: `src/main/resources/static/index.html`, `src/main/resources/static/graph.html`

# Event Storming Summary
- Note lifecycle command/event: Create/Update/Delete -> NoteCreated/Updated/Deleted
- Discovery command/event: Search/Backlinks/Recommend/Graph -> NotesSearched/BacklinksLoaded/RecommendationsCalculated/GraphBuilt
- UI command/event: RenderShadcnUI/ToggleFold -> UiRenderedWithShadcn/NoteTypeFoldToggled

# Aggregate Design Summary
- NoteAggregate: 쓰기 일관성 경계
- NoteDiscoveryProjection: 검색/추천/그래프 조회 모델

# Bounded Context Summary
- Note Core Context
- Note Discovery Context
- Web UI Context

# Plan of Work
- `frontend/`에 React + Vite 앱 및 shadcn/ui 컴포넌트를 추가한다.
- 앱에서 기존 `/api/*` 엔드포인트를 직접 호출하도록 `api.ts`를 만든다.
- 타입별 그룹을 Collapsible 섹션으로 렌더링한다.
- Vite build 결과를 `src/main/resources/static`에 출력한다.

# Concrete Steps
1. `(repo root)` `mkdir -p frontend` 후 Vite 프로젝트 파일 생성
2. `(repo root)` shadcn 스타일 컴포넌트 파일 생성 및 App 구현
3. `(repo root/frontend)` `npm install && npm run build`
4. `(repo root)` `./gradlew build`
5. `(repo root)` `./gradlew test`
6. `(repo root)` `./gradlew e2eTest`

# Validation and Acceptance
- 노트 타입별 섹션 접기/펼치기가 동작한다.
- 노트 선택 시 편집 폼이 채워진다.
- 검색/백링크/추천/그래프가 정상 렌더링된다.
- `./gradlew build`, `./gradlew test`, `./gradlew e2eTest`가 성공한다.

# Idempotence and Recovery
- Vite build는 재실행 가능하며 `src/main/resources/static`을 덮어쓴다.
- 실패 시 `frontend/src` 수정 후 재빌드한다.

# Documentation Impact
- 설계 문서: domain-boundary/use-cases/event-storming/aggregate-design/bounded-context/detailed-design
- 실행 문서: plan.md, implementation-log.md
- 허브 문서: work-unit index stage 링크/상태

# Change Log
- 2026-04-14:09:02 oracle 승인 이후 실행 계획 초안 생성
- 2026-04-14:09:10 React+Vite + shadcn/ui 전환 구현 및 빌드/테스트 완료
- 2026-04-14:09:20 test_gate PASS 및 completed 경로 이동 준비

# Backlinks
- docs/work-units/zetenkastel/note-management-program/index.md
- docs/use-case-harvests/zetenkastel/note-management-program/use-case-harvest.md
