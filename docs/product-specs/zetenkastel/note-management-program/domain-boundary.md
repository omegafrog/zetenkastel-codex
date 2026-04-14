# Properties
doc_path: docs/product-specs/zetenkastel/note-management-program/domain-boundary.md
owner: Codex
status: draft
domain: zetenkastel
task: note-management-program
last_updated: 2026-04-14:10:12

# Task Summary
- 기존 노트 웹 앱에서 편집 화면의 메타데이터 과밀 문제를 줄이고 본문 중심 UX로 재구성한다.
- 저장 동작은 생성/수정을 통합한 단일 저장 액션으로 단순화한다.
- 파일명은 `노트타입/제목` 규칙을 따르는 path key를 기준으로 자동 산출한다.

# Domain Boundary Candidate
- In Scope
  - Note Edit UX Compaction: 제목 우선, property 영역(노트 타입/태그/링크) 축소 배치
  - Tag Input Policy: Enter 기반 태그 칩 확정
  - Save Semantics: 단일 저장 버튼에서 create/update 분기
  - File Naming Policy: `type/title-slug` 기반 식별자 계산
  - Layout Policy: 좌/우 패널 축소 + 중앙 본문 확장
  - Action Affordance: 저장/삭제 아이콘 + hover 설명
- Out of Scope
  - 인증/인가
  - 백업/동기화
  - 저장소(Local File) 변경

# External Systems
- Local File System (노트 저장소)
- Browser Runtime (React + Vite)

# Domain Terms
- Property Panel: 제목 아래 메타데이터 입력 영역
- Path Key: `type/fileName`
- Auto FileName: 제목에서 정규화된 `fileName`
- Save Intent: 단일 저장 액션으로 upsert 분기 판단
- Tag Chip: Enter 확정된 태그 시각 블록

# Design Tension
- 기존 API는 update 경로가 `/{type}/{fileName}`이므로 path key 변경 시 create로 처리되는 UX 기준이 필요하다.
- 검색 결과 상태와 전체 노트 상태가 다를 때 save 분기 판정 기준을 안정적으로 유지해야 한다.

# Backlinks
- docs/use-case-harvests/zetenkastel/note-management-program-20260414-0959/use-case-harvest.md
- docs/work-units/zetenkastel/note-management-program-20260414-0959/index.md
