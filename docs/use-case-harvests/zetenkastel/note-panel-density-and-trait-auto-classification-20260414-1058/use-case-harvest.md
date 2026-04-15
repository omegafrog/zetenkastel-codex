# Properties
doc_path: docs/use-case-harvests/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/use-case-harvest.md
owner: Codex
status: ready-for-oracle
title: 좌측 패널 공간 확보 및 reference 규칙 기반 노트 특성 자동 분류
domain: zetenkastel
task: note-panel-density-and-trait-auto-classification-20260414-1058
coverage_gate: YES
next_step: wait-for-user-approval
last_updated: 2026-04-14:10:58

# Prompt Interpretation
- User Goal
  - 좌측 노트 리스트 패널의 실제 사용 폭을 늘리고, 편집 헤더의 불필요한 문구를 제거하며, `docs/reference` 규칙을 기반으로 노트 특성을 자동 분류하고 싶다.
- Requested Actions
  - 좌측 리스트 패널 다층 구조를 단순화(패딩 축소 또는 레이어 축소)하여 공간을 확보한다.
  - 중앙 패널 상단 `노트 편집` 문구를 제거한다.
  - `docs` 하위 reference 규칙을 근거로 노트 특성을 자동 분류한다.
  - 자동 분류를 무료 AI 모델로 할지 스크립트로 할지 판단한다.
- Preferred Implementation Stack (language/framework/runtime)
  - Backend: Java 21 + Spring Boot
  - Frontend: React + Vite
- Constraints
  - 빌드/테스트 실패를 무시하지 않는다.
  - 분류 규칙 Source of Truth는 `docs/reference/*` 문서다.
  - 템플릿 기반 속성 합산 결과(`docs/reference/property-union.md`, `docs/reference/property-union.json`)를 기준 데이터로 사용한다.
- Expected Outcome
  - 좌측 패널 가독성이 개선되고 불필요한 헤더가 제거되며, 규칙 기반 자동 분류 전략(스크립트 우선/무료 AI 보조 여부)이 결정된다.
- Explicit Non-goals
  - 유료 API 비용을 전제로 한 1차 구현
  - reference 규칙을 무시한 하드코딩 분류

# Candidate Use Cases
- UC-01 ~ UC-05를 candidate로 수집 후 confirmed 판정.

# Confirmed Use Cases
## UC-01: 좌측 리스트 패널 밀도 개선
- Primary Actor: 노트 편집 사용자
- Goal: 패널 내 텍스트 가독성과 항목 탐색 효율을 높인다.
- Preconditions
  - 좌측 패널이 다층 래퍼/과도한 패딩 구조를 가진다.
- Trigger
  - 사용자가 노트 목록을 탐색한다.
- Main Success Flow
  1. 시스템이 중첩 레이어를 축소하거나 내부 패딩을 줄인다.
  2. 시스템이 접기/펼치기, 선택, 스크롤 기능을 유지한다.
  3. 사용자가 더 넓어진 리스트 영역에서 노트를 탐색한다.
- Alternative / Failure Flow
  - 축소 후 클릭 타깃 접근성이 저하되면 최소 클릭 영역을 보장하도록 보정한다.
- Success Outcome
  - 리스트 패널의 유효 콘텐츠 폭이 증가한다.
- Source Prompt Evidence
  - "좌측 노트 리스트 패널이 여러 층으로 되어 있어서 너무 좁아. 패널의 패딩을 줄이던지 레이어를 한층 낮추던지 해서 공간을 확보해"
- Status: confirmed

## UC-02: 중앙 편집 패널 헤더 문구 제거
- Primary Actor: 노트 편집 사용자
- Goal: 편집 화면의 시각적 잡음을 줄인다.
- Preconditions
  - 중앙 패널 상단에 `노트 편집` 텍스트가 표시된다.
- Trigger
  - 사용자가 편집 화면을 연다.
- Main Success Flow
  1. 시스템이 `노트 편집` 텍스트를 렌더링하지 않는다.
  2. 레이아웃은 유지되고 본문/속성 편집 기능은 그대로 제공된다.
- Alternative / Failure Flow
  - 문구 제거 후 상단 간격이 깨지면 헤더/본문 spacing을 재조정한다.
- Success Outcome
  - 편집 화면이 간결해진다.
- Source Prompt Evidence
  - "노트 편집 이라고 중앙 패널 상단에 써져 있는데, 그 부분 없애."
- Status: confirmed

## UC-03: reference 규칙 + 속성 합산 데이터 기반 자동 분류
- Primary Actor: 노트 편집 사용자
- Goal: 노트 저장/검토 시 특성이 자동으로 부여되도록 한다.
- Preconditions
  - `docs/reference/*` 규칙 문서가 존재한다.
  - 합산 속성 정의(`property-union.md/.json`)가 존재한다.
- Trigger
  - 사용자가 노트를 저장하거나 분류 실행을 요청한다.
- Main Success Flow
  1. 시스템이 reference 문서와 속성 합산 정의를 로드한다.
  2. 시스템이 노트의 메타/본문 신호를 분석해 특성 후보를 계산한다.
  3. 시스템이 우선순위 규칙에 따라 최종 특성을 확정한다.
  4. 시스템이 특성을 노트 메타데이터(또는 태그)로 반영한다.
- Alternative / Failure Flow
  - 규칙 파싱 실패 시 원인 로그를 남기고 수동 분류로 fallback한다.
  - 충돌 규칙 다발 시 `needs-review` 상태로 표시한다.
- Success Outcome
  - 노트가 reference 규칙 기준으로 일관되게 분류된다.
- Source Prompt Evidence
  - "각 노트의 특성은 내가 docs아래에 reference를 만들어서 거기에 넣어 둘게. 그 규칙을 참조해서 각 노트를 자동으로 분류해야 해."
- Status: confirmed

## UC-04: 무료 AI 모델 vs 스크립트 방식 판단
- Primary Actor: 제품 의사결정자(동일 사용자)
- Goal: 무비용 운영 기준으로 분류 구현 전략을 선택한다.
- Preconditions
  - 분류 규칙과 속성 집합이 정의되어 있다.
- Trigger
  - 사용자가 자동 분류 방식 결정을 요청한다.
- Main Success Flow
  1. 시스템/설계가 규칙 복잡도, 설명가능성, 유지보수성을 평가한다.
  2. 스크립트 방식(룰 엔진/키워드 매칭)과 무료 AI 로컬 모델 방식을 비교한다.
  3. 1차 도입 전략을 확정한다.
- Alternative / Failure Flow
  - 정확도 목표 미달 시 하이브리드(스크립트 1차 + 무료 AI 재평가)를 채택한다.
- Success Outcome
  - 구현 방식이 결정되고 실행 계획으로 이관 가능해진다.
- Source Prompt Evidence
  - "이걸 하기 위한 무료 ai 모델이나 아니면 스크립트로 처리할 수 있는지 판단해야 해."
- Status: confirmed

## UC-05: 분류 방식 의사결정의 1차 결론 기록
- Primary Actor: 제품 의사결정자(동일 사용자)
- Goal: 현재 레포 상태에서 즉시 구현 가능한 전략을 문서화한다.
- Preconditions
  - 현재 저장 포맷 제한(`title/tags/links + content`)이 인지되어 있다.
- Trigger
  - 분류 구현 착수 전 설계 결론을 확정한다.
- Main Success Flow
  1. 시스템이 `property-union` 문서에서 canonical key 매핑을 사용한다.
  2. 1차는 스크립트 기반 분류(결정 규칙)로 구현한다.
  3. 필요 시 무료 로컬 AI 모델을 2차 보조 단계로 명시한다.
- Alternative / Failure Flow
  - 문서 포맷 불일치가 증가하면 parser 강화 또는 YAML frontmatter 도입 계획을 추가한다.
- Success Outcome
  - 무비용/설명가능/테스트가능한 분류 전략이 확보된다.
- Source Prompt Evidence
  - "기존의 properties와 각 template 파일 내부의 속성들을 참고해서 필요한 속성들을 합산"
- Status: confirmed

# Coverage Mapping
- Prompt Requirement -> Mapped Use Case IDs
  - 좌측 패널 공간 확보(패딩/레이어): UC-01
  - 중앙 상단 `노트 편집` 제거: UC-02
  - reference 규칙 기반 자동 분류: UC-03
  - 무료 AI vs 스크립트 판단: UC-04
  - 합산 속성 기반 결과 반영: UC-03, UC-05
- Coverage Gaps
  - 없음 (현 시점 요구는 confirmed use case로 매핑 완료)

# Coverage Gate
- Ready for Event Storming: YES
- Why
  - 사용자 목표와 액션이 모두 confirmed use case로 매핑되었고, 주요 실패 흐름(파싱 실패/충돌/정확도 미달)도 정의되었다. 분류 규칙 입력은 `docs/reference`와 `property-union`으로 식별 가능하며, 구현 범위/비범위도 구분 가능하다.
- Blocking Conditions
  - 없음

# Blocking Unknowns
- 없음 (설계 착수 차단 요소 없음)

# Needs Review
- 무료 AI 모델 도입은 2차 보조 전략으로 유지하고, 1차 릴리즈는 규칙 스크립트 기반으로 제한할지 최종 합의 필요
- 메타데이터 저장 포맷을 현행 헤더 방식으로 유지할지 YAML frontmatter로 확장할지 실행 단계에서 선택 필요

# Rejected Use Cases
- 외부 유료 분류 API 호출 기반 기본 설계
- reference 규칙 없이 임의 모델 추론만으로 특성 확정

# Missing-but-Plausible Use Cases
- UC-MP-01: 분류 결과 수동 수정 시 규칙 개선 피드백 루프
- UC-MP-02: reference 갱신 후 기존 노트 일괄 재분류 배치

# Next Revision Focus
- 실행 단계에서 분류 결과 저장 위치(태그 확장 vs 메타 필드 확장)를 확정한다.
- 분류 정확도 측정 지표(precision/recall 또는 승인율)를 테스트 케이스로 정의한다.

# Oracle Handoff
- Allowed To Proceed: YES
- Confirmed Use Cases for Oracle
  - UC-01
  - UC-02
  - UC-03
  - UC-04
  - UC-05
- Assumptions Forbidden for Oracle
  - `docs/reference`와 `property-union`을 무시하고 분류 규칙을 임의 정의하지 말 것
  - 유료 API 전제를 기본안으로 채택하지 말 것
  - 현행 저장 포맷 한계를 고려하지 않은 설계를 하지 말 것
- User Approval Required Before Orchestration: YES

# Backlinks
- docs/work-units/zetenkastel/note-panel-density-and-trait-auto-classification-20260414-1058/index.md
