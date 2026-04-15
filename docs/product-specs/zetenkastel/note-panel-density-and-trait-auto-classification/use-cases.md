# Properties
doc_path: docs/product-specs/zetenkastel/note-panel-density-and-trait-auto-classification/use-cases.md
owner: Codex
status: draft
domain: zetenkastel
task: note-panel-density-and-trait-auto-classification
source_use_case_harvest: docs/use-case-harvests/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/use-case-harvest.md
last_updated: 2026-04-14:11:07

# Confirmed Use Cases
- UC-01 좌측 리스트 패널 밀도 개선
- UC-02 중앙 편집 패널 헤더 문구 제거
- UC-03 reference 규칙 + 속성 합산 데이터 기반 자동 분류
- UC-04 무료 AI 모델 vs 스크립트 방식 판단
- UC-05 분류 방식 의사결정의 1차 결론 기록

# Actor
- 단일 사용자 (인증 없음)

# Classification Strategy Decision
- 1차 분류는 `docs/reference/*`와 `property-union.json` 기반 스크립트 규칙으로 처리한다.
- 무료 AI는 로컬 실행 가능한 모델만 보조 fallback으로 허용하고, 애매한 케이스에만 사용한다.
- `literature`는 출처 증거가 강할 때만 분류하고, 애매하면 `inbox` 유지 + review metadata를 남긴다.

# Non-goals
- 유료 모델 기반 기본 분류기
- reference 규칙 없는 순수 모델 추론

# Backlinks
- docs/use-case-harvests/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/use-case-harvest.md
- docs/work-units/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/index.md
