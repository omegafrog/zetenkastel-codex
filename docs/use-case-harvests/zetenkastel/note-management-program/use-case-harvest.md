# Properties
doc_path: docs/use-case-harvests/zetenkastel/note-management-program/use-case-harvest.md
owner: Codex
status: ready-for-oracle
title: 제텐카스텔 기반 노트 관리 프로그램 (웹)
domain: zetenkastel
task: note-management-program
coverage_gate: YES
next_step: wait-for-user-approval
last_updated: 2026-04-13:22:16

# Prompt Interpretation
- User Goal: 제텐카스텔 기반 노트 프로그램을 웹 플랫폼으로 구현하고, plain JavaScript UI에서 지정 노트 타입 CRUD/백링크/그래프 뷰를 사용할 수 있게 한다.
- Requested Actions: 노트 타입(inbox/fleeting notes/literature notes/projects/area/archives/maps-of-content/references/templates/attachments) 확정, 경로+파일명 식별 규칙 적용, 백링크 기반 링크 정책, 자동 연결 또는 연결 추천(유사도 기반), 로컬 파일 저장소, 태그+제목+내용 검색, 1인용/인증 없음, MVP 범위(모든 타입 CRUD + 백링크 + 그래프 뷰) 확정, plain JavaScript 기반 웹 UI 작성.
- Preferred Implementation Stack (language/framework/runtime): Frontend는 plain JavaScript, Backend는 Spring Boot.
- Constraints: 저장소는 로컬 파일만 사용한다. 사용자 범위는 1인용이며 인증은 없다. 백업은 현재 범위에서 제외한다.
- Expected Outcome: 설계 입력 가능한 유스케이스 문서로 정리되어, plain JavaScript UI를 포함한 웹 MVP 구현 범위와 제외 범위가 명확해진다.
- Explicit Non-goals: 다중 사용자 협업, 인증/인가, 클라우드 저장소, 백업 기능.

# Candidate Use Cases
- UC-CAND-001: 유사도 기반 링크 자동 연결/연결 추천
  - Primary Actor: 사용자
  - Goal: 노트 작성 시 시스템이 관련 노트를 자동 연결하거나 연결 후보를 추천해 링크 작업을 보조한다.
  - Source Prompt Evidence: "링크를 유사도 판단 등의 별도 알고리즘으로 자동 연결 or 연결 추천"
  - Status: candidate

# Confirmed Use Cases
- UC-CONF-001: 모든 노트 타입 CRUD
  - Name: 노트 타입별 생성/조회/수정/삭제
  - Primary Actor: 사용자
  - Goal: 지정된 모든 노트 타입에서 노트를 CRUD한다.
  - Preconditions: 웹 앱이 실행 중이고 로컬 저장 경로가 접근 가능하다.
  - Trigger: 사용자가 특정 노트 타입에서 노트 작업을 시작한다.
  - Main Success Flow:
    1. 사용자가 노트 타입을 선택한다.
    2. 사용자가 노트를 생성/조회/수정/삭제한다.
    3. 시스템이 로컬 파일에 변경 사항을 반영한다.
  - Alternative / Failure Flow:
    1. 파일 쓰기 실패 시 시스템은 원인을 표시하고 재시도/취소를 제공한다.
    2. 대상 파일 누락/손상 시 시스템은 오류를 표시하고 복구 가능한 동작을 안내한다.
  - Success Outcome: 모든 지정 노트 타입에서 CRUD가 일관되게 동작한다.
  - Source Prompt Evidence: "일단 모든 노트 타입 노트 CRUD"
  - Status: confirmed

- UC-CONF-002: 경로+파일명 기반 식별
  - Name: 경로 기반 고유 식별 및 재조회
  - Primary Actor: 사용자
  - Goal: 노트를 경로 + 파일명으로 고유 식별하고 안정적으로 조회한다.
  - Preconditions: 노트가 로컬 파일 구조에 존재한다.
  - Trigger: 사용자가 노트를 생성/열기/이동/이름변경한다.
  - Main Success Flow:
    1. 시스템이 상대 경로와 파일명을 식별키로 사용한다.
    2. 사용자가 식별키 기준으로 노트를 재조회한다.
  - Alternative / Failure Flow:
    1. 경로 충돌 시 시스템은 충돌을 표시하고 대체 경로/이름 선택을 유도한다.
    2. 이동/이름변경 후 링크 불일치 발생 시 시스템은 링크 갱신 또는 무효 링크 표시를 수행한다.
  - Success Outcome: 노트 식별과 접근이 경로+파일명 기준으로 유지된다.
  - Source Prompt Evidence: "고유 식별 규칙은 경로 + 파일명"
  - Status: confirmed

- UC-CONF-003: 백링크 생성/조회
  - Name: 백링크 기반 연결 탐색
  - Primary Actor: 사용자
  - Goal: 노트 간 링크를 생성하고 백링크로 역참조를 확인한다.
  - Preconditions: 최소 2개 이상의 노트가 존재한다.
  - Trigger: 사용자가 노트 링크를 추가하거나 노트를 열람한다.
  - Main Success Flow:
    1. 사용자가 노트 A에서 노트 B로 링크를 추가한다.
    2. 시스템이 노트 B 백링크에 노트 A를 반영한다.
    3. 사용자가 백링크를 통해 관련 노트로 이동한다.
  - Alternative / Failure Flow:
    1. 링크 대상이 존재하지 않으면 시스템이 깨진 링크로 표시한다.
    2. 노트 삭제/이동으로 백링크 무결성이 깨지면 시스템이 갱신 또는 무효 상태를 표시한다.
  - Success Outcome: 사용자가 백링크를 통해 연결 맥락을 탐색한다.
  - Source Prompt Evidence: "링크 정책은 백링크를 사용"
  - Status: confirmed

- UC-CONF-004: 태그/제목/내용 검색 + 그래프 탐색
  - Name: 검색과 그래프 뷰 기반 탐색
  - Primary Actor: 사용자
  - Goal: 태그/제목/내용 검색과 그래프 뷰를 통해 관련 노트를 찾고 관계를 파악한다.
  - Preconditions: 노트와 링크 데이터가 존재한다.
  - Trigger: 사용자가 노트 탐색 또는 관계 확인을 수행한다.
  - Main Success Flow:
    1. 사용자가 검색 조건(태그, 제목, 내용)을 입력한다.
    2. 시스템이 일치 노트를 반환한다.
    3. 사용자가 그래프 뷰에서 노트 관계를 확인하고 노트로 이동한다.
  - Alternative / Failure Flow:
    1. 검색 결과가 없으면 시스템은 빈 결과와 조건 조정 힌트를 제공한다.
    2. 그래프 렌더링 실패 시 시스템은 목록/백링크 탐색으로 폴백한다.
  - Success Outcome: 사용자가 텍스트/태그 검색과 시각화 탐색을 함께 수행할 수 있다.
  - Source Prompt Evidence: "검색 기능은 태그 + 제목 + 내용 검색", "그래프 뷰"
  - Status: confirmed

- UC-CONF-005: 1인용 웹 로컬 운영
  - Name: 인증 없는 싱글 유저 웹 사용
  - Primary Actor: 사용자
  - Goal: 인증 없이 단일 사용자가 웹 앱으로 로컬 파일 기반 기능을 사용한다.
  - Preconditions: 사용자 로컬 환경에서 웹 앱 실행과 파일 접근이 가능하다.
  - Trigger: 사용자가 웹 앱을 실행한다.
  - Main Success Flow:
    1. 사용자가 인증 절차 없이 웹 앱을 연다.
    2. 앱이 로컬 저장소를 로드한다.
    3. 사용자가 단일 사용자 컨텍스트에서 기능을 수행한다.
  - Alternative / Failure Flow:
    1. 로컬 경로 접근 권한이 없으면 시스템이 권한/경로 재설정 절차를 안내한다.
  - Success Outcome: 인증 없는 1인용 웹 사용 흐름이 성립한다.
  - Source Prompt Evidence: "사용자 범위는 1인용. 인증 없음", "우선 플랫폼은 웹", "저장소는 로컬 파일으로만"
  - Status: confirmed

- UC-CONF-006: Plain JavaScript UI 상호작용
  - Name: 웹 UI에서 노트 작업 수행
  - Primary Actor: 사용자
  - Goal: plain JavaScript UI에서 노트 타입 선택, 노트 편집, 검색, 백링크 확인, 그래프 탐색을 수행한다.
  - Preconditions: 웹 앱이 실행 중이고 API 및 로컬 저장소가 접근 가능하다.
  - Trigger: 사용자가 브라우저 UI에서 노트 관리 작업을 시작한다.
  - Main Success Flow:
    1. 사용자가 UI에서 노트 타입을 선택한다.
    2. 사용자가 생성/수정/삭제/검색/백링크 조회/그래프 탐색을 수행한다.
    3. 시스템이 API 응답을 반영해 UI 상태를 갱신한다.
  - Alternative / Failure Flow:
    1. API 호출 실패 시 시스템은 오류 메시지를 표시하고 재시도 경로를 제공한다.
    2. UI 렌더링 실패 시 시스템은 기능별 최소 대체 표시(목록/텍스트 결과)를 제공한다.
  - Success Outcome: 사용자가 plain JavaScript UI만으로 MVP 핵심 기능을 수행할 수 있다.
  - Source Prompt Evidence: "이제 UI 작성해줘. plain js로"
  - Status: confirmed

# Coverage Mapping
- Prompt Requirement -> Mapped Use Case IDs
- "노트 타입은 inbox / fleeting notes/ literature notes/projects/area/archives/maps-of-content/references/templates/attachments" -> UC-CONF-001
- "고유 식별 규칙은 경로 + 파일명" -> UC-CONF-002
- "링크 정책은 백링크" -> UC-CONF-003
- "유사도 기반 자동 연결 or 연결 추천" -> UC-CAND-001
- "저장소는 로컬 파일으로만" -> UC-CONF-001, UC-CONF-005
- "검색 기능은 태그 + 제목 + 내용 검색" -> UC-CONF-004
- "사용자 범위는 1인용. 인증 없음" -> UC-CONF-005
- "우선 플랫폼은 웹" -> UC-CONF-005
- "백업은 일단 불필요" -> UC-CONF-005 (범위 제외 정책)
- "모든 노트 타입 CRUD + 백링크 + 그래프 뷰" -> UC-CONF-001, UC-CONF-003, UC-CONF-004
- "이제 UI 작성해줘. plain js로" -> UC-CONF-006
- Coverage Gaps
- 자동 연결/추천의 동작 정책(자동 반영 vs 제안 후 승인)이 미확정이다.

# Coverage Gate
- Ready for Event Storming: YES
- Why
- 사용자 목표와 핵심 기능 요구가 confirmed use case로 반영되었다.
- 기본 구현 스택(plain JavaScript + Spring Boot)이 확정되어 기술 경계가 고정되었다.
- 자동 연결/추천은 "있으면 좋겠어" 수준의 선택 요구(candidate)로 분리되어 MVP 필수 범위를 방해하지 않는다.
- Blocking Conditions
- 없음

# Blocking Unknowns
- 없음

# Needs Review
- UC-CAND-001을 MVP 필수로 승격할지(자동 연결) 또는 권장 기능(연결 추천)으로 한정할지 결정 필요.
- 웹에서 "로컬 파일 저장소만 사용"을 어떤 실행 모델(예: 브라우저 단독 vs 로컬 브리지 포함)로 충족할지 결정 필요.

# Rejected Use Cases
- UC-REJ-001: 다중 사용자 협업 편집
  - Reason: 사용자 범위가 1인용으로 명시됨.
- UC-REJ-002: 인증/인가 플로우
  - Reason: 인증 없음으로 명시됨.
- UC-REJ-003: 백업/클라우드 동기화
  - Reason: 백업 불필요, 저장소는 로컬 파일만 사용으로 명시됨.

# Missing-but-Plausible Use Cases
- UC-MBP-001: 첨부 파일 미리보기/열기
  - Reason: `attachments` 타입이 명시되어 있어 조회 UX가 필요할 가능성이 높으나, 현재 프롬프트에는 직접 요구가 없다.

# Next Revision Focus
- UC-CAND-001(자동 연결/추천)의 제품 정책(자동 반영 여부)을 확정한다.
- 웹 플랫폼에서 로컬 파일 접근 모델을 확정한다.

# Oracle Handoff
- Allowed To Proceed: YES
- Confirmed Use Cases for Oracle
- UC-CONF-001
- UC-CONF-002
- UC-CONF-003
- UC-CONF-004
- UC-CONF-005
- UC-CONF-006
- Assumptions Forbidden for Oracle
- 노트 타입 목록을 임의 변경/축소하지 않는다.
- 식별 규칙(경로+파일명)을 UUID 등으로 치환하지 않는다.
- 저장소를 DB/클라우드로 변경 가정하지 않는다.
- 기본 구현 스택을 plain JavaScript + Spring Boot 외로 임의 변경 가정하지 않는다.
- 인증/다중사용자/백업 기능을 범위에 추가하지 않는다.
- 자동 연결/추천을 사용자 확인 없이 완전 자동으로 고정 가정하지 않는다.
- User Approval Required Before Orchestration: YES
