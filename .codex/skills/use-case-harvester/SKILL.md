---
name: use-case-harvester
description: >
  사용자 요청을 해석해
  docs/use-case-harvests/<domain>/<task>/use-case-harvest.md 를
  호출마다 새로 생성하고,
  coverage gate 및 oracle 진행 가능 상태를 판정하는 스킬
---

# Use Case Harvester

이 스킬은 사용자의 최신 작업 요청을 해석하여  
`docs/use-case-harvests/<domain>/<task>/use-case-harvest.md` 를 호출마다 새 task 경로로 생성한다.

이 스킬의 목적은 다음이다.

- 사용자 요청을 유스케이스 관점으로 정리한다
- candidate / confirmed use case를 분리한다
- coverage mapping을 작성한다
- coverage gate를 판정한다
- oracle이 시작 가능한 상태인지 문서로 명시한다
- 단, oracle이나 downstream agent는 절대 직접 시작하지 않는다

---

## Source of Truth

이 스킬은 아래 설정을 source of truth로 따른다.

- `.codex/config.toml`
- `.codex/openai.yaml`
- `.codex/agents/use_case_harvester.toml`

설정 충돌 시:

1. `use_case_harvester.toml`
2. `config.toml`
3. `openai.yaml`

순으로 우선 적용한다.

---

## Primary Goal

이 스킬의 목표는 다음을 안전하게 수행하는 것이다.

- 사용자 프롬프트를 해석한다
- 호출 시점의 새 task identity를 생성한다
- 해당 task 경로에 새 harvest/work-unit 문서를 생성한다
- 유스케이스를 가능한 한 완전하게 수집한다
- 프롬프트 요구사항과 유스케이스 간 coverage mapping을 만든다
- coverage gate를 YES / PARTIAL / NO 로 판정한다
- oracle 입력 가능 여부를 문서 상태로 남긴다

---

## Non-Goals

이 스킬은 다음을 하지 않는다.

- 사용자에게 추가 질문하기 (예외: 기본 구현 스택 확인 질문은 필수 1회 허용)
- 이벤트 스토밍 시작하기
- aggregate / bounded context 설계 시작하기
- oracle 또는 downstream agent 호출하기
- 코드 수정하기
- 프롬프트에 없는 기능을 임의 확정하기
- 기존 run의 harvest 문서를 덮어쓰기
- 랜덤 id를 만들지 않는다
- 문서 id 필드를 사용하지 않는다
- 문서 identity는 `doc_path`로만 표현한다

---

## Global Rules

- 반드시 `AGENTS.md` 와 `ARCHITECTURE.md` 를 따른다
- 기본 구현 스택(언어/프레임워크/런타임)이 명시되지 않았으면, harvest 고도화 전에 해당 항목을 사용자에게 먼저 확인한다
- 최신 run 이전 산출물을 참고할 수 있으나, 기존 run 문서를 직접 수정하지 않는다
- 충돌은 삭제하지 말고 `Needs Review` 에 기록한다
- work unit 허브 문서 `docs/work-units/<domain>/<task>/index.md`를 함께 생성/갱신한다
- harvest 문서는 work unit 허브 문서로 backlink를 포함한다
- 랜덤 id를 만들지 않는다
- 모든 문서는 `doc_path`로 식별한다
- coverage gate가 YES여도 사용자 승인 전에는 downstream을 시작하지 않는다
- `.codex/stack-profile.yaml`가 존재하면 기술 스택 Source of Truth로 사용한다
- 없으면 사용자 입력, `.codex/repository-settings.md`, 기존 repo 탐지 결과를 합쳐 stack profile 초안을 만든다
- 핵심 기술 스택 필드가 확정되지 않으면 `status: ready-for-oracle`로 판정하지 않는다

---

## Invocation Identity Rule

- harvester 호출마다 새로운 `<task>` identity를 만든다.
- 권장 형식: `<prompt-summary-slug>-YYYYMMDD-HHMM`
- `<prompt-summary-slug>`는 최신 유저 프롬프트의 핵심 목적을 짧게 요약한 slug다.
- slug 규칙:
  - 소문자 kebab-case
  - 허용 문자: `a-z`, `0-9`, `-`
  - 단어 수 권장 3~8개, 최대 60자
  - random suffix 금지
- timestamp는 호출 시점 기준이며 random suffix는 금지한다.
- 기존 run 문서는 append-only로 보존한다.
- 오케스트레이션은 해당 run의 `<task>` 경로 문서를 기준으로 진행하면서 stage 문서를 수정/추가한다.

---

## Document Path Rule

use-case-harvest 문서는 반드시 아래 경로에 있어야 한다.

`docs/use-case-harvests/<domain>/<task>/use-case-harvest.md`

work unit 허브 문서는 반드시 아래 경로에 있어야 한다.

`docs/work-units/<domain>/<task>/index.md`

규칙:

- domain은 유지하되 task는 호출마다 새 identity를 사용한다
- 경로를 임의 추측하지 않는다
- index.md에는 harvest 문서 링크와 오케스트레이션 단계 상태를 기록한다

---

## Document Identity Rule

모든 문서는 `doc_path`로만 식별한다.

규칙:

- `doc_path`는 project-root-relative full path including filename 이어야 한다
- 별도의 document id 필드는 만들지 않는다
- 문서 identity는 경로에서만 derive 한다

---

## Input Priority

이 스킬은 아래 순서로 입력을 해석한다.

1. 사용자의 최신 프롬프트
<<<<<<< HEAD
2. 기존 `docs/use-case-harvests/<domain>/<task>/use-case-harvest.md`
3. 기존 `docs/work-units/<domain>/<task>/index.md`
=======
2. 가장 최신 run의 `docs/use-case-harvests/<domain>/<prev-task>/use-case-harvest.md` (있는 경우, 참고용)
3. 가장 최신 run의 `docs/work-units/<domain>/<prev-task>/index.md` (있는 경우, 참고용)
>>>>>>> 0013045 (chore(codex): align orchestration contracts and templates)
4. `AGENTS.md`
5. `ARCHITECTURE.md`
6. 관련 `docs/*`
7. 현재 코드베이스

---

## Prompt Interpretation Rules

프롬프트를 반드시 사용자 행위 관점으로 해석한다.

정리 대상:

- User Goal
- Requested Actions
- Constraints
- Expected Outcome
- Explicit Non-goals

주의:

- 유스케이스는 구현 작업이 아니라 사용자/시스템 행위여야 한다
- 근거가 약한 항목은 confirmed로 올리지 않는다
- 프롬프트 직접 근거가 없는 항목은 rejected 또는 missing-but-plausible 로 분리한다
- 기본 구현 스택이 비어 있으면 `Blocking Unknowns`에 명시하고 stack 확인 질문을 선행한다

---

## Use Case Harvest Rules

각 유스케이스는 다음을 포함해야 한다.

- UC ID
- Name
- Primary Actor
- Goal
- Preconditions
- Trigger
- Main Success Flow
- Alternative / Failure Flow
- Success Outcome
- Source Prompt Evidence
- Status (`candidate | confirmed | blocked | needs-review`)

구분 규칙:

- 확실한 것은 `Confirmed Use Cases`
- 가능하지만 근거가 약한 것은 `Missing-but-Plausible Use Cases`
- 프롬프트에 직접 근거가 없는 것은 `Rejected Use Cases`

---

## Coverage Mapping Rules

프롬프트의 각 요구를 최소 하나 이상의 유스케이스 또는 non-UC change에 매핑해야 한다.

반드시 기록:

- Prompt Requirement → Mapped Use Case IDs or Non-UC Change IDs
- Coverage Gaps

추가 점검:

- 주요 실패/예외 흐름 누락 여부
- 외부 시스템 관련 요구 반영 여부
- 구현 범위와 제외 범위 구분 여부

---

## Coverage Gate Rules

Coverage Gate는 UC가 하나 이상 존재하는 경우에만 판정한다.

### YES

다음 조건을 모두 만족할 때만 YES다.

1. 모든 사용자 목표가 confirmed use case에 반영되었다
2. 주요 실패/예외 흐름이 정의되었다
3. 관련 외부 시스템이 식별되었다
4. 구현 범위와 제외 범위가 구분되었다
5. Blocking Unknowns 가 이벤트 스토밍 착수를 막지 않는다

### PARTIAL

일부만 만족하고 보완이 더 필요할 때

### NO

핵심 정보가 비어 있어 설계 입력으로 쓰기 어려울 때

PARTIAL 또는 NO 인 경우:

- 문서를 저장하고 종료한다
- `Next Revision Focus` 를 반드시 채운다
- downstream 은 절대 시작하지 않는다

YES 인 경우:

- `stack_profile_status == READY` 이어야 한다
- `status: ready-for-oracle`
- `coverage_gate: YES`
- `next_step: wait-for-user-approval`

로 기록하고 종료한다

단, 사용자 승인 전에는 oracle을 절대 시작하지 않는다.

## Non-UC Scope Gate Rules

UC가 없고 non-UC 작업만 있는 경우에는 `non_uc_scope_status`로 판정한다.

### READY

다음 조건을 모두 만족할 때만 READY다.

1. UI/TECH/TEST/DOC 항목이 실행 가능 수준으로 분해되었다
2. 구현 범위와 제외 범위가 구분되었다
3. 설계 입력으로 사용할 수 있을 만큼 명확하다

### PARTIAL

범위가 모호하거나 서로 섞여 있을 때

PARTIAL 인 경우:

- 문서를 저장하고 종료한다
- `Next Revision Focus` 를 반드시 채운다
- downstream 은 절대 시작하지 않는다

READY 인 경우:

- `stack_profile_status == READY` 이어야 한다
- `status: ready-for-oracle`
- `non_uc_scope_status: READY`
- `next_step: wait-for-user-approval`

로 기록하고 종료한다

단, 사용자 승인 전에는 oracle을 절대 시작하지 않는다.

---

## Oracle Handoff Rules

`status == ready-for-oracle` 인 경우에만 `Oracle Handoff` 를 채운다.

반드시 포함:

- Allowed To Proceed: YES | NO
- Confirmed Use Cases for Oracle
- Non-Use-Case Changes for Oracle
- Assumptions Forbidden for Oracle
- User Approval Required Before Orchestration: YES

규칙:

- YES 여도 user explicit approval 전에는 실제 오케스트레이션 시작 금지
- oracle이 추정하면 안 되는 항목을 명시한다

---

## Required Document Structure

`use-case-harvest.md` 는 반드시 아래 구조를 따른다.

```md
# Properties
doc_path: docs/use-case-harvests/<domain>/<task>/use-case-harvest.md
owner: Codex
status: draft | blocked | ready-for-oracle
title: <작업 제목>
domain: <domain>
task: <task>
coverage_gate: YES | PARTIAL | NO | N/A
non_uc_scope_status: READY | PARTIAL | N/A
next_step: revise-harvest | finalize-stack-profile | wait-for-user-approval
last_updated: <YYYY-MM-DD:HH:mm>

# Prompt Interpretation
- User Goal
- Requested Actions
- Preferred Implementation Stack
- Constraints
- Expected Outcome
- Explicit Non-goals

# Work Item Classification
## UC
## UI
## TECH
## TEST
## DOC

# Candidate Use Cases

# Confirmed Use Cases

# Coverage Mapping
- Prompt Requirement -> Mapped Use Case IDs or Non-UC Change IDs
- Coverage Gaps

# Non-Use-Case Changes
## UI Changes
## Technical Changes
## Test / Quality Changes
## Documentation Changes

# Coverage Gate
- Ready for Event Storming: YES | PARTIAL | NO | N/A
- Why
- Blocking Conditions

# Non-UC Scope Gate
- Ready for Design/Planning: READY | PARTIAL | N/A
- Why
- Blocking Conditions

# Stack Profile Readiness
- stack_profile_path: .codex/stack-profile.yaml
- stack_profile_status: READY | PARTIAL | MISSING
- stack_profile_source: existing | derived | user-updated
- asked_user_for_stack: YES | NO
- required_fields_present:
- blocking_fields:

# Blocking Unknowns

# Needs Review

# Rejected Use Cases

# Missing-but-Plausible Use Cases

# Next Revision Focus

# Oracle Handoff
- Allowed To Proceed: YES | NO
- Confirmed Use Cases for Oracle
- Non-Use-Case Changes for Oracle
- Assumptions Forbidden for Oracle
- User Approval Required Before Orchestration: YES

# Backlinks
- docs/work-units/<domain>/<task>/index.md
````

`index.md` 는 최소한 아래 구조를 따른다.

```md
# Properties
doc_path: docs/work-units/<domain>/<task>/index.md
owner: Codex
status: active | completed
domain: <domain>
task: <task>
last_updated: <YYYY-MM-DD:HH:mm>

# Work Unit Summary
- latest_stage
- latest_status

# Stage Documents
- use_case_harvester: docs/use-case-harvests/<domain>/<task>/use-case-harvest.md
- oracle:
- doc_writer:
- doc_verify_before_execute:
- executor:
- test_gate:
- execute_writer:
- doc_verify_after_execute:
- closer:
```

---

## Stop Rules

아래 경우 즉시 종료한다.

- UC 포함 작업에서 coverage_gate == NO
- UC 포함 작업에서 coverage_gate == PARTIAL
- non-UC only 작업에서 non_uc_scope_status == PARTIAL
- 기존 문서와 새 정보가 충돌하지만 자동 해결 근거가 부족함
- domain/task identity를 새 run 경로로 결정할 수 없음
- stack_profile_status != READY

종료 시에도 반드시 문서는 저장하거나 갱신한다.

---

## Success Criteria

이 스킬이 성공적으로 끝났다는 뜻은 다음 중 하나다.

### Case A. Harvest improved but not ready

- 문서가 갱신되었다
- UC 포함 작업이면 coverage_gate 가 PARTIAL 또는 NO 다
- non-UC only 작업이면 non_uc_scope_status 가 PARTIAL 이다
- 다음 revision focus 가 명시되었다

### Case B. Harvest ready for oracle

- 문서가 갱신되었다
- UC 포함 작업이면 coverage_gate == YES
- non-UC only 작업이면 non_uc_scope_status == READY
- status == ready-for-oracle
- stack_profile_status == READY
- user approval required 상태가 명시되었다
- downstream agent는 호출하지 않았다

---

## Invocation Pattern

이 스킬은 다음처럼 호출된다.

```text
Use the use-case-harvester skill for this task:
<user request>
```

또는 오케스트레이션 전에 단독으로 다음 목적에 사용한다.

- 유저 요청을 유스케이스로 수집하고 싶을 때
- 아직 설계 전에 coverage gate를 확인하고 싶을 때
- oracle 입력 가능 상태인지 판정하고 싶을 때
