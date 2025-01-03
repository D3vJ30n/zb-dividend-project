# 주식 배당금 서비스

미국 주식 배당금 정보를 제공하는 API 서비스입니다.

## 목차
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [시스템 아키텍처](#시스템-아키텍처)
- [프로젝트 구조](#프로젝트-구조)
- [시작하기](#시작하기)
- [API 문서](#api-문서)
- [캐시 전략](#캐시-전략)
- [보안](#보안)

## 주요 기능

### 1. 배당금 정보 조회
- 회사명으로 배당금 정보 조회
  - 배당금 지급 내역
  - 배당금 지급 주기
- Yahoo Finance 스크래핑
- Redis 캐싱

### 2. 회사 정보 관리
- 회사 정보 등록/삭제
- Trie 자료구조 기반 회사명 자동완성
- 회사 목록 페이징 조회

### 3. 사용자 관리
- JWT 기반 인증
- Spring Security 기반 보안 처리
- 비밀번호 암호화 (BCrypt)

## 기술 스택

### Backend
- Java 17
- Spring Boot 3.4.1
- Spring Data JPA
- Spring Security
- Gradle

### Database
- H2 Database (개발)
- Redis 7.0
  - 캐싱

### 인프라
- Docker

### 테스트
- JUnit 5
- Spring Boot Test

## 시스템 아키텍처
```
[Client] → [Spring Boot Application]
              ↓
       [Redis Cache] ← [Yahoo Finance API]
              ↓
       [H2 Database]
```

## 프로젝트 구조
```
src
├── main
│   ├── java
│   │   └── com.example.demo
│   │       ├── config        # 설정 클래스
│   │       ├── controller    # REST API 컨트롤러
│   │       ├── dto          # 데이터 전송 객체
│   │       ├── model        # 도메인 모델
│   │       ├── persist      # 영속성 계층
│   │       ├── scraper      # 데이터 스크래핑
│   │       └── security     # 보안 설정
│   └── resources
│       └── application.yml  # 애플리케이션 설정
└── test
    └── java                # 테스트 코드
```

## 시작하기

### 필수 조건
- Java 17 이상
- Redis

### 환경 변수 설정
```properties
# application.yml 설정 필요
spring.data.redis.host=localhost
spring.data.redis.port=6379
jwt.secret=your-secret-key
```

## API 문서

### 회원 관리
- POST /auth/signup : 회원가입
  - Request Body
    ```json
    {
      "username": "user@example.com",
      "password": "password123!"
    }
    ```
  - Response: 201 Created

- POST /auth/signin : 로그인
  - Request Body
    ```json
    {
      "username": "user@example.com",
      "password": "password123!"
    }
    ```
  - Response
    ```json
    {
      "token": "eyJhbGciOiJ..."
    }
    ```

### 회사 정보
- GET /company : 회사 목록 조회
  - Parameters
    - page: 페이지 번호 (기본값: 0)
    - size: 페이지 크기 (기본값: 10)
  - Response: 200 OK

- POST /company : 회사 정보 추가
  - Request Body
    ```json
    {
      "ticker": "AAPL"
    }
    ```
  - Response: 201 Created

- DELETE /company/{ticker} : 회사 정보 삭제
  - Response: 200 OK

- GET /company/autocomplete : 회사명 자동완성
  - Parameters
    - keyword: 검색어
  - Response: 200 OK

### 배당금 정보
- GET /finance/dividend/{companyName}
  - Parameters
    - companyName: 회사명
  - Response: 200 OK

## 캐시 전략

### Redis 캐시 설정
- 만료 시간: 3분
- 캐시 키 구조
  - 배당금 정보: `finance:{companyName}`
  - 회사 정보: `company:{ticker}`

### 캐시 정책
- Write-Through 캐시 전략
- 자동 캐시 삭제 조건
  - 회사 정보 변경 시
  - 배당금 정보 업데이트 시

## 보안

### 인증/인가
- JWT 기반 토큰 인증
- Role 기반 접근 제어
  - READ: 일반 사용자
  - WRITE: 관리자

### 보안 설정
- CORS 설정
- 비밀번호 암호화