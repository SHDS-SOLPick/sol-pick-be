# SOL SOL한 레시피

## 디렉토리 구조 (25/03/03 update)

```
solpick-be/ 
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── solpick/
│   │   │           ├── config/                    # 설정 클래스
│   │   │           ├── auth/                      # 인증 관련
│   │   │           │   ├── controller/
│   │   │           │   ├── service/
│   │   │           │   ├── dto/
│   │   │           │   └── security/
│   │   │           ├── card/                      # 카드 관리
│   │   │           │   ├── controller/
│   │   │           │   ├── service/
│   │   │           │   ├── repository/
│   │   │           │   ├── entity/
│   │   │           │   └── dto/
│   │   │           ├── point/                     # 포인트 시스템
│   │   │           │   ├── controller/
│   │   │           │   ├── service/
│   │   │           │   ├── repository/
│   │   │           │   ├── entity/
│   │   │           │   ├── dto/
│   │   │           │   └── game/                  # 포인트 게임
│   │   │           │       ├── controller/
│   │   │           │       ├── service/
│   │   │           │       ├── repository/
│   │   │           │       ├── entity/
│   │   │           │       └── dto/
│   │   │           ├── refrigerator/              # 냉장고 기능
│   │   │           │   ├── controller/
│   │   │           │   ├── service/
│   │   │           │   ├── repository/
│   │   │           │   ├── entity/
│   │   │           │   └── dto/
│   │   │           ├── main/                      # 메인 페이지 관련
│   │   │           │   ├── controller/
│   │   │           │   ├── service/
│   │   │           │   ├── repository/
│   │   │           │   ├── entity/
│   │   │           │   └── dto/
│   │   │           ├── user/                      # 사용자/마이페이지
│   │   │           │   ├── controller/
│   │   │           │   ├── service/
│   │   │           │   ├── repository/
│   │   │           │   ├── entity/
│   │   │           │   └── dto/
│   │   │           ├── cooking/                   # 함께요리 기능 (+)
│   │   │           │   ├── controller/
│   │   │           │   ├── service/
│   │   │           │   ├── repository/
│   │   │           │   ├── entity/
│   │   │           │   └── dto/
│   │   │           ├── api/                       # 연동 API
│   │   │           │   ├── client/
│   │   │           │   └── dto/
│   │   │           └── common/                    # 공통 기능
│   │   │               ├── exception/
│   │   │               ├── util/
│   │   │               └── dto/
│   │   └── resources/                             # 리소스 파일
│   └── test/                                      # 테스트 코드
├── build.gradle                                   # 의존성 관리
├── Dockerfile(+)                                     
└── README.md                                      
```
