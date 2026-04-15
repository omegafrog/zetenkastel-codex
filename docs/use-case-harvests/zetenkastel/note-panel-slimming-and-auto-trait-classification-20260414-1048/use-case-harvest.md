# Properties
doc_path: docs/use-case-harvests/zetenkastel/note-panel-slimming-and-auto-trait-classification-20260414-1048/use-case-harvest.md
owner: Codex
status: draft
title: 노트 리스트 패널 공간 확보 및 규칙 기반 노트 특성 자동 분류 요구 수집
domain: zetenkastel
task: note-panel-slimming-and-auto-trait-classification-20260414-1048
coverage_gate: PARTIAL
next_step: revise-harvest
last_updated: 2026-04-14:10:48

# Prompt Interpretation
- User Goal
  - 좌측 노트 리스트 패널의 실사용 폭을 늘리고, 편집 화면 상단의 불필요한 레이블을 제거하며, 문서화된 규칙을 기준으로 노트 특성을 자동 분류하는 방식을 도입하고자 한다.
- Requested Actions
  - 좌측 노트 리스트 패널이 다층 구조로 좁아진 문제를 해결하기 위해 패딩 축소 또는 레이어 축소를 적용한다.
  - 중앙 패널 상단의 `노트 편집` 텍스트를 제거한다.
  - `docs` 하위 reference 규칙을 참조해 각 노트를 자동 분류하는 기능을 설계 범위에 포함한다.
  - 자동 분류 구현 방식으로 무료 AI 모델 사용 가능성 또는 스크립트 기반 처리 가능성을 판단한다.
- Preferred Implementation Stack (language/framework/runtime)
  - Backend: Java 21 + Spring Boot
  - Frontend: React + Vite
- Constraints
  - 빌드/테스트 실패를 무시하지 않는다.
  - 분류 규칙의 Source of Truth는 사용자가 `docs` 하위에 관리하는 reference 문서다.
- Expected Outcome
  - 리스트 패널의 가시 폭이 증가하고, 중앙 편집 패널 헤더가 단순화되며, 노트 특성 자동 분류 전략(무료 AI vs 스크립트)의 적용 가능성이 명확해진다.
- Explicit Non-goals
  - 유료 모델 사용을 전제로 한 설계 확정
  - reference 문서가 없는 상태에서 임의 분류 규칙 하드코딩

# Candidate Use Cases
- UC-01 ~ UC-04를 candidate로 수집 후 confirmed로 판정한다.

# Confirmed Use Cases
## UC-01: 리스트 패널 밀도 최적화로 가시 폭 확보
- Primary Actor: 노트 편집 사용자
- Goal: 좌측 노트 리스트에서 더 많은 항목을 한 번에 인식할 수 있도록 유효 폭을 확보한다.
- Preconditions
  - 노트 리스트 패널이 렌더링되어 있다.
  - 패널 내부에 다층 래퍼(중첩 레이아웃) 또는 과도한 패딩이 존재한다.
- Trigger
  - 사용자가 노트 리스트를 탐색한다.
- Main Success Flow
  1. 시스템이 패널 내부 여백/레이어 구조를 재조정한다.
  2. 시스템이 기존 기능(폴드/선택/스크롤)을 유지하면서 콘텐츠 가용 폭을 늘린다.
  3. 사용자가 더 넓어진 리스트 영역에서 노트를 탐색한다.
- Alternative / Failure Flow
  - 밀도 조정 중 클릭 영역이 줄어 접근성이 저하되면 최소 터치 타깃 기준을 만족하도록 보정한다.
  - 레이어 축소로 스타일 충돌이 발생하면 패딩 조정 기반 대안으로 fallback한다.
- Success Outcome
  - 좌측 패널에서 항목 가독성과 탐색 효율이 개선된다.
- Source Prompt Evidence
  - "좌측 노트 리스트 패널이 여러 층으로 되어 있어서 너무 좁아. 패널의 패딩을 줄이던지 레이어를 한층 낮추던지 해서 공간을 확보해"
- Status: confirmed

## UC-02: 중앙 편집 패널 상단 고정 문구 제거
- Primary Actor: 노트 편집 사용자
- Goal: 편집 화면 상단의 불필요한 문구를 제거해 시각적 잡음을 줄인다.
- Preconditions
  - 중앙 편집 패널 상단에 `노트 편집` 표시가 존재한다.
- Trigger
  - 사용자가 노트를 열어 편집 화면에 진입한다.
- Main Success Flow
  1. 시스템이 상단 `노트 편집` 텍스트를 렌더링하지 않는다.
  2. 시스템이 제거 후에도 편집 기능 접근(제목/본문/속성)은 유지한다.
- Alternative / Failure Flow
  - 상단 문구 제거 시 레이아웃 간격이 붕괴하면 헤더 컨테이너 높이를 재정렬한다.
- Success Outcome
  - 화면 상단이 단순해지고 본문 집중도가 높아진다.
- Source Prompt Evidence
  - "노트 편집 이라고 중앙 패널 상단에 써져 있는데, 그 부분 없애."
- Status: confirmed

## UC-03: reference 규칙 기반 노트 특성 자동 분류
- Primary Actor: 노트 편집 사용자
- Goal: 사용자가 관리하는 `docs` 하위 reference 규칙을 근거로 노트 특성을 자동 부여한다.
- Preconditions
  - `docs` 하위에 분류 규칙(reference)이 존재한다.
  - 분류 대상 노트의 입력(제목/본문/메타데이터)이 준비되어 있다.
- Trigger
  - 사용자가 노트를 저장하거나 분류 실행을 요청한다.
- Main Success Flow
  1. 시스템이 reference 규칙을 로드한다.
  2. 시스템이 노트 입력을 규칙과 비교해 특성 후보를 산출한다.
  3. 시스템이 최종 특성을 노트에 반영한다.
- Alternative / Failure Flow
  - reference 문서가 없거나 파싱 불가하면 자동 분류를 건너뛰고 원인을 표시한다.
  - 다중 규칙 충돌 시 우선순위 정책에 따라 단일 결과를 선택하거나 검토 상태로 표시한다.
- Success Outcome
  - 각 노트에 일관된 기준의 특성이 자동 부여된다.
- Source Prompt Evidence
  - "각 노트의 특성은 내가 docs아래에 reference를 만들어서 거기에 넣어 둘게. 그 규칙을 참조해서 각 노트를 자동으로 분류해야 해."
- Status: confirmed

## UC-04: 자동 분류 방식 적합성 판정 (무료 AI 모델 vs 스크립트)
- Primary Actor: 제품/기능 설계 의사결정자(동일 사용자)
- Goal: 운영 비용 없는 분류 방식을 선택할 수 있도록 구현 대안을 판정한다.
- Preconditions
  - 분류 규칙과 분류 대상 데이터 특성이 정의되어 있다.
- Trigger
  - 사용자가 자동 분류 구현 방식을 결정하려 한다.
- Main Success Flow
  1. 시스템/설계가 규칙 복잡도와 결정 가능성을 분석한다.
  2. 무료 AI 모델 사용 시 로컬/호스팅 비용, 정확도, 유지보수 부담을 비교한다.
  3. 스크립트(규칙 엔진/키워드 매칭 등) 방식의 구현/운영 비용을 비교한다.
  4. 현재 요구에 적합한 우선 대안을 제시한다.
- Alternative / Failure Flow
  - 비교 기준이 불명확하면 판정을 보류하고 필요한 기준(정확도 목표, 지연 허용치, 오분류 처리 정책)을 명시한다.
- Success Outcome
  - 무료 AI 모델 또는 스크립트 기반 중 우선 도입 전략이 확정된다.
- Source Prompt Evidence
  - "이걸 하기 위한 무료 ai 모델이나 아니면 스크립트로 처리할 수 있는지 판단해야 해."
- Status: confirmed

# Coverage Mapping
- Prompt Requirement -> Mapped Use Case IDs
  - 좌측 리스트 패널 공간 확보(패딩 축소/레이어 축소): UC-01
  - 중앙 상단 `노트 편집` 문구 제거: UC-02
  - docs/reference 규칙 기반 자동 분류: UC-03
  - 무료 AI 모델 vs 스크립트 방식 판단: UC-04
- Coverage Gaps
  - `docs/reference`의 구체 포맷(예: Markdown 표, YAML, JSON, 자연어 규칙 문장)의 표준이 아직 확정되지 않았다.
  - 규칙 충돌 시 tie-breaker(우선순위/다중 라벨 허용 여부) 정책이 아직 명시되지 않았다.

# Coverage Gate
- Ready for Event Storming: PARTIAL
- Why
  - 핵심 요구는 모두 confirmed use case로 매핑되었으나, 자동 분류 구현 착수에 필요한 규칙 문서 포맷과 충돌 해소 정책이 비어 있어 설계 입력이 완결되지 않았다.
- Blocking Conditions
  - reference 문서 스키마 미정
  - 분류 충돌 처리 정책 미정

# Blocking Unknowns
- `docs` 하위 reference 문서의 canonical 스키마(파서 구현 기준)
- 단일 특성 강제인지 복수 특성 허용인지
- 분류 실패 시 사용자 피드백 UX(자동 재시도/수동 선택/검토 큐)

# Needs Review
- 무료 AI 모델을 사용할 경우 완전 무료 범위를 로컬 실행으로 제한할지, 무료 티어 외부 API까지 허용할지 정책 확인 필요
- 규칙 기반 스크립트와 AI 보조 분류의 하이브리드 허용 여부 확인 필요

# Rejected Use Cases
- 인증/권한 기반 특성 편집 정책
- 노트 특성 분류 결과를 외부 SaaS에 동기화

# Missing-but-Plausible Use Cases
- UC-MP-01: 분류 결과 수동 수정 및 재학습/규칙 보정 피드백 루프
- UC-MP-02: 분류 규칙 변경 시 기존 노트 일괄 재분류 배치 실행

# Next Revision Focus
- `docs/reference` 문서의 최소 파싱 스펙(필드명, 우선순위, 충돌 규칙)을 명시한다.
- 특성 단일/복수 라벨 정책과 저장 포맷을 확정한다.
- 무료 AI 모델 사용 시 허용 런타임(로컬 CPU/GPU)과 지연/정확도 목표를 수치로 설정한다.

# Oracle Handoff
- Allowed To Proceed: NO
- Confirmed Use Cases for Oracle
  - UC-01
  - UC-02
  - UC-03
  - UC-04
- Assumptions Forbidden for Oracle
  - reference 문서 포맷을 임의로 단정하지 말 것
  - 라벨 충돌 처리 정책을 임의로 단정하지 말 것
  - 무료 AI 모델 사용 가능성을 비용/인프라 제약 없이 가정하지 말 것
- User Approval Required Before Orchestration: YES

# Backlinks
- docs/work-units/zetenkastel/note-panel-slimming-and-auto-trait-classification-20260414-1048/index.md
