# handDoc - BE 
Spring Boot 기반의 백엔드 서버로, 사용자 인증, WebRTC 시그널링, 데이터 관리 등의 기능을 담당합니다.

## 🛠️ 기술 스택
- 프레임워크 : Spring Boot, Spring Data JPA
- 인증 : Spring Security, OAuth 2.0 
- 배포 : AWS EC2, Docker, Nginx, AWS RDS, MongoDB Atlas
- CI/CD : Github Actions 
- 데이터베이스 : MySQL, MongoDB
- 외부 API : OpenAI API, Naver CLOVA Speech-to-Text API, 공공 데이터 포털 API 

## 🚀 실행 방법

### 1. 실행 환경

백엔드 서버를 로컬에서 실행하기 위해서는 다음 환경 요소가 필요합니다.
- Java Development kit (JDK) : 17 이상
- Gradlew : Wrapper 사용 (프로젝트 내에 포함되어 있음)
- Docker 및 Docker Compose : 데이터 환경 구성을 위해 필수
- Database : MySQL, MongoDB 

### 2. 환경 변수 설정

프로젝트를 실행하려면 환경변수 파일이 필요합니다. 프로젝트 루트 디렉토리에 `.env` 파일을 생성하고, {}로 표시된 부분은 직접 받은 키와 URL로 대체하여 입력합니다. 
```bash
SPRING_PROFILES_ACTIVE=local

DOCKER_DB_PASS=0000

# LOCAL
LOCAL_DB_URL=jdbc:mysql://localhost:53306/handdoc
LOCAL_DB_USERNAME=root
LOCAL_DB_PASSWORD=0000

DOCKER_MONGO_USERNAME=root
DOCKER_MONGO_PASSWORD=0000
MONGO_DB_NAME=handdoc

#JWT
JWT_SECRET={직접 설정}
JWT_ACCESS_TOKEN_EXPIRATION=18000000

#KAKAO
KAKAO_REST_API_KEY={직접 받은 카카오 API 키}
KAKAO_REDIRECT_URI={직접 설정한 카카오 Redirect URL} 

#GOOGLE
GOOGLE_CLIENT_ID={직접 받은 구글 API 키}
GOOGLE_REDIRECT_URI={직접 설정한 구글 Redirect URL}
GOOGLE_CLIENT_SECRET={직접 설정한 구글 Secret 키}

# NAVER CLOVA
CLOVA_API_KEY_ID={직접 받은 네이버 클로버 API 키 ID}
CLOVA_API_KEY={직접 받은 네이버 클로버 API 키}

# SWAGGER 
LOCAL_SWAGGER=http://localhost:8080

# OPEN AI
OPEN_API_KEY={직접 받은 Open AI API 키}

# 공공데이터 
SEOUL_API_KEY={직접 받은 공공데이터 API 키}
```

#### API 키 생성 
- [카카오 로그인](https://developers.kakao.com/)
- [구글 로그인](https://docs.cloud.google.com/identity-platform/docs/use-rest-api?hl=ko) 
- [네이버 클로버](https://www.ncloud.com/v2/product/aiService/csr) 
- [공공데이터](https://www.data.go.kr/) 
- [OPEN AI](https://openai.com/ko-KR/index/openai-api/)

### 3. 데이터베이스 환경 구성
MySQL 및 MongoDB 환경을 Docker Compose를 사용하여 구성하고 백그라운드에서 실행하며, 필수 데이터베이스를 생성합니다. 

#### A. Docker 컨테이너 실행 

프로젝트 루트 디렉토리에서 다음 명령어를 실행하여 데이터베이스 컨테이너를 시작합니다. 

```bash
# Docker Compose 파일을 사용하여 데이터베이스 컨테이너 설정 
docker compose up -d
```

#### B. 필수 데이터베이스 생성 
- MySQL 데이터베이스 생성
```bash
# MySQL 컨테이너 접속
docker exec -it handdoc-mysql mysql -u root -p

# .env의 DOCKER_DB_PASS 값을 비밀번호로 입력

# MySQL 쉘에서 데이터베이스 생성
CREATE DATABASE handdoc;

# MySQL 쉘 종료
exit
```
- MongoDB 데이터베이스 생성
```bash
# MongoDB 쉘 접속
docker exec -it handdoc-mongo mongosh -u root -p

# .env의 DOCKER_MONGO_PASSWORD 값을 비밀번호로 입력

# MongoDB 쉘에서 데이터베이스로 전환 (없으면 생성됨)
use handdoc

# MongoDB 쉘 종료
exit
```

### 4. 빌드 및 실행 
```bash
# Gradlew 빌드
./gradlew build -x test

# 실행
java -jar build/libs/handdoc-0.0.1-SNAPSHOT.jar

```
### 5. 스웨거 조회 
[Swagger URL](http://localhost:8080/swagger-ui/index.html) 접속 



## 📝 API 명세서
[API 명세서](https://blossom-handbell-e0c.notion.site/API-1cd50b8ae11e80f0a4c7ffae1ba19107?pvs=73)

## 📌 ERD 
<img width="1000" height="600" alt="image" src="https://github.com/user-attachments/assets/9e2088db-a451-4cbc-bd83-2446df654f64" />

## ⚒️ 시스템 아키텍처 
<img width="3903" height="2042" alt="시스템아키텍쳐-2차보고서용 drawio (1)" src="https://github.com/user-attachments/assets/1f45cba8-f4f9-47fc-9564-be948b583641" />
