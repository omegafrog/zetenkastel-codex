# ARCHITECTURE.md

## 1. Purpose

이 문서는 시스템의 전체 구조와 설계 원칙을 설명한다.  
개발자와 에이전트는 이 문서를 기준으로 설계와 구현을 수행해야 한다.

---

## 2. Why This Architecture Exists

이 시스템은 다음 문제를 해결하기 위해 설계되었다:

- 비즈니스 로직이 여러 계층에 분산되는 문제
- 모듈 간 경계가 무너지는 문제
- 변경 흐름이 명확하지 않은 문제
- 정책이 테스트 없이 추가되는 문제

이를 해결하기 위해:

- 도메인 중심 설계
- 모듈형 모놀리스 구조
- aggregate 기반 트랜잭션 경계
- policy 중심 설계

를 사용한다.

---

## 3. Big Picture

시스템은 모듈형 모놀리스로 구성된다.

- Core Module: 비즈니스 로직
- Support Module: 횡단 관심사
- Main Module: 실행 조립

Core Module 내부 구조:

```

ui → app → domain
↓
port ← adapter

```

모든 비즈니스 변경은 다음 흐름을 따른다:

```

Command → Aggregate → Event → Policy → Next Command

```

---

## 4. Module Types

### Core Module

- 도메인 모델과 비즈니스 로직 포함
- aggregate, entity, value object, use case 포함

### Support Module

- 인증, 사용자 관리, 공통 인프라 등
- 도메인 정책을 가지지 않는다
- Core Module의 도메인을 침범하지 않는다

### Main Module

- 애플리케이션 실행 진입점
- 모든 모듈을 조합
- 비즈니스 로직 금지

---

## 5. Dependency Rules

### Layer Dependency

```

ui → app → domain
app → port
adapter → port

```

- domain은 어떤 계층에도 의존하지 않는다
- ui는 domain을 직접 호출하지 않는다

### Module Dependency

- Core Module은 다른 Core Module의 내부 구현에 의존하지 않는다
- 모듈 간 협력은 API, port, event를 통해 수행한다
- Main Module만 전체 모듈을 조립한다

---

## 6. Domain Model Rules

### Aggregate

- aggregate는 변경의 일관성 경계다
- aggregate root만 상태 변경의 진입점이다

### Entity / Value Object

- entity는 identity를 가진다
- value object는 불변으로 설계한다

### Repository

- repository는 aggregate root 기준으로 정의한다
- 내부 객체는 root를 통해서만 수정된다

### Transaction

- 트랜잭션은 aggregate 단위로 나눈다
- 여러 aggregate를 하나의 트랜잭션으로 묶지 않는다
- aggregate 간 일관성은 eventual consistency를 허용한다

---

## 7. Inter-Module Collaboration

- Core Module 간 협력은 API 또는 port를 통해 수행한다
- 다른 모듈 내부 구현 직접 접근 금지
- 호출 실패 시 retry하지 않고 실패 처리
- retry는 상위 정책에서 처리한다

---

## 8. Persistence Rules

- JPA Entity는 aggregate root만 사용한다
- domain 모델은 JPA에 종속되지 않는다
- 조회는 별도 query 로직으로 분리할 수 있다

---

## 9. Spring Boot Composition

- Main Module만 Spring Boot Application을 가진다
- Bean 구성은 Main Module에서 수행한다
- 각 모듈은 독립적으로 테스트 가능해야 한다

---

## 10. Anti-Patterns

다음은 금지한다:

- domain에서 Spring 의존성 사용
- controller에서 repository 직접 호출
- main module에 비즈니스 로직 작성
- support/common 모듈에 도메인 정책 작성
- aggregate root 외 객체 직접 수정
- 여러 aggregate를 하나의 트랜잭션으로 묶는 것

---

## 11. Policy Rule (Critical)

- 모든 비즈니스 규칙은 policy로 표현한다
- policy는 반드시 테스트 가능해야 한다
- 최소 1개 이상의 테스트 케이스를 가져야 한다
