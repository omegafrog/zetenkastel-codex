# Properties
doc_path: docs/use-case-harvests/zetenkastel/note-management-program/use-case-harvest.md
owner: Codex
status: ready-for-oracle
title: 제텐카스텔 기반 웹 노트 프로그램
domain: zetenkastel
task: note-management-program
coverage_gate: YES
next_step: wait-for-user-approval
last_updated: 2026-04-14:08:59

# Prompt Interpretation
- User Goal
  - 제텐카스텔 방법론에 맞는 1인용 웹 노트 프로그램을 만들고, 핵심 노트 작업/연결/탐색을 완성하고 싶다.
- Requested Actions
  - 노트 타입을 `inbox`, `fleeting-notes`, `literature-notes`, `projects`, `area`, `archives`, `maps-of-content`, `references`, `templates`, `attachments` 로 지원한다.
  - 모든 노트 타입에 대해 CRUD를 제공한다.
  - 고유 식별 규칙을 `경로 + 파일명`으로 사용한다.
  - 백링크를 제공한다.
  - 링크 자동 연결 또는 연결 추천 기능(유사도 등 별도 알고리즘 기반)을 제공한다.
  - 저장소는 로컬 파일만 사용한다.
  - 검색은 태그 + 제목 + 내용 기준으로 지원한다.
  - 그래프 뷰를 제공한다.
  - 우선 플랫폼은 웹으로 한다.
  - 현재 UI를 `shadcn/ui` 기반으로 정비한다.
  - 노트 목록/탐색 화면에서 노트 종류별로 폴드(접기/펼치기) 정리한다.
- Preferred Implementation Stack (language/framework/runtime)
  - Backend: Java 21 + Spring Boot
  - Frontend: React + Vite (사용자 확정)
- Constraints
  - 사용자 범위는 1인용이며 인증은 없다.
  - 백업은 현재 범위에서 제외한다.
  - 외부 클라우드/원격 저장소 요구는 없다.
  - `shadcn/ui`는 React + Vite 프론트엔드에 적용한다.
- Expected Outcome
  - 웹에서 노트를 생성/수정/삭제/조회하고, 노트 간 연결 상태(백링크/추천/그래프)와 검색 결과를 사용할 수 있다.
  - UI가 `shadcn/ui` 컴포넌트 기반으로 일관되게 제공되고, 노트 타입별 폴드 탐색이 가능하다.
- Explicit Non-goals
  - 인증/인가 기능
  - 백업 기능

# Candidate Use Cases
- UC-01 ~ UC-10을 candidate로 수집 후 confirmed 판정.

# Confirmed Use Cases
## UC-01: 노트 생성
- Primary Actor: 단일 사용자
- Goal: 지정된 노트 타입 하위에 노트를 생성한다.
- Preconditions
  - 웹 애플리케이션이 실행 중이다.
  - 노트 타입 루트 경로가 준비되어 있다.
- Trigger
  - 사용자가 "새 노트"를 선택하고 노트 타입/파일명/내용(및 태그)을 입력한다.
- Main Success Flow
  1. 사용자가 노트 타입을 선택한다.
  2. 사용자가 파일명과 본문/메타데이터를 입력한다.
  3. 시스템이 `타입 경로 + 파일명`으로 고유 식별자를 생성/검증한다.
  4. 시스템이 로컬 파일 저장소에 노트를 저장한다.
  5. 시스템이 노트 상세 화면을 갱신한다.
- Alternative / Failure Flow
  - 동일 경로+파일명 충돌 시 저장을 거부하고 파일명 변경을 요구한다.
  - 허용되지 않은 노트 타입이면 저장을 거부한다.
  - 로컬 파일 쓰기 실패 시 오류를 표시한다.
- Success Outcome
  - 새 노트가 로컬 파일에 저장되고 조회 가능하다.
- Source Prompt Evidence
  - "모든 노트 타입 노트 CRUD", "고유 식별 규칙은 경로 + 파일명", "저장소는 로컬 파일"
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
  - 사용자가 노트 편집 후 저장한다.
- Main Success Flow
  1. 시스템이 편집 내용을 검증한다.
  2. 시스템이 동일 식별자(경로+파일명) 기준으로 파일을 갱신한다.
  3. 시스템이 링크/백링크 인덱스를 재계산 또는 갱신한다.
- Alternative / Failure Flow
  - 저장 중 파일 쓰기 실패 시 수정 전 상태를 유지하고 오류를 표시한다.
- Success Outcome
  - 노트가 최신 내용으로 반영된다.
- Source Prompt Evidence
  - "모든 노트 타입 노트 CRUD", "백링크"
- Status: confirmed

## UC-04: 노트 삭제
- Primary Actor: 단일 사용자
- Goal: 노트를 삭제한다.
- Preconditions
  - 대상 노트가 존재한다.
- Trigger
  - 사용자가 삭제를 확정한다.
- Main Success Flow
  1. 시스템이 대상 파일을 삭제한다.
  2. 시스템이 백링크/그래프 인덱스를 정리한다.
  3. 시스템이 목록/그래프에서 해당 노드를 제거한다.
- Alternative / Failure Flow
  - 파일 삭제 실패 시 삭제를 취소하고 오류를 표시한다.
- Success Outcome
  - 노트가 저장소와 화면에서 제거된다.
- Source Prompt Evidence
  - "모든 노트 타입 노트 CRUD", "백링크", "그래프 뷰"
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

# Coverage Mapping
- "제텐카스텔 기반 노트 프로그램" -> UC-01, UC-02, UC-03, UC-04, UC-05, UC-06, UC-07, UC-08
- "노트 타입 inbox/fleeting notes/literature notes/projects/area/archives/maps-of-content/references/templates/attachments" -> UC-01, UC-02, UC-03, UC-04
- "고유 식별 규칙은 경로 + 파일명" -> UC-01, UC-03
- "링크 정책은 백링크" -> UC-05
- "유사도 기반 자동 연결 or 연결 추천" -> UC-06
- "저장소는 로컬 파일" -> UC-01, UC-02, UC-03, UC-04
- "검색 기능은 태그 + 제목 + 내용" -> UC-07
- "사용자 범위 1인용, 인증 없음" -> UC-01, UC-02, UC-03, UC-04, UC-05, UC-06, UC-07, UC-08
- "플랫폼은 웹" -> UC-01, UC-02, UC-03, UC-04, UC-05, UC-06, UC-07, UC-08
- "백업 불필요" -> 명시적 제외 범위(Non-goal)
- "모든 노트 타입 CRUD + 백링크 + 그래프 뷰" -> UC-01, UC-02, UC-03, UC-04, UC-05, UC-08
- "현재 UI를 shadcn 사용으로 수정" -> UC-09
- "노트 종류에 따라 폴드되어 정리" -> UC-10
- Coverage Gaps
  - 없음 (기존 기능 요구 + UI 전환/폴드 탐색 요구 모두 confirmed use case로 매핑됨)

# Coverage Gate
- Ready for Event Storming: YES
- Why
  - 핵심 사용자 목표와 기능 범위가 confirmed use case로 커버되었다.
  - 실패/예외 흐름, 외부 시스템(로컬 파일 시스템), 범위/제외 범위 구분이 반영되었다.
  - 기본 구현 스택(`Java + Spring Boot`, `React + Vite`)이 사용자 승인으로 확정되었다.
  - 링크 정책은 \"자동 연결 또는 연결 추천\" 요구를 충족하는 범위로 설계 가능하다.
  - UI 전환(`shadcn/ui`)과 타입별 폴드 탐색 요구가 별도 confirmed use case로 반영되었다.
- Blocking Conditions
  - 없음

# Blocking Unknowns
- 없음

# Needs Review
- 기존 work-unit은 `completed` 상태였으나, 사용자의 신규/확장 요청으로 동일 identity에서 재활성화가 필요함.
- 기존 정적 페이지(`src/main/resources/static/*.html`)와 신규 React + Vite UI의 공존/전환 전략은 구현 단계에서 결정 필요.

# Rejected Use Cases
- 다중 사용자 협업/권한 관리 (프롬프트에 없음)
- 계정 인증/인가 (명시적 제외)
- 백업/동기화 파이프라인 (명시적 제외)

# Missing-but-Plausible Use Cases
- 대량 노트에서 그래프 필터링/클러스터링 고도화
- 링크 추천 결과에 대한 사용자 피드백(수락/거절) 학습
- 폴드 상태의 세션 간 유지(local storage 등)

# Next Revision Focus
- 사용자 승인 후 오케스트레이션(`oracle`) 진행 여부를 결정한다.

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
- Assumptions Forbidden for Oracle
  - 저장소를 로컬 파일 외 다른 저장소로 확장하지 않는다.
  - 인증/백업 기능을 범위에 임의 추가하지 않는다.
  - Frontend 스택을 React + Vite 이외로 임의 변경하지 않는다.
  - 타입별 폴드 요구를 단순 정렬/필터로 대체하지 않는다.
- User Approval Required Before Orchestration: YES

# Backlinks
- docs/work-units/zetenkastel/note-management-program/index.md
