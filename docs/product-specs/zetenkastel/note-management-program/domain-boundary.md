# Properties
doc_path: docs/product-specs/zetenkastel/note-management-program/domain-boundary.md
owner: Codex
status: draft
domain: zetenkastel
task: note-management-program
last_updated: 2026-04-14:09:02

# Task Summary
- 1인용 제텐카스텔 웹 앱에서 노트 CRUD, 링크/백링크, 검색, 그래프, 추천을 제공한다.
- UI는 React + Vite 기반으로 전환하고 shadcn/ui 컴포넌트를 사용한다.
- 노트 목록은 노트 타입별 폴드(접기/펼치기) 탐색을 제공한다.

# Domain Boundary Candidate
- In Scope
  - Note Lifecycle: 노트 생성/조회/수정/삭제
  - Note Discovery: 검색, 타입별 목록 탐색
  - Link Intelligence: 백링크, 추천 링크
  - Graph Visualization: 노트 연결 그래프 조회
  - Web UI Composition: shadcn/ui 기반 화면 구성
- Out of Scope
  - 인증/인가
  - 백업/동기화
  - 다중 사용자 협업

# External Systems
- Local File System (노트 저장소)
- Browser Runtime (React + Vite frontend)

# Domain Terms
- NoteType: 노트 분류(inbox, fleeting-notes, ...)
- NoteId: `type/fileName` 형식의 경로 기반 식별자
- Backlink: 특정 노트를 참조하는 역방향 연결
- Recommendation: 유사도 계산으로 얻는 링크 후보
- Fold State: 타입 섹션의 접기/펼치기 상태

# Design Tension
- 기존 정적 HTML(`src/main/resources/static`)과 Vite build 산출물의 운영 경로를 일치시켜야 한다.
- 타입 폴드 상태 영속화(localStorage)는 필수 요구가 아니라 옵션으로 처리한다.

# Backlinks
- docs/use-case-harvests/zetenkastel/note-management-program/use-case-harvest.md
- docs/work-units/zetenkastel/note-management-program/index.md
