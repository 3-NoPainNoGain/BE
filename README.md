# handDoc - BE 
Spring Boot 기반의 백엔드 서버로, 사용자 인증, WebRTC 시그널링, 데이터 관리 등의 기능을 담당합니다.

### 🛠️ 기술 스택
- 프레임워크 : Spring Boot, Spring Data JPA, QueryDSL
- 인증 : Spring Security, OAuth 2.0 
- 배포 : AWS EC2, Docker, Github Actions 
- 데이터베이스 : MySQL, Redis 

### 📁 프로젝트 구조 

```
src
├── main
│   ├── java
│   │   └── com.example.handdoc
│   │       ├── HandDocApplication.java
|   |       ├── global
|   |       ├── auth
|   |       ├── signaling
|   |       ├── user
|   |       |   ├── controller 
|   |       |   ├── domain
|   |       |   ├── dto 
|   |       |   ├── exception
|   |       |   ├── repository
|   |       |   └── service
|   |       | 
│   └── resources
│       ├── application.yml
|
└── test
```

### 📌 API 명세서 
### 🧱 ERD (Entity Relationship Diagram) 
