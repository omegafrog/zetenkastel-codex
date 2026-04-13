# Properties
doc_path: docs/use-case-harvests/zetenkastel/note-management-program/use-case-harvest.md
owner: Codex
status: ready-for-oracle
title: 제텐카스텔 기반 노트 관리 프로그램
domain: zetenkastel
task: note-management-program
coverage_gate: YES
next_step: wait-for-user-approval
last_updated: 2026-04-13:21:38

# Prompt Interpretation
- User Goal: 제텐카스텔 기반 노트 관리 프로그램을 만들고, 지정한 노트 타입 체계와 연결/탐색 기능을 포함한 데스크톱 MVP를 완성한다.
- Requested Actions: 노트 타입 정의, 경로+파일명 기반 식별, 백링크 및 자동 연결/추천, 로컬 파일 저장소, 검색+태그+백링크+그래프 뷰, 1인용 데스크톱 범위, 핵심 MVP 기능(CRUD+백링크+그래프)을 유스케이스로 확정한다.
- Constraints: 저장소는 로컬 파일만 사용한다. 사용자 범위는 1인용이며 인증을 두지 않는다. 백업은 이번 범위에서 제외한다.
- Expected Outcome: 모든 노트 타입에 대한 CRUD, 백링크, 그래프 뷰를 포함하는 제텐카스텔 노트 관리 MVP 요구가 설계 입력 가능 수준으로 정리된다.
- Explicit Non-goals: 다중 사용자 협업, 인증/인가, 클라우드 동기화, 백업 기능.

# Candidate Use Cases
- UC-CAND-001: 링크 자동 연결/추천 보조
  - Primary Actor: 사용자
  - Goal: 시스템이 유사도 기반으로 링크 자동 연결 또는 연결 추천을 제공해 연결 작업을 줄인다.
  - Source Prompt Evidence: "링크를 유사도 판단 등의 별도 알고리즘으로 자동 연결 or 연결 추천 해주면 좋겠어."
  - Status: candidate

# Confirmed Use Cases
- UC-CONF-001: 노트 타입별 CRUD 수행
  - Name: 모든 노트 타입 CRUD
  - Primary Actor: 사용자
  - Goal: 지정된 노트 타입(inbox/fleeting notes/literature notes/projects/area/archives/maps-of-content/references/templates/attachments)에 대해 생성/조회/수정/삭제를 수행한다.
  - Preconditions: 데스크톱 앱이 실행 중이며 로컬 저장 경로가 접근 가능하다.
  - Trigger: 사용자가 특정 노트 타입에서 노트 작업을 시작한다.
  - Main Success Flow:
    1. 사용자가 노트 타입을 선택한다.
    2. 사용자가 노트를 생성/조회/수정/삭제한다.
    3. 시스템이 로컬 파일에 변경사항을 반영한다.
  - Alternative / Failure Flow:
    1. 파일 쓰기 실패 시 시스템은 실패 원인을 표시하고 재시도/취소를 제공한다.
    2. 대상 파일 누락/손상 시 시스템은 오류를 표시하고 복구 가능한 동작(새 파일 생성 또는 참조 갱신)을 안내한다.
  - Success Outcome: 모든 지정 노트 타입에서 CRUD가 일관되게 동작한다.
  - Source Prompt Evidence: "모든 노트 타입 노트 CRUD"
  - Status: confirmed

- UC-CONF-002: 경로+파일명 기반 식별 및 조회
  - Name: 파일 경로 기반 노트 식별
  - Primary Actor: 사용자
  - Goal: 노트를 경로 + 파일명으로 고유하게 식별하고 관리한다.
  - Preconditions: 노트가 로컬 파일 구조 내에 존재한다.
  - Trigger: 사용자가 노트를 생성/열기/이동/이름변경한다.
  - Main Success Flow:
    1. 시스템이 노트의 상대 경로와 파일명을 식별키로 사용한다.
    2. 사용자가 해당 식별키 기준으로 노트를 재조회한다.
  - Alternative / Failure Flow:
    1. 경로 충돌 시 시스템은 충돌을 표시하고 이름 변경 또는 위치 변경을 유도한다.
    2. 이동/이름변경 후 참조 불일치가 발생하면 시스템이 링크 갱신 또는 무효 링크 표시를 수행한다.
  - Success Outcome: 노트 식별과 접근이 경로+파일명 기준으로 안정적으로 유지된다.
  - Source Prompt Evidence: "고유 식별 규칙은 경로 + 파일명으로 해"
  - Status: confirmed

- UC-CONF-003: 백링크 생성/조회
  - Name: 백링크 중심 연결 탐색
  - Primary Actor: 사용자
  - Goal: 노트 간 링크를 생성하고 백링크로 역참조를 확인한다.
  - Preconditions: 최소 2개 이상의 노트가 존재한다.
  - Trigger: 사용자가 노트 간 링크를 생성하거나 노트를 열람한다.
  - Main Success Flow:
    1. 사용자가 노트 A에서 노트 B로 링크를 추가한다.
    2. 시스템이 노트 B의 백링크 목록에 노트 A를 반영한다.
    3. 사용자가 백링크를 통해 관련 노트로 이동한다.
  - Alternative / Failure Flow:
    1. 링크 대상이 존재하지 않으면 시스템이 깨진 링크로 표시한다.
    2. 노트 삭제/이동으로 백링크 무결성이 깨지면 시스템이 백링크를 갱신하거나 무효 상태를 표시한다.
  - Success Outcome: 사용자는 백링크를 통해 연결 맥락을 탐색할 수 있다.
  - Source Prompt Evidence: "링크 정책은 백링크를 사용"
  - Status: confirmed

- UC-CONF-004: 검색/태그/백링크/그래프 기반 탐색
  - Name: 고급 탐색과 그래프 뷰
  - Primary Actor: 사용자
  - Goal: 제목/본문 검색, 태그 필터, 백링크 조회, 그래프 뷰를 통해 지식을 탐색한다.
  - Preconditions: 노트와 링크 데이터가 축적되어 있다.
  - Trigger: 사용자가 노트를 찾거나 관계를 시각적으로 확인하려고 한다.
  - Main Success Flow:
    1. 사용자가 검색어(제목/본문) 또는 태그 필터를 입력한다.
    2. 시스템이 일치 노트를 반환한다.
    3. 사용자가 백링크와 그래프 뷰에서 관계를 확인하고 노트로 이동한다.
  - Alternative / Failure Flow:
    1. 검색 결과가 없으면 시스템은 빈 결과와 검색 조건 조정 안내를 제공한다.
    2. 그래프 렌더링 실패 시 시스템은 리스트 기반 대체 탐색(검색/백링크)으로 폴백한다.
  - Success Outcome: 사용자는 다차원 탐색으로 관련 노트를 빠르게 찾고 연결 구조를 파악한다.
  - Source Prompt Evidence: "검색 기능은 D", "백링크 + 그래프 뷰"
  - Status: confirmed

- UC-CONF-005: 1인용 데스크톱 로컬 운영
  - Name: 싱글 유저 데스크톱 운영
  - Primary Actor: 사용자
  - Goal: 인증 없이 단일 사용자가 데스크톱 환경에서 로컬 파일 기반으로 앱을 사용한다.
  - Preconditions: 사용자 로컬 머신에서 앱 실행 가능하다.
  - Trigger: 앱 실행 및 노트 관리 시작.
  - Main Success Flow:
    1. 사용자가 인증 절차 없이 앱을 연다.
    2. 앱이 로컬 저장소를 로드한다.
    3. 사용자가 모든 기능을 단일 사용자 컨텍스트에서 수행한다.
  - Alternative / Failure Flow:
    1. 로컬 경로 접근 권한이 없으면 시스템이 경로 재설정/권한 확인을 안내한다.
  - Success Outcome: 인증 없는 1인용 데스크톱 사용 흐름이 안정적으로 동작한다.
  - Source Prompt Evidence: "사용자 범위는 1인용. 인증 없음", "우선 플랫폼은 데스크톱", "저장소는 로컬 파일으로만"
  - Status: confirmed

# Coverage Mapping
- Prompt Requirement -> Mapped Use Case IDs
- "노트 타입은 inbox / fleeting notes/ literature notes/projects/area/archives/maps-of-content/references/templates/attachments" -> UC-CONF-001
- "고유 식별 규칙은 경로 + 파일명" -> UC-CONF-002
- "링크 정책은 백링크" -> UC-CONF-003
- "링크 자동 연결 or 연결 추천" -> UC-CAND-001
- "저장소는 로컬 파일" -> UC-CONF-001, UC-CONF-005
- "검색 기능은 D(검색+태그+백링크+그래프)" -> UC-CONF-004
- "사용자 범위 1인용, 인증 없음" -> UC-CONF-005
- "우선 플랫폼 데스크톱" -> UC-CONF-005
- "백업 불필요" -> UC-CONF-005 (범위 제외 정책)
- "MVP: 모든 노트 타입 CRUD + 백링크 + 그래프 뷰" -> UC-CONF-001, UC-CONF-003, UC-CONF-004
- Coverage Gaps
- 자동 연결/추천의 정확한 정책(완전 자동 vs 추천 우선), 허용 오탐률, 승인 UX는 미정이지만 "있으면 좋겠어" 수준의 선택 기능으로 해석하여 MVP 블로커로 보지 않는다.

# Coverage Gate
- Ready for Event Storming: YES
- Why
- 사용자 목표와 MVP 범위가 confirmed use case로 모두 반영되었다.
- 주요 실패/예외 흐름(파일 I/O 실패, 경로 충돌, 깨진 링크, 그래프 렌더 실패)을 정의했다.
- 외부 시스템 의존은 없고(로컬 파일만 사용), 시스템 경계가 명확하다.
- 구현 범위(CRUD/백링크/그래프)와 제외 범위(인증/협업/백업/클라우드)를 구분했다.
- Blocking Conditions
- 없음

# Blocking Unknowns
- 없음(현 단계 이벤트 스토밍 착수에 필요한 최소 정보 충족)

# Needs Review
- UC-CAND-001(자동 연결/추천)을 MVP 필수로 승격할지 여부는 추후 우선순위 결정 필요.

# Rejected Use Cases
- UC-REJ-001: 다중 사용자 협업 편집
  - Reason: 사용자 범위가 1인용으로 확정됨.
- UC-REJ-002: 인증/인가 흐름
  - Reason: 인증 없음으로 명시됨.
- UC-REJ-003: 클라우드 동기화 및 백업
  - Reason: 로컬 파일만 사용, 백업 불필요로 명시됨.

# Missing-but-Plausible Use Cases
- 없음

# Next Revision Focus
- 자동 연결/추천 기능을 MVP 포함 여부에 따라 confirmed로 승격하거나 후속 릴리스 항목으로 분리.
- 그래프 뷰 성능 한계(노트 수 증가 시) 기준을 정의.

# Oracle Handoff
- Allowed To Proceed: YES
- Confirmed Use Cases for Oracle
- UC-CONF-001
- UC-CONF-002
- UC-CONF-003
- UC-CONF-004
- UC-CONF-005
- Assumptions Forbidden for Oracle
- 노트 타입 목록을 임의 변경/축소하지 않는다.
- 식별 규칙(경로+파일명)을 UUID 등으로 치환하지 않는다.
- 저장소를 DB/클라우드로 변경 가정하지 않는다.
- 인증/다중사용자/백업 기능을 기본 범위에 추가하지 않는다.
- 자동 연결/추천을 필수 기능으로 단정하지 않는다(현재 candidate).
- User Approval Required Before Orchestration: YES
