# 06-Archives (PARA 통합)

## 목적
완료된 프로젝트와 더 이상 활성화되지 않은 Areas를 보관합니다.

## PARA의 Archives
- 비활성 항목들의 저장소
- 참고용으로 보관
- 필요시 다시 활성화 가능

## 제텔카스텐과의 통합
- **프로젝트 문서**는 여기로 아카이브
- **지식 자체**(permanent notes)는 아카이브하지 않음
- 프로젝트에서 추출한 영구 지식은 `03-permanent-notes/`에 유지

## 보관 대상
✅ 아카이브 대상:
- 완료된 프로젝트 폴더
- 더 이상 관리하지 않는 Areas
- 옛날 fleeting notes (처리 완료된 것)

❌ 아카이브하지 않을 것:
- Permanent notes (항상 활성)
- Literature notes (계속 참고 가능)
- MOC (살아있는 문서)

## 구조
```
06-archives/
├── projects/
│   ├── 2024-프로젝트A/
│   └── 2025-프로젝트B/
├── areas/
│   └── 옛날-관심사/
└── fleeting-notes/
    └── 2024-Q1/
```

## 아카이브 프로세스

### 프로젝트 완료 시
1. 프로젝트 최종 검토
2. 얻은 인사이트를 permanent notes로 추출
   ```markdown
   # Permanent Note: [프로젝트에서 배운 것]

   출처: [[06-archives/projects/프로젝트명]]
   ```
3. 프로젝트 폴더를 `06-archives/projects/`로 이동
4. 관련 Areas 업데이트

### Area 비활성화 시
1. Area의 가치 있는 지식이 permanent notes에 있는지 확인
2. Area 폴더를 `06-archives/areas/`로 이동
3. 관련 MOC에서 링크 업데이트

## 검색 가능성
- 아카이브된 내용도 Obsidian 검색에 포함
- 파일명에 날짜 포함 권장: `2024-프로젝트명/`
- 아카이브 노트에 태그: `#archived`

## 정리 규칙
- 연 1회 아카이브 검토
- 2년 이상 미참조 시 삭제 고려
- 중요 프로젝트는 README 작성하여 보관
