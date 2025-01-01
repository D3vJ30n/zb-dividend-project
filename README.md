# 주식 배당금 서비스

미국 주식 배당금 정보를 제공하는 API 서비스입니다.

## 주요 기능

1. 배당금 정보 조회
   - 회사명으로 배당금 정보 조회
   - 배당금 정보 캐싱

2. 회사 정보 관리
   - 회사 정보 등록/삭제
   - 회사명 자동완성
   - 회사 목록 페이징 조회

3. 사용자 관리
   - 회원가입/로그인
   - JWT 기반 인증

## 기술 스택

- Spring Boot 3.4.1
- Java 17
- Spring Data JPA
- H2 Database
- Redis
- Jsoup
- Spring Security
- JWT
- Docker

## 시작하기

### 필수 조건

- Java 17 이상
- Docker

### 실행 방법

1. Redis 서버 실행
```bash
docker run -d --name redis -p 6379:6379 redis
```

2. 애플리케이션 실행
```bash
./gradlew bootRun
```

## API 엔드포인트

### 회원 관리

- POST /auth/signup : 회원가입
- POST /auth/signin : 로그인

### 회사 정보

- GET /company : 회사 목록 조회
- POST /company : 회사 정보 추가
- DELETE /company/{ticker} : 회사 정보 삭제
- GET /company/autocomplete : 회사명 자동완성

### 배당금 정보

- GET /finance/dividend/{companyName} : 배당금 정보 조회

## 캐시 전략

- Redis를 사용하여 배당금 정보 캐싱
- 회사 정보 삭제 시 캐시 자동 삭제

## 에러 처리

- GlobalExceptionHandler를 통한 중앙화된 예외 처리
- 적절한 HTTP 상태 코드와 에러 메시지 반환

## 로깅

- logback을 사용한 로그 관리
- 로그 레벨별 출력 관리
- 일별 로그 파일 관리
