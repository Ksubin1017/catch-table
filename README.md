# Catch Table

식당 웨이팅 등록 및 관리 서비스 백엔드 API

## 프로젝트 개요

고객이 식당 웨이팅을 등록하고, 식당 사장이 순서를 호출하면 Slack 알림이 전송되는 웨이팅 관리 시스템입니다.

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.3.1 |
| Database | MySQL 8 |
| Messaging | Apache Kafka + Confluent Schema Registry (Avro) |
| Cache | Caffeine Cache |
| Notification | Slack Webhook |
| Build | Gradle (Multi-Module) |
| Container | Docker Compose |

## 모듈 구조

```
catch-table/
├── multi-module-api        # API 서버 (Spring Boot, 포트: 1017)
├── multi-module-database   # 도메인 로직, 엔티티, 서비스, DTO
├── kafka-consumer          # Kafka Consumer (Slack 알림 전송)
└── common-avro             # Avro 스키마 공유 모듈
```

## 주요 기능

### 고객
- 카테고리별 식당 목록 조회 (Caffeine 캐시 적용)
- 식당 상세 조회 (메뉴 포함, CompletableFuture 병렬 조회)
- 웨이팅 등록 / 취소
- 내 웨이팅 현황 조회 (대기 순서, 예상 대기 시간)

### 식당 사장
- 현재 웨이팅 현황 조회 (CompletableFuture 비동기 처리)
- 웨이팅 호출 → Kafka 이벤트 발행 → Slack 알림 전송

## 아키텍처

```
Client
  │
  ▼
multi-module-api (REST API)
  │
  ├── multi-module-database (Service / Repository)
  │         │
  │         └── MySQL
  │
  └── Kafka Producer (waiting-call-slack 토픽)
            │
            ▼
      kafka-consumer
            │
            ▼
      Slack Webhook
```

Avro 스키마(`WaitingCall`)를 `common-avro` 모듈에서 공유하여 Producer/Consumer 간 타입 안전성을 보장합니다.

## API 엔드포인트

### Restaurant

| Method | URI | 설명 |
|--------|-----|------|
| GET | `/restaurant/{category}` | 카테고리별 식당 목록 조회 |
| GET | `/restaurant/{restaurantId}/detail` | 식당 상세 조회 (메뉴 포함) |

### Waiting (고객)

| Method | URI | 설명 |
|--------|-----|------|
| POST | `/restaurant/{restaurantId}/waiting` | 웨이팅 등록 |
| POST | `/restaurant/{restaurantId}/waiting/{waitingId}/cancel` | 웨이팅 취소 |
| GET | `/restaurant/{restaurantId}/waiting/status` | 식당 웨이팅 전체 현황 조회 |
| POST | `/restaurant/{restaurantId}/my-waiting/{waitingId}` | 내 웨이팅 현황 조회 |

### Waiting (사장)

| Method | URI | 설명 |
|--------|-----|------|
| GET | `/restaurant/{restaurantId}/waiting/status/owner` | 사장용 웨이팅 현황 조회 |
| POST | `/waiting/call` | 다음 손님 호출 (Slack 알림 발송) |

## 실행 방법

### 사전 요구사항
- Java 17
- Docker & Docker Compose
- Kafka + Schema Registry (외부 또는 로컬)

### 1. 데이터베이스 실행

```bash
docker-compose up -d
```

### 2. application.yml 설정

`multi-module-api/src/main/resources/application.yml`에서 아래 항목을 환경에 맞게 수정하세요.

```yaml
spring:
  datasource:
    url: jdbc:mysql://<DB_HOST>:3306/catch_table
    username: root
    password: <PASSWORD>
  kafka:
    bootstrap-servers: <KAFKA_HOST>:9092
    producer:
      properties:
        schema.registry.url: http://<SCHEMA_REGISTRY_HOST>:8081
```

`kafka-consumer/src/main/resources/application.yml`도 동일하게 Kafka 설정을 맞춰주세요.

### 3. 서버 실행

```bash
# API 서버
./gradlew :multi-module-api:bootRun

# Kafka Consumer (별도 터미널)
./gradlew :kafka-consumer:bootRun
```

## 캐시 전략

`RestaurantService.getRestaurantList()`에 Caffeine 캐시 적용

- **Key**: `RestaurantCategory`
- **TTL**: 10분 (`expireAfterWrite`)
- **최대 엔트리**: 100개

## Kafka 토픽

| 토픽 | 설명 |
|------|------|
| `waiting-call-slack` | 웨이팅 호출 이벤트 (Avro 직렬화) |

**Avro 스키마 (`WaitingCall`)**

```json
{
  "type": "record",
  "name": "WaitingCall",
  "fields": [
    {"name": "restaurantName", "type": "string"},
    {"name": "waitingNumber", "type": "int"},
    {"name": "nextWaitingNumber", "type": "int"},
    {"name": "webhookUrl", "type": "string"}
  ]
}
```

## 예상 대기 시간 계산

| 앞 대기 팀 수 | 안내 메시지 |
|--------------|------------|
| 0팀 | 현재 대기 팀이 없습니다 |
| 1 ~ 10팀 | 약 30분 내 입장 가능 |
| 11 ~ 20팀 | 약 1시간 대기 예상 |
| 21팀 이상 | 약 2시간 이상 대기 예상 |
