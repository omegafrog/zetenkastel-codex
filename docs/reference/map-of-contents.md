# 07-Maps of Content (MOC)

## 목적
제텔카스텐의 지식 네트워크를 탐색하기 위한 최상위 지도입니다.

## MOC의 역할
- Permanent notes들의 진입점
- 특정 주제의 큰 그림 제공
- Areas의 지식 구조 시각화
- 학습 경로 안내

## PARA와의 연결
```
MOC ←→ Areas ←→ Projects
 ↓
Permanent Notes
```

- **Areas**: 지속적 관심사 (무엇을 관리하는가)
- **MOC**: 지식의 구조 (어떻게 연결되는가)
- 하나의 Area는 여러 MOC를 가질 수 있음
- 하나의 MOC는 여러 Areas에 걸칠 수 있음

## MOC 예시
```markdown
# MOC - Spring Framework

## 개요
Spring 생태계와 핵심 개념들의 지식 지도

## 관련 Areas
- [[05-areas/백엔드개발]]
- [[05-areas/아키텍처]]

## 핵심 개념

### 1. 기초
- [[03-permanent-notes/IoC컨테이너]]
- [[03-permanent-notes/의존성주입]]
- [[03-permanent-notes/빈라이프사이클]]

### 2. 웹 개발
- [[03-permanent-notes/MVC패턴]]
- [[03-permanent-notes/필터와인터셉터]]

### 3. 고급
- [[03-permanent-notes/AOP개념]]
- [[03-permanent-notes/트랜잭션관리]]

## 학습 경로
1단계: IoC와 DI 이해
2단계: 웹 MVC 구조
3단계: 데이터 접근
4단계: 고급 기능

## 관련 프로젝트
- [[04-projects/Spring-Security-구현]]
- [[06-archives/projects/2024-게시판프로젝트]]

## 참고 자료
- [[02-literature-notes/토비의스프링]]
- [[references/Spring-공식문서]]

---
최종 업데이트: {{date}}
```

## MOC vs Structure Notes
- **MOC**: 넓고 포괄적, 탐색과 발견 중심
- **Structure Notes**: 좁고 깊게, 특정 주제의 논리적 구조

## MOC 관리
- 주요 Areas마다 MOC 생성
- 월 1회 정도 업데이트
- 새 permanent note 추가 시 관련 MOC에 링크
- 홈 대시보드에서 주요 MOC 연결
