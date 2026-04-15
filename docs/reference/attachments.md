# 10-Attachments

## 목적
노트에 첨부되는 모든 미디어 파일을 중앙에서 관리합니다.

## 포함 파일 타입
- 이미지 (PNG, JPG, GIF, SVG)
- 다이어그램 (Excalidraw, Draw.io)
- 오디오 파일
- 비디오 파일
- PDF (참고 자료는 `08-references/`에)
- 기타 바이너리 파일

## Obsidian 설정
Settings → Files & Links에서:
- **Default location for new attachments**: `10-attachments/`
- **Automatically update internal links**: ON 권장

## 조직 방법

### 옵션 1: 플랫 구조 (권장)
모든 파일을 한 곳에:
```
10-attachments/
├── image1.png
├── diagram1.svg
└── screenshot1.png
```
- 장점: 간단, 이미지 중복 방지
- 단점: 파일 많을 시 관리 어려움

### 옵션 2: 주제별 서브폴더
```
10-attachments/
├── spring/
├── database/
└── architecture/
```

### 옵션 3: 날짜별
```
10-attachments/
├── 2025-01/
├── 2025-02/
└── 2025-03/
```

## 파일명 규칙
- 설명적인 이름 사용: `spring-bean-lifecycle.png`
- 공백 대신 하이픈 사용
- 날짜 포함 권장: `20251010-architecture-diagram.png`
- 소문자 사용 권장

## 이미지 삽입
```markdown
![[image-name.png]]
![[image-name.png|300]] # 너비 지정
![[image-name.png|300x200]] # 너비x높이
```

## 정리 팁
- 주기적으로 미사용 첨부파일 확인 및 삭제
- 큰 파일(동영상 등)은 외부 스토리지 고려
- 스크린샷은 설명적으로 이름 변경
- 중요 다이어그램은 소스 파일과 함께 보관

## 백업
- 10-attachments 폴더는 필수 백업 대상
- Git으로 관리 시 `.gitattributes`에서 LFS 고려
