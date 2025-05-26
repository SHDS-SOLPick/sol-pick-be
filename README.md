<div align="center">

<img height="250px" alt="SolPick Logo" src="./logo.png">

# 💳 SOL Pick Backend

SOL Pick 프로젝트의 **백엔드** 리포지토리입니다.

프로젝트 소개 및 공통 문서는 [👉 여기서](https://shds-solpick.github.io/) 확인하실 수 있습니다.


<p>
  <a href="#-프로젝트-소개">📌 프로젝트 소개</a> •
  <a href="#-아키텍처">🏗 아키텍처</a> •
  <a href="#-주요-기능">✨ 주요 기능</a> •
  <a href="#-기술-스택">🛠 기술 스택</a> •
  <a href="#-api-문서">📄 API 문서</a> •
  <a href="#-설치-및-실행">🚀 설치 및 실행</a> •
  <a href="#-team">👨‍👩‍👧‍👦 팀 소개</a>
</p>

</div>

---

## 📌 프로젝트 소개

### 💡 주제
**SOL Pick Backend** - 스마트 식생활 관리 서비스를 위한 ReciPICK X 신한카드 제휴 카드 백엔드 시스템

### 📝 개요
- **SOL Pick Backend**는 스마트한 식생활 관리를 위한 종합 백엔드 서비스입니다.
- **Spring Boot**와 **JPA**를 기반으로 한 견고한 REST API를 제공합니다.
- **ReciPICK**과의 원활한 연동을 위한 통합 인증 및 API 시스템을 구축했습니다.
- **JWT 기반 보안**, **스케줄링**, **외부 API 연동** 등 다양한 기능을 포함합니다.

### ⏱️ 개발 기간
**2025.02 ~ 2025.03**

---

## 🏗 아키텍처

### 📊 시스템 구조
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   SOL Pick FE   │    │  ReciPICK API   │    │   External APIs │
│    (React)      │    │                 │    │  (OpenAI, GCP)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │ REST API              │ HTTP Client           │ API Calls
         ▼                       ▼                       ▼
┌─────────────────────────────────────────────────────────────────┐
│                    SOL Pick Backend                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐│
│  │ Controller  │ │   Service   │ │ Repository  │ │   Entity    ││
│  │    Layer    │ │    Layer    │ │    Layer    │ │    Layer    ││
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘│
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐│
│  │   Security  │ │ Scheduling  │ │   OCR/AI    │ │  External   ││
│  │   (JWT)     │ │  Service    │ │   Service   │ │   Client    ││
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘│
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │     MariaDB     │
                    └─────────────────┘
```

---

## ✨ 주요 기능

### 1. 🔐 인증 및 권한 관리
- **JWT 기반 통합 인증 시스템**
- ReciPICK과의 연동 로그인
- Spring Security를 활용한 엔드포인트 보안
- 토큰 검증 및 갱신

```java
@RestController
@RequestMapping("/auth")
public class AuthController {
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO loginRequest) {
        AuthResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}
```

### 2. 🧊 스마트 냉장고 관리
- **식재료 CRUD 기능**
- **영수증 OCR 처리** (Google Cloud Vision API)
- **유통기한 알림 스케줄링**
- 카테고리별 식재료 분류 및 검색

```java
@Scheduled(cron = "0 30 9 * * ?", zone = "Asia/Seoul")
@Transactional
public void checkExpirationDates() {
    // 매일 오전 9시 30분 유통기한 검사 및 알림 생성
}
```

### 3. 💳 카드 서비스
- **SOL Pick 카드 발급 시스템**
- **커스텀 카드 디자인 관리**
- **포인트 적립 및 사용 처리**
- **카드 유효성 검증**

```java
@PostMapping("/issue-card")
public ResponseEntity<CardResponseDTO> issueCard(@RequestBody CardIssueRequestDTO requestDTO) {
    CardResponseDTO response = cardDesignService.issueCard(requestDTO);
    return ResponseEntity.ok(response);
}
```

### 4. 🎮 미니게임 시스템
- **푸디캣 게임 상태 관리**
- **레시피 선택 및 진행 상황 추적**
- **식재료 발견 시스템**
- **게임 완료 시 포인트 적립**

```java
@PostMapping("/discover-ingredient")
public ResponseEntity<DiscoveryResultDTO> discoverIngredient(@RequestBody DiscoverIngredientRequestDTO request) {
    DiscoveryResultDTO result = gameService.discoverIngredient(
        request.getUserId(), request.getRecipeId(), request.getIngredientName(), request.getRecipePoints());
    return ResponseEntity.ok(result);
}
```

### 5. 🤖 AI 기반 레시피 추천
- **OpenAI GPT-4 연동**
- **알러지 정보 기반 맞춤 추천**
- **보유 식재료 기반 레시피 생성**
- **일주일 식단 계획 수립**

```java
@GetMapping("/recommend/{userId}")
public Map<String, Object> getRecipeRecommendation(@PathVariable Integer userId) {
    List<RefrigeratorIng> ingredients = refrigeratorIngService.getIngredientsByUserId(userId);
    List<String> allergies = userAllergyService.getUserAllergies(userId);
    return openAiService.getRecipeRecommendation(ingredientNames, allergies);
}
```

### 6. 🔗 외부 API 연동
- **ReciPICK API 통합**
- **주문 내역 동기화**
- **포인트 시스템 연동**
- **결제 검증 시스템**

---

## 🛠 기술 스택

### 🖥 Backend Framework
<p>
  <img src="https://img.shields.io/badge/Spring_Boot-2.7.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/Java-11-007396?style=for-the-badge&logo=java&logoColor=white"/>
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white"/>
</p>

### 🔒 Security & Authentication
<p>
  <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"/>
  <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white"/>
</p>

### 🗃 Database & ORM
<p>
  <img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white"/>
  <img src="https://img.shields.io/badge/JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white"/>
  <img src="https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white"/>
</p>

### 📡 External APIs
<p>
  <img src="https://img.shields.io/badge/Google_Cloud_Vision-4285F4?style=for-the-badge&logo=googlecloud&logoColor=white"/>
  <img src="https://img.shields.io/badge/OpenAI_GPT--4-412991?style=for-the-badge&logo=openai&logoColor=white"/>
  <img src="https://img.shields.io/badge/ReciPICK_API-FF5A5F?style=for-the-badge&logo=api&logoColor=white"/>
</p>

### 🛠 Development Tools
<p>
  <img src="https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white"/>
  <img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white"/>
  <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white"/>
</p>

---

## 📄 API 문서

### 🔐 Authentication
```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

### 🧊 Refrigerator Management
```http
# 식재료 목록 조회
GET /api/solpick/refrigerator/ingredients/list/{userId}?sortType=latest

# 식재료 등록
POST /api/solpick/refrigerator/ingredients
Content-Type: application/json

{
  "userId": 1,
  "name": "토마토",
  "quantity": 3,
  "expiryDate": "2025-03-15T00:00:00"
}
```

### 💳 Card Service
```http
# 카드 발급
POST /solpick/api/card-design/issue-card
Content-Type: application/json

{
  "userId": 1,
  "designId": 1,
  "lastName": "KIM",
  "firstName": "JEONGRAN"
}
```

### 🎮 Mini Game
```http
# 게임 상태 조회
GET /solpick/api/game/state/{userId}

# 식재료 발견 처리
POST /solpick/api/game/discover-ingredient
Content-Type: application/json

{
  "userId": 1,
  "recipeId": 1,
  "ingredientName": "토마토",
  "recipePoints": 5000
}
```

---

## 🚀 설치 및 실행

### 📋 사전 요구사항
- Java 11 이상
- Maven 3.6 이상
- MariaDB 10.5 이상

### 🔧 환경 설정

1. **Repository 클론**
```bash
git clone https://github.com/SHDS-SOLPick/sol-pick-be.git
cd sol-pick-be
```

2. **데이터베이스 설정**
```sql
CREATE DATABASE solpick;
```

3. **환경 변수 설정** (`application-secret.properties`)
```properties
# Database
spring.datasource.url=jdbc:mariadb://localhost:3306/solpick
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT
jwt.secret.key=your_jwt_secret_key

# ReciPICK API
recipick.api.key=your_recipick_api_key

# OpenAI API
openai.api.key=your_openai_api_key

# SOL Pick API
solpick.api.key=your_solpick_api_key
```

4. **Google Cloud Vision API 설정**
```bash
# google-credentials.json 파일을 src/main/resources/에 배치
```

### ▶️ 실행 방법

```bash
# Maven을 사용한 실행
mvn spring-boot:run

# 또는 JAR 파일 생성 후 실행
mvn clean package
java -jar target/sol-pick-be-1.0.0.jar
```

---

## 📁 프로젝트 구조

```
src/
├── main/
│   ├── java/kr/co/solpick/
│   │   ├── auth/                 # 인증 관련
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── security/         # JWT, Security 설정
│   │   │   └── dto/
│   │   ├── refrigerator/         # 냉장고 관리
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   └── dto/
│   │   ├── card/                 # 카드 서비스
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   └── dto/
│   │   ├── game/                 # 미니게임
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   └── dto/
│   │   ├── point/                # 포인트 시스템
│   │   ├── member/               # 회원 관리
│   │   ├── external/             # 외부 API 연동
│   │   └── config/               # 설정 파일들
│   └── resources/
│       ├── application.properties
│       └── google-credentials.json
└── test/                         # 테스트 코드
```

---

## 👨‍👩‍👧‍👦 Team

<div align="center">
  <table>
    <tr>
      <td align="center">
        <img src="https://github.com/jrkim-kr.png" width="120px" height="120px" style="border-radius: 50%"/><br/>
      </td>
      <td align="center">
        <img src="https://github.com/eko147.png" width="120px" height="120px" style="border-radius: 50%"/><br/>
      </td>
      <td align="center">
        <img src="https://github.com/jinyoung1221.png" width="120px" height="120px" style="border-radius: 50%"/><br/>
      </td>
      <td align="center">
        <img src="https://github.com/ayeooong.png" width="120px" height="120px" style="border-radius: 50%"/><br/>
      </td>
    </tr>
    <tr>
      <td align="center"><b>김정란</b><br/></td>
      <td align="center"><b>고은지</b><br/></td>
      <td align="center"><b>양진영</b><br/></td>
      <td align="center"><b>임아영</b><br/></td>
    </tr>
    <tr>
      <td align="center">팀장 / Backend<br/>
        카드 관련 서비스, 미니게임 백엔드 개발<br/>
        게임 로직 구현<br/>
      </td>
      <td align="center">팀원 / Backend<br/>
        인증 시스템, 외부 API 연동<br/>
        ReciPICK 통합, 레시피 서비스<br/>
      </td>
      <td align="center">팀원 / Backend<br/>
        AI 레시피 추천 시스템<br/>
        OpenAI API 연동, 식단 추천<br/>
      </td>
      <td align="center">팀원 / Backend<br/>
        냉장고 CRUD, 유통기한 알림<br/>
        OCR 서비스, 스케줄링 시스템<br/>       
      </td>
    </tr>
    <tr>
      <td align="center"><a href="https://github.com/jrkim-kr">GitHub</a></td>
      <td align="center"><a href="https://github.com/eko147">GitHub</a></td>
      <td align="center"><a href="https://github.com/jinyoung1221">GitHub</a></td>
      <td align="center"><a href="https://github.com/ayeooong">GitHub</a></td>
    </tr>
  </table>
</div>

---

## 📚 관련 문서

- 📕 [ERD 설계도](https://www.erdcloud.com/d/gcDtQjFwdT2wfwoYe)
- 📘 [기능 명세서](https://chloekim99.notion.site/19b341dc2a9380a0ad69d45318d00af5?v=19b341dc2a93812babc0000cb8f89373)
- 📗 [API 명세서](https://chloekim99.notion.site/19e341dc2a9380dab3dbfbef4ca1dc06?v=19e341dc2a9381b3b442000c7402b403&pvs=4)
- 📋 [팀 프로젝트 노션](https://chloekim99.notion.site/DS-SW-4-7-1-144341dc2a9380b28c7ad1a0e69ab591)

---

<div align="center">
  <sub>ⓒ 2025 SOL Pick Backend Team. All rights reserved.</sub>
</div>
