# Properties
doc_path: docs/use-case-harvests/zetenkastel/note-management-program-20260414-0959/use-case-harvest.md
owner: Codex
status: ready-for-oracle
title: 제텐카스텔 기반 웹 노트 프로그램 UI 편집 경험 개선
domain: zetenkastel
task: note-management-program-20260414-0959
coverage_gate: YES
next_step: wait-for-user-approval
last_updated: 2026-04-14:09:59

# Prompt Interpretation
- User Goal
  - 노트 편집 화면에서 메타 정보가 본문 가독성을 방해하지 않도록 인터페이스를 단순화하고, 본문 중심으로 작업 효율을 높이고 싶다.
- Requested Actions
  - 노트 창 상단의 제목, 노트 타입, 파일명, 태그, 링크가 차지하는 공간을 줄인다.
  - 태그는 입력 후 엔터를 치면 태그 블록(칩)으로 확정되도록 한다.
  - 노트 타입/태그/링크를 제목 아래 `property` 영역으로 묶어 작은 공간에 배치한다.
  - 파일명은 `노트타입/제목` 규칙으로 자동 생성한다.
  - 생성/수정 버튼을 `저장` 1개로 통합하고, 새 파일이면 생성/기존 파일이면 수정으로 동작시킨다.
  - 저장/삭제 버튼을 아이콘 버튼으로 단순화하고 마우스오버 시 설명(tooltip)을 제공한다.
  - 전체 뷰포트 폭을 활용하고 좌/우 패널 폭을 줄여 중앙 본문 영역을 넓힌다.
- Preferred Implementation Stack (language/framework/runtime)
  - Backend: Java 21 + Spring Boot
  - Frontend: React + Vite
- Constraints
  - 빌드/테스트 실패는 무시하지 않는다.
  - 사용자 요청 범위는 UI/편집 경험 개선이며 1인용/인증 없음/로컬 파일 저장소 제약을 유지한다.
- Expected Outcome
  - 노트 편집 화면에서 본문 영역 가시성이 향상되고, 저장 상호작용과 메타데이터 입력 흐름이 간결해진다.
- Explicit Non-goals
  - 인증/인가 기능 추가
  - 백업 기능 추가
  - 저장소를 로컬 파일 외로 변경

# Candidate Use Cases
- UC-01 ~ UC-16을 candidate로 수집 후 confirmed 판정.

# Confirmed Use Cases
## UC-01: 노트 생성
- Primary Actor: 단일 사용자
- Goal: 지정된 노트 타입 하위에 노트를 생성한다.
- Preconditions
  - 웹 애플리케이션이 실행 중이다.
  - 노트 타입 루트 경로가 준비되어 있다.
- Trigger
  - 사용자가 새 노트 편집 상태에서 저장을 실행한다.
- Main Success Flow
  1. 사용자가 제목/본문/속성(property)을 입력한다.
  2. 시스템이 파일명을 규칙에 맞춰 생성한다.
  3. 시스템이 `타입 경로 + 파일명`으로 고유 식별자를 생성/검증한다.
  4. 시스템이 로컬 파일 저장소에 노트를 저장한다.
  5. 시스템이 노트 상세 화면을 갱신한다.
- Alternative / Failure Flow
  - 동일 경로+파일명 충돌 시 저장을 거부하고 제목 변경을 요구한다.
  - 허용되지 않은 노트 타입이면 저장을 거부한다.
  - 로컬 파일 쓰기 실패 시 오류를 표시한다.
- Success Outcome
  - 새 노트가 로컬 파일에 저장되고 조회 가능하다.
- Source Prompt Evidence
  - "모든 노트 타입 노트 CRUD", "고유 식별 규칙은 경로 + 파일명", "저장소는 로컬 파일", "생성, 수정은 저장 1개 버튼으로 합쳐서 새로운 파일일 경우 생성"
- Status: confirmed

## UC-02: 노트 조회/열람
- Primary Actor: 단일 사용자
- Goal: 노트를 열람한다.
- Preconditions
  - 최소 1개 이상의 노트가 존재한다.
- Trigger
  - 사용자가 목록/검색/링크를 통해 특정 노트를 선택한다.
- Main Success Flow
  1. 시스템이 해당 노트를 로컬 파일에서 읽는다.
  2. 시스템이 제목/태그/본문/링크 정보를 렌더링한다.
- Alternative / Failure Flow
  - 파일이 삭제/손상된 경우 오류를 표시하고 목록으로 복귀시킨다.
- Success Outcome
  - 사용자가 노트 내용을 확인한다.
- Source Prompt Evidence
  - "모든 노트 타입 노트 CRUD", "플랫폼은 웹"
- Status: confirmed

## UC-03: 노트 수정
- Primary Actor: 단일 사용자
- Goal: 기존 노트 내용을 수정한다.
- Preconditions
  - 대상 노트가 존재한다.
- Trigger
  - 사용자가 기존 노트 편집 상태에서 저장을 실행한다.
- Main Success Flow
  1. 시스템이 편집 내용을 검증한다.
  2. 시스템이 동일 식별자(경로+파일명) 기준으로 파일을 갱신한다.
  3. 시스템이 링크/백링크 인덱스를 재계산 또는 갱신한다.
- Alternative / Failure Flow
  - 저장 중 파일 쓰기 실패 시 수정 전 상태를 유지하고 오류를 표시한다.
- Success Outcome
  - 노트가 최신 내용으로 반영된다.
- Source Prompt Evidence
  - "모든 노트 타입 노트 CRUD", "생성, 수정은 저장 1개 버튼으로 합쳐서 기존 파일의 경우 수정"
- Status: confirmed

## UC-04: 노트 삭제
- Primary Actor: 단일 사용자
- Goal: 노트를 삭제한다.
- Preconditions
  - 대상 노트가 존재한다.
- Trigger
  - 사용자가 삭제 아이콘 버튼을 선택하고 삭제를 확정한다.
- Main Success Flow
  1. 시스템이 대상 파일을 삭제한다.
  2. 시스템이 백링크/그래프 인덱스를 정리한다.
  3. 시스템이 목록/그래프에서 해당 노드를 제거한다.
- Alternative / Failure Flow
  - 파일 삭제 실패 시 삭제를 취소하고 오류를 표시한다.
- Success Outcome
  - 노트가 저장소와 화면에서 제거된다.
- Source Prompt Evidence
  - "모든 노트 타입 노트 CRUD", "저장, 삭제 버튼을 아이콘으로 단순화"
- Status: confirmed

## UC-05: 백링크 확인
- Primary Actor: 단일 사용자
- Goal: 특정 노트를 참조하는 다른 노트들을 확인한다.
- Preconditions
  - 링크 인덱스가 생성되어 있다.
- Trigger
  - 사용자가 노트 상세에서 백링크 섹션을 연다.
- Main Success Flow
  1. 시스템이 현재 노트를 대상으로 역참조 목록을 계산/조회한다.
  2. 시스템이 백링크 목록과 개수를 표시한다.
- Alternative / Failure Flow
  - 인덱스가 손상/누락되면 재색인을 수행하고 결과를 재표시한다.
- Success Outcome
  - 사용자가 연결된 문맥 노트를 탐색한다.
- Source Prompt Evidence
  - "링크 정책은 백링크를 사용"
- Status: confirmed

## UC-06: 링크 자동 연결 또는 추천
- Primary Actor: 단일 사용자
- Goal: 관련 노트 링크를 자동 연결 또는 추천받는다.
- Preconditions
  - 추천 계산에 필요한 노트 데이터가 존재한다.
- Trigger
  - 사용자가 노트 저장/열람 시 추천 요청을 수행하거나 자동 추천이 실행된다.
- Main Success Flow
  1. 시스템이 태그/제목/본문 기반 유사도를 계산한다.
  2. 시스템이 후보 링크를 산출한다.
  3. 시스템이 자동 연결 또는 추천 리스트를 제공한다.
- Alternative / Failure Flow
  - 유사도 계산 결과가 없으면 빈 추천 결과를 제공한다.
  - 계산 실패 시 기존 수동 링크만 유지한다.
- Success Outcome
  - 사용자가 관련 노트를 더 빠르게 연결한다.
- Source Prompt Evidence
  - "유사도 판단 등의 별도 알고리즘으로 자동 연결 or 연결 추천"
- Status: confirmed

## UC-07: 검색(태그/제목/내용)
- Primary Actor: 단일 사용자
- Goal: 노트를 조건으로 검색한다.
- Preconditions
  - 검색 가능한 노트 데이터가 존재한다.
- Trigger
  - 사용자가 검색어 또는 태그 필터를 입력한다.
- Main Success Flow
  1. 시스템이 태그/제목/내용 필드에서 검색한다.
  2. 시스템이 정렬된 결과 목록을 제공한다.
  3. 사용자가 결과를 선택해 노트를 연다.
- Alternative / Failure Flow
  - 결과가 없으면 빈 결과 상태를 표시한다.
- Success Outcome
  - 사용자가 원하는 노트를 빠르게 찾는다.
- Source Prompt Evidence
  - "검색 기능은 태그 + 제목 + 내용 검색"
- Status: confirmed

## UC-08: 그래프 뷰 탐색
- Primary Actor: 단일 사용자
- Goal: 노트 간 연결 구조를 그래프로 본다.
- Preconditions
  - 노트와 링크 데이터가 존재한다.
- Trigger
  - 사용자가 그래프 뷰를 연다.
- Main Success Flow
  1. 시스템이 노트를 노드, 링크를 엣지로 구성한다.
  2. 시스템이 그래프를 렌더링한다.
  3. 사용자가 노드를 선택해 해당 노트로 이동한다.
- Alternative / Failure Flow
  - 노트 수가 많아 렌더링 성능이 저하되면 축약/필터 상태로 로드한다.
- Success Outcome
  - 사용자가 지식 구조를 시각적으로 탐색한다.
- Source Prompt Evidence
  - "그래프 뷰까지 해줘"
- Status: confirmed

## UC-09: shadcn/ui 기반 UI 전환
- Primary Actor: 단일 사용자
- Goal: 기존 웹 화면을 `shadcn/ui` 컴포넌트 기반으로 일관성 있게 사용한다.
- Preconditions
  - React + Vite 프론트엔드 빌드 환경이 준비되어 있다.
- Trigger
  - 사용자가 노트 목록/상세/편집/검색/그래프 화면을 연다.
- Main Success Flow
  1. 시스템이 `shadcn/ui` 컴포넌트 체계를 사용해 화면을 렌더링한다.
  2. 시스템이 화면 간 공통 UI 패턴(버튼, 입력, 카드, 패널)을 일관되게 제공한다.
  3. 사용자가 기존 기능(CRUD/검색/링크/그래프)을 동일하게 수행한다.
- Alternative / Failure Flow
  - `shadcn/ui` 컴포넌트 적용 중 일부 화면 누락 시 해당 화면은 기존 UI로 fallback되고 보완 대상에 기록한다.
- Success Outcome
  - 사용자 관점에서 일관된 컴포넌트 기반 UI를 사용한다.
- Source Prompt Evidence
  - "현재 ui를 shadcn을 사용하도록 수정"
- Status: confirmed

## UC-10: 노트 타입별 폴드 탐색
- Primary Actor: 단일 사용자
- Goal: 노트 목록을 타입별 접기/펼치기로 정리해 탐색 효율을 높인다.
- Preconditions
  - 노트가 하나 이상 존재하며 타입 정보가 식별 가능하다.
- Trigger
  - 사용자가 노트 목록/사이드바에서 타입 섹션을 펼치거나 접는다.
- Main Success Flow
  1. 시스템이 노트를 타입별 섹션으로 그룹화해 표시한다.
  2. 사용자가 특정 타입 섹션을 접거나 펼친다.
  3. 시스템이 폴드 상태를 반영한 목록을 즉시 렌더링한다.
- Alternative / Failure Flow
  - 특정 타입에 노트가 없으면 빈 상태로 표시한다.
  - 폴드 상태 저장 실패 시 기본 펼침 상태로 렌더링한다.
- Success Outcome
  - 사용자가 원하는 노트 타입 섹션에 빠르게 접근한다.
- Source Prompt Evidence
  - "노트 종류에 따라 폴드되어 정리되게"
- Status: confirmed

## UC-11: 메타데이터 property 영역 축소 배치
- Primary Actor: 단일 사용자
- Goal: 본문보다 메타정보가 과도하게 공간을 차지하지 않도록 상단 편집 구조를 재배치한다.
- Preconditions
  - 노트 편집 화면이 열려 있다.
- Trigger
  - 사용자가 노트를 생성/편집하기 위해 편집 화면에 진입한다.
- Main Success Flow
  1. 시스템이 제목을 우선 배치한다.
  2. 시스템이 제목 아래 작은 `property` 영역에 노트 타입/태그/링크를 묶어 표시한다.
  3. 시스템이 본문 편집 영역을 더 큰 비중으로 렌더링한다.
- Alternative / Failure Flow
  - 기존 레이아웃과 충돌 시 property 영역을 접힘 상태 또는 최소 높이로 유지한다.
- Success Outcome
  - 사용자가 화면 진입 시 주요 내용(본문)에 즉시 집중할 수 있다.
- Source Prompt Evidence
  - "노트 타입, 태그, 링크는 property로 묶어서 제목 아래에 작은 공간을 차지하게"
- Status: confirmed

## UC-12: 태그 엔터 확정 입력
- Primary Actor: 단일 사용자
- Goal: 태그 입력을 빠르게 수행하고 입력 완료 시 태그 칩으로 확정한다.
- Preconditions
  - 편집 화면에서 태그 입력 필드가 활성화되어 있다.
- Trigger
  - 사용자가 태그 문자열 입력 후 Enter 키를 누른다.
- Main Success Flow
  1. 시스템이 입력값을 검증한다(빈값/중복 처리).
  2. 시스템이 태그를 칩(블록) 형태로 추가한다.
  3. 시스템이 다음 태그 입력을 위해 입력칸을 초기화한다.
- Alternative / Failure Flow
  - 빈 문자열 또는 중복 태그 입력 시 칩을 생성하지 않고 입력 힌트를 표시한다.
- Success Outcome
  - 태그 입력-확정 흐름이 단축되고 시각적으로 명확해진다.
- Source Prompt Evidence
  - "태그는 태그 입력후 엔터 치면 태그 블록이 되도록"
- Status: confirmed

## UC-13: 파일명 자동 생성
- Primary Actor: 단일 사용자
- Goal: 파일명 수동 입력 부담을 줄이고 일관된 규칙으로 파일명을 생성한다.
- Preconditions
  - 사용자가 노트 타입과 제목을 입력했다.
- Trigger
  - 사용자가 저장을 수행하거나 제목/노트타입이 변경된다.
- Main Success Flow
  1. 시스템이 `노트타입/제목` 규칙으로 파일명을 계산한다.
  2. 시스템이 저장 식별자(경로+파일명)에 반영한다.
  3. 시스템이 충돌 여부를 검사한다.
- Alternative / Failure Flow
  - 제목이 비어 있거나 파일명으로 부적합한 문자가 포함되면 정규화하거나 저장을 제한한다.
  - 동일 파일명 충돌 시 사용자에게 제목 수정을 요구한다.
- Success Outcome
  - 파일명이 자동으로 일관되게 생성되어 저장 흐름이 단순해진다.
- Source Prompt Evidence
  - "파일명은 노트타입/제목 으로 자동으로 만들도록 수정"
- Status: confirmed

## UC-14: 단일 저장 버튼 기반 생성/수정 분기
- Primary Actor: 단일 사용자
- Goal: 생성/수정 구분 UI를 단일 저장 행위로 단순화한다.
- Preconditions
  - 편집 중인 노트 컨텍스트(신규/기존)를 시스템이 식별할 수 있다.
- Trigger
  - 사용자가 저장 버튼(아이콘)을 클릭한다.
- Main Success Flow
  1. 시스템이 대상 노트가 신규인지 기존인지 판정한다.
  2. 신규면 생성 플로우를 실행한다.
  3. 기존이면 수정 플로우를 실행한다.
  4. 결과를 동일한 저장 피드백으로 제공한다.
- Alternative / Failure Flow
  - 상태 판정 실패 시 저장을 중단하고 오류 메시지를 노출한다.
- Success Outcome
  - 사용자는 버튼 구분 없이 저장 1회 동작만 기억하면 된다.
- Source Prompt Evidence
  - "생성, 수정은 저장 1개 버튼으로 합쳐서 새로운 파일일 경우 생성, 기존 파일의 경우 수정"
- Status: confirmed

## UC-15: 저장/삭제 아이콘 버튼 + 툴팁
- Primary Actor: 단일 사용자
- Goal: 편집 액션 버튼을 시각적으로 간결하게 유지하면서 의미를 잃지 않는다.
- Preconditions
  - 편집 화면에서 저장/삭제 액션이 노출된다.
- Trigger
  - 사용자가 버튼 영역을 확인하거나 마우스를 올린다.
- Main Success Flow
  1. 시스템이 저장/삭제를 텍스트 버튼이 아닌 아이콘 버튼으로 렌더링한다.
  2. 사용자가 아이콘에 hover 시 툴팁으로 동작 설명을 확인한다.
  3. 사용자가 아이콘 클릭으로 액션을 수행한다.
- Alternative / Failure Flow
  - 툴팁 렌더링 실패 시 접근 가능한 대체 라벨(aria-label 등)을 유지한다.
- Success Outcome
  - 버튼 영역 밀도가 낮아지고 동작 의미가 유지된다.
- Source Prompt Evidence
  - "저장, 삭제 버튼을 아이콘으로 단순화, 마우스오버시 설명 뜨도록"
- Status: confirmed

## UC-16: 본문 중심 3패널 폭 재조정
- Primary Actor: 단일 사용자
- Goal: 가로 공간을 최대 활용해 가운데 본문 영역의 가독성을 높인다.
- Preconditions
  - 좌/중앙/우 패널 레이아웃이 활성화되어 있다.
- Trigger
  - 사용자가 노트 편집/열람 레이아웃을 연다.
- Main Success Flow
  1. 시스템이 전체 레이아웃을 viewport width 기준으로 확장한다.
  2. 시스템이 좌/우 패널 폭을 줄인다.
  3. 시스템이 중앙 본문 패널 폭을 확장한다.
- Alternative / Failure Flow
  - 좁은 화면에서는 최소 사용성 보장을 위해 반응형 임계값에 따라 패널을 축소/숨김 처리한다.
- Success Outcome
  - 사용자가 핵심 본문을 더 넓은 영역에서 편집/열람한다.
- Source Prompt Evidence
  - "전체 vw를 다 쓰도록 하고, 좌, 우측 패널의 너비를 줄이고 중간의 내용 칸의 너비를 넓게 수정"
- Status: confirmed

# Coverage Mapping
- "노트 창의 제목, 노트 타입, 파일명, 태그, 링크가 내용보다 너무 많은 공간을 차지" -> UC-11, UC-16
- "태그 입력후 엔터 치면 태그 블록" -> UC-12
- "노트 타입, 태그, 링크는 property로 묶어서 제목 아래 작은 공간" -> UC-11
- "파일명은 노트타입/제목 자동 생성" -> UC-13
- "생성/수정 저장 1개 버튼으로 통합" -> UC-14, UC-01, UC-03
- "저장/삭제 버튼 아이콘 단순화 + hover 설명" -> UC-15, UC-04
- "전체 vw 사용 + 좌우 패널 축소 + 중앙 확장" -> UC-16
- 기존 기능 요구(CRUD/백링크/추천/검색/그래프/shadcn/타입 폴드) 유지 -> UC-01, UC-02, UC-03, UC-04, UC-05, UC-06, UC-07, UC-08, UC-09, UC-10
- Coverage Gaps
  - 없음

# Coverage Gate
- Ready for Event Storming: YES
- Why
  - 최신 UI 재구성 요구가 독립된 confirmed use case(UC-11~UC-16)로 명확히 분해되었다.
  - 생성/수정/삭제/레이아웃/입력 상호작용의 실패/예외 흐름이 정의되었다.
  - 외부 시스템 의존(로컬 파일 저장소)과 기존 제약(1인용/인증 없음)이 유지되었다.
  - 범위(편집 UI 개선)와 제외 범위(인증/백업/저장소 변경)가 분리되었다.
  - Blocking Unknowns가 이벤트 스토밍 착수를 막지 않는다.
- Blocking Conditions
  - 없음

# Blocking Unknowns
- 없음

# Needs Review
- 기존 work-unit은 `completed` 상태였으나, 사용자 신규 UI 요구 반영을 위해 동일 identity에서 재활성화했다.
- 파일명 자동 생성 규칙 적용 시 기존 수동 파일명 노트와의 충돌/마이그레이션 정책은 구현 단계에서 명시 결정이 필요하다.

# Rejected Use Cases
- 다중 사용자 협업/권한 관리 (프롬프트에 없음)
- 계정 인증/인가 (명시적 제외)
- 백업/동기화 파이프라인 (명시적 제외)
- 저장소를 클라우드/DB로 변경 (프롬프트에 없음)

# Missing-but-Plausible Use Cases
- 아이콘 버튼에 대한 키보드 단축키 지정
- 태그 자동완성/추천
- 패널 폭 사용자 커스터마이징 저장

# Next Revision Focus
- 사용자 승인 후 oracle 단계에서 편집 화면 정보 구조(heading/property/content/actions)와 반응형 브레이크포인트 정책을 이벤트 중심으로 상세화한다.

# Oracle Handoff
- Allowed To Proceed: YES
- Confirmed Use Cases for Oracle
  - UC-01: 노트 생성
  - UC-02: 노트 조회/열람
  - UC-03: 노트 수정
  - UC-04: 노트 삭제
  - UC-05: 백링크 확인
  - UC-06: 링크 자동 연결 또는 추천
  - UC-07: 검색(태그/제목/내용)
  - UC-08: 그래프 뷰 탐색
  - UC-09: shadcn/ui 기반 UI 전환
  - UC-10: 노트 타입별 폴드 탐색
  - UC-11: 메타데이터 property 영역 축소 배치
  - UC-12: 태그 엔터 확정 입력
  - UC-13: 파일명 자동 생성
  - UC-14: 단일 저장 버튼 기반 생성/수정 분기
  - UC-15: 저장/삭제 아이콘 버튼 + 툴팁
  - UC-16: 본문 중심 3패널 폭 재조정
- Assumptions Forbidden for Oracle
  - 저장소를 로컬 파일 외 다른 저장소로 확장하지 않는다.
  - 인증/백업 기능을 범위에 임의 추가하지 않는다.
  - Frontend 스택을 React + Vite 이외로 임의 변경하지 않는다.
  - `property` 영역 축소 요구를 단순 숨김 처리로 대체하지 않는다.
  - 생성/수정 통합 저장 요구를 별도 버튼 복원으로 대체하지 않는다.
- User Approval Required Before Orchestration: YES

# Backlinks
- docs/work-units/zetenkastel/note-management-program-20260414-0959/index.md
