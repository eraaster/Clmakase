# 올리브영 대규모 세일 이벤트 시스템

Cloud Wave 7기 최종 프로젝트: AWS 기반 대규모 트래픽 처리 시스템

## 프로젝트 구조

```
Clmakase/
├── backend/                    # Spring Boot 백엔드
│   ├── src/main/java/com/oliveyoung/sale/
│   │   ├── config/            # 설정 (Redis, 초기 데이터)
│   │   ├── controller/        # REST API 컨트롤러
│   │   ├── domain/            # 엔티티 (Product, PurchaseOrder)
│   │   ├── dto/               # 요청/응답 DTO
│   │   ├── repository/        # JPA Repository
│   │   └── service/           # 비즈니스 로직
│   ├── Dockerfile
│   └── build.gradle
├── frontend/                   # React 프론트엔드
│   ├── src/
│   │   ├── components/        # 재사용 컴포넌트
│   │   ├── pages/             # 페이지 컴포넌트
│   │   └── api.js             # API 클라이언트
│   ├── Dockerfile
│   └── package.json
├── monitoring/                 # Prometheus 설정
├── docker-compose.yml          # 로컬 개발 환경
└── README.md
```

## 빠른 시작

### 1. Docker Compose로 실행 (권장)

```bash
# 기본 실행 (백엔드 + 프론트엔드 + Redis)
docker-compose up -d

# 접속
# - 프론트엔드: http://localhost:3000
# - 백엔드 API: http://localhost:8080
# - H2 Console: http://localhost:8080/h2-console

# 모니터링 포함 실행
docker-compose --profile monitoring up -d

# 전체 스택 (MySQL 포함)
docker-compose --profile full up -d
```

### 2. 로컬 개발 환경

```bash
# Redis 실행 (필수)
docker run -d -p 6379:6379 redis:7-alpine

# 백엔드 실행
cd backend
./gradlew bootRun

# 프론트엔드 실행 (별도 터미널)
cd frontend
npm install
npm run dev
```

## API 명세

### 상품 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/products` | 상품 목록 조회 |
| GET | `/api/products/{id}` | 상품 상세 조회 |

### 세일 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/sale/status` | 세일 상태 조회 |
| POST | `/api/sale/start` | 세일 시작 |
| POST | `/api/sale/end` | 세일 종료 |

### 대기열 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/queue/enter` | 대기열 진입 |
| GET | `/api/queue/status` | 대기 순번 조회 |

### 구매 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/purchase` | 구매 처리 |

## 응답 형식

모든 API는 통일된 응답 형식을 사용합니다:

```json
{
  "success": true,
  "data": { ... },
  "message": "성공 메시지",
  "errorCode": null
}
```

## 시연 시나리오

1. **메인 화면 접속** → 정가 상품 확인
2. **타이머 시작** 또는 **'할인시작' 버튼 클릭**
3. **가격 즉시 변경** (예: ₩50,000 → ₩35,000 [30% OFF])
4. **상품 클릭** → 상세 페이지 → **구매 버튼 클릭**
5. **대기열 화면** → "대기 순번: 157명 → 84명 → 12명" 실시간 감소
6. **구매 완료** → "구매가 완료되었습니다" → 메인 화면 복귀

## 핵심 기술 선택 근거

### Redis Sorted Set 대기열

**Q: "왜 대기열을 Redis Sorted Set으로 구현했나요?"**

- 시간순 정렬: score를 timestamp로 사용해 FIFO 보장
- O(log N) 순위 조회: ZRANK로 내 앞에 몇 명인지 즉시 확인
- 원자적 연산: ZADD, ZREM이 atomic하여 동시성 안전
- List는 순위 조회가 O(N), DB Queue는 부하가 큼

### 세일 상태 Redis 관리

**Q: "세일 상태를 Redis로 관리하는 이유는?"**

- EKS 다중 Pod 환경에서 일관성 보장
- In-Memory는 서버별 분리로 동기화 불가
- Redis는 중앙 저장소 역할

### 비관적 락 재고 관리

**Q: "왜 비관적 락을 사용했나요?"**

- 동시 구매 시 Lost Update 방지
- DB 레벨 동시성 제어로 정합성 보장
- 대기열로 락 경합 최소화

### Polling vs WebSocket

**Q: "실시간 순번 업데이트를 어떻게 처리했나요?"**

- 2초 간격 Polling 선택
- 구현 간단, 연결 관리 불필요
- EKS Pod 재시작에도 안정적
- 프로덕션에서는 SSE + Redis Pub/Sub 고려

## 확장 고려사항

### Aurora 읽기/쓰기 분리

```java
@Transactional(readOnly = true)  // Reader 인스턴스로 라우팅
public List<Product> getProducts() { ... }

@Transactional  // Writer 인스턴스로 라우팅
public void purchase() { ... }
```

### 모니터링

- Prometheus: 메트릭 수집
- Grafana: 대시보드 시각화
- Actuator: `/actuator/prometheus` 엔드포인트

### CI/CD

- GitHub Actions: 빌드/테스트
- Argo CD: GitOps 배포
- EKS: 컨테이너 오케스트레이션
