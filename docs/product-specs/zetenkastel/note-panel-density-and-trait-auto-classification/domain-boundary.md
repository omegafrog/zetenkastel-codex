# Properties
doc_path: docs/product-specs/zetenkastel/note-panel-density-and-trait-auto-classification/domain-boundary.md
owner: Codex
status: draft
title: 노트 패널 밀도 개선 및 규칙 기반 자동 분류 도메인 경계
domain: zetenkastel
task: note-panel-density-and-trait-auto-classification
last_updated: 2026-04-14:11:07

# Scope
- 좌측 노트 리스트 패널의 중첩 레이어와 패딩을 줄여 유효 폭을 늘린다.
- 중앙 편집 패널 상단의 `노트 편집` 헤더를 제거한다.
- note 저장 포맷을 메타데이터 확장 가능 구조로 바꾸되 기존 파일 읽기 호환성을 유지한다.
- `docs/reference/*`와 `docs/reference/property-union.json`을 기준으로 inbox 노트를 자동 분류한다.

# External Boundaries
- Local File System: 노트 저장소 및 reference 규칙 원본
- Browser Runtime: 편집 UI, 저장 액션, 상태 메시지

# In Scope
- 패널 밀도 정책, 헤더 제거, 상태 메시지 유지
- note metadata 저장/조회 계약 확장
- rule-based classifier
- script-first / free local AI fallback 설계 문서화
- regression/unit/e2e 테스트

# Out of Scope
- 유료 외부 AI API 연동
- 다중 사용자/인증
- reference 규칙 편집 UI

# Key Constraints
- ARCHITECTURE.md 레이어 규칙 유지
- 기존 note 파일은 read-compatible 해야 한다
- classification evidence는 테스트 가능한 규칙으로 설명 가능해야 한다

# Discovery Hints (grep)
- `grep -R "property-union" docs/reference/`
- `grep -R "note-panel-density-and-trait-auto-classification" docs/`
- `grep -R "LocalFileNoteRepository" src/main/java/`

# Backlinks
- docs/use-case-harvests/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/use-case-harvest.md
- docs/work-units/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/index.md
