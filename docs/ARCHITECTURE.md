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
