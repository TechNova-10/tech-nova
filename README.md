# 💻:TECHNOVA:💻
## 물류 관리 및 배송 시스템을 위한 MSA 기반 플랫폼 개발
- 프로젝트 소개 <br>
  ° B2B 물류 관리 및 배송 시스템<br>
  ° MSA(Microservice Architecture)를 기반으로 설계<br>
  ° 물류 시스템의 디지털화<br>
  ° 효율적 배송 프로세스

## 팀원 구성 및 담당 부분
<div align="center">

|         조우석         |                 한미수                 |              조창현               | 신진우 |
|:-------------------:|:-----------------------------------:|:------------------------------:|:---:|
| 허브 관리,허브 간 이동정보, AI | 배송 관리,배송 경로 기록 관리,배송 담당자 관리, 사용자 관리 | 업체 관리, 상품 관리, 주문 관리, 배송 담당자 배정 로직 |  -  |
</div>


## 1. 개발 환경
- **프로젝트 개발 환경** : IntelliJ
- **Backend**: Java 17, Spring Boot 3.x
- **Database**: PostgreSQL
- **Build Tool**: Gradle
- **API 설계**: RESTful API
- **서비스 탐색**: Spring Cloud Eureka
- **API Gateway**: Spring Cloud Gateway
- **보안**: Spring Security, JWT, BCrypt
- **버전 관리** : Git
- **협업 툴** : Slack, Notion

## 2. 개발 기간 및 작업 관리
- 24-12-05 ~ 24-12-17(총 13일)

## 3. 아키텍쳐
![인프라](https://github.com/user-attachments/assets/a8bba90b-c831-42cc-b86b-944a7ebe858c)

## 4. 테이블 명세서
https://teamsparta.notion.site/5b791b85f72847458f53089b0064f929

## 5. ERD
<img width="852" alt="erd" src="https://github.com/user-attachments/assets/8e901a19-e19c-4c8d-b0ac-f9ba83ae5a15" />

## 6. API 명세서
https://teamsparta.notion.site/API-57d139b62887422283c4030f11d26af2
## 트러블 슈팅

### 한미수
### 경유지 정보 부족 또는 없음으로 순서 결정 불가

- 문제정의
  1. 업체 배송 담당자가 방문해야 하는 장소들의 위경도 값을 기반으로 경유지 순서를 정하려 AI에 요청 시, 주어진 정보가 부족해 응답 불가능 메시지가 발생
  2. 경유지가 없을 시 예외가 발생
- 해결과정

  어떤 정보를 주어야 AI가 대답할 수 있는지, 그리고 경유지가 없을 시 AI에게 어떤 값을 요청하고 응답을 받을지 결정.

  1. 방문해야 하는 모든 장소를 |로 구분해 한 문자열로 생성.
  2. AI에게 최대한 상세하게 메시지를 요청.
  3. 경유지 없이 방문 장소 두 곳일 때 대처 방안도 포함, 경유지 유무에 관계없이 응답 반환.

    ```java
        private List<Integer> getWaypointsOrder(List<LocationData> locationDatas) {
            String waypointsString = buildWaypointsString(locationDatas);
    
            String requestBody = "내가 다음과 같이 여러 장소들을 경도,위도로 보냈어. 각 장소는 |로 구분돼. 차로 이동했을 때 가장 빠르게 이동할 수 있을지 순서를 알려줘. 만약 내가 준 장소가 두개라면 임의적으로 너가 1번 2번을 정해. 다른 부가 설명없이 다음과 같이 방문 순서만 응답해줘, 그리고 각 방문 순서는 ,로 구분해줘. 예시 보여줄게. 1,2,3,5,4" + waypointsString;
    
            String response = googleApiService.generateContent(requestBody);
    
            return parseOptimizedOrder(response);
        }
    ```

- 결과

  AI가 경유지가 없거나 있는 경우 모두 응답 결과 반환
  AI가 응답한 경로의 순서를 Integer 타입의 리스트로 가공해 이후 경유지 지정에 이용.

    ```java
    public List<Integer> parseOptimizedOrder(String jsonResponse) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
    
                String text = rootNode.at("/candidates/0/content/parts/0/text").asText().trim();
    
                return Arrays.stream(text.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse optimized order from response text", e);
            }
        }
    ```


### 주문 생성과 배송 자동 수행 구분 문제 해결

- 문제정의
  1. 배송 생성 로직은 주문이 생성되면 자동으로 수행되며, 권한은 MASTER만 부여됨.
  2. 주문은 MASTER가 아니어도 생성 가능.
  3. 생성과 자동 수행을 어떻게 구분해야 하나?
- 해결과정

  X-Order-Origin 커스텀 헤더 값을 정의해 API 요청에 헤더 값이 있으면 주문 서비스나 다른 서비스에서 호출했다고 결정.

  API 요청에 헤더 값이 없으면 마스터 권한을 소유한 사용자가 요청한 것인지 검증.

    ```java
    @PostMapping
    public ResponseEntity<ApiResponseDto<UUID>> createDelivery(
    	@RequestBody DeliveryRequest request,
      @RequestHeader(value = "X-Order-Origin", required = false) String orderOriginToken,
      @RequestHeader(value = "X-User-Id", required = true) UUID userId,
      @RequestHeader(value = "X-Role", required = true) String role
    ) {
    	UUID deliveryId = deliveryService.createDelivery(request.toDTO(), orderOriginToken);
      return ResponseEntity.ok(ApiResponseDto.success("Delivery created successfully", deliveryId));
    }
    ```

    ```java
    createDelivery(dto, "orderApp-001", userId, role);
    ```

- 결과

  X-Order-Origin 헤더가 있으면 외부 호출로 처리되고, 헤더가 없으면 마스터 권한을 가진 사용자가 요청한 것으로 간주하여 자동 수행 여부를 결정.

### 조창현
### **통신 호출**

**문제점:**  각 서비스 간 통신 호출은 이루어 졌는데 실제 아이디 값 확인하는데 에러 발생<br>
**원인** : 응답 형식 또는 데이터 타입이 다른 형태라서 검증 실패 발생.<br>
**해결 :** 응답 형식 또는 데이터 타입을 통일하여 문제를 해결<br>

```java
// ClientAPiResponse 활용하여 타입 통일하여 문제를 해결
@FeignClient(name = "hub-service", path = "/api/v1/hubs")
public interface HubServiceClient {

    @GetMapping("/{hubId}")
    ClientApiResponse<ApiResponseDto<HubResponse>> getHub(@PathVariable("hubId") UUID hubId); // 허브 ID로 조회

}

```

### **무한 API 호출**

**문제점** : 무한 API 호출이 발생하여 서비스 장애 초래 (메모리 공간이 부족할 때까지 무한 호출)

```java
// 다음과 같은 형식으로 무한 호출
/api/v1/api/v1/api/v1/api/v1/api/v1/api/v1/api/v1/api/v1/api/v1/api/v1/
api/v1/api/v1/api/v1/api/v1/api/v1/api/v1/api/v1/api/v1/api/v1/api/v1/api/v1
/api/v1/api/v1/api/v1/api/v1/api/v1/api/v1/엔드포인트
```

**원인** : 예외 발생하면  globalExceptionHandler에서 공통 예외 처리가 이루어져야 하는데 해당 코드에서 반환이 이루어지지 않음

```java
// 다음과 같이 작성하여 에러 발생
@ControllerAdvice
public ApiResponseDto<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage());
        return ApiResponseDto.<Void>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .statusMessage(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .build();
    }
    
    // 다른 코드
```

**해결** : (1.)  또는 (2.) 방식으로 해결이 가능한데 (2.)으로 해결함
```java
// 해결방법
// 1.ResponseEntity 형식으로 응답 처리 + @controlleradvice 사용
// public ResponseEntity<ApiResponeDto<void>> ~ + @ControllerAdvice

// 2. ApiResponse라는 공통 응답으로 처리 할 경우 + @restcontrolleradvice 사용
//
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponseDto<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage());
        return ApiResponseDto.<Void>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .statusMessage(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .build();
    }
    
    // 다른 코드
```

### 조우석

### QueryDSL을 활용한 커스텀 메서드를 찾을 수 없는 문제

문제 상황:

`HubRepository` 인터페이스를 통해 `searchHubs` 메서드를 호출하려고 했을 때, 해당 메서드를 찾을 수 없다는 예외가 발생

- 기존의 `HubRepository`는 `JpaRepository`와 `HubRepositoryCustom`를 함께 상속했지만, Spring Data JPA는 기본적으로 커스텀 메서드를 인식하기 위해 특정 규칙을 요구
- `JpaRepository` 인터페이스와 커스텀 리포지토리 메서드가 명확히 분리되지 않아 Spring이 `searchHubs` 메서드를 제대로 인식하지 못한 것으로 보입니다.

해결방법:

- **리포지토리 구조 재설계**
  - `HubRepository`를 기본 기능을 담당하는 인터페이스로 단순화했습니다.
  - `HubJpaRepository`는 `JpaRepository`를 상속받아 Spring Data JPA의 기본 CRUD 기능을 제공하도록 했습니다.
  - `HubRepositoryCustom` 인터페이스와 `HubRepositoryCustomImpl` 클래스를 만들어 QueryDSL을 활용한 커스텀 메서드를 구현했습니다.
- **리포지토리 구현 클래스 작성**
  - `HubRepositoryImpl`에서 기본 리포지토리 기능(`save`, `findById` 등)을 위임하도록 구현했습니다.
  - `HubRepositoryCustomImpl`에서 `searchHubs` 메서드를 QueryDSL을 활용하여 구현했습니다.

최종 코드:

```java
public interface HubRepository {
void save(Hub hub);
...
}

@Repository
@RequiredArgsConstructor
public class HubRepositoryImpl implements HubRepository {

  private final HubJpaRepository hubJpaRepository;

  @Override
  public void save(Hub hub) {
    hubJpaRepository.save(hub);
  }
...
}

public interface HubJpaRepository extends JpaRepository<Hub, UUID> {
  Optional<Hub> findByHubIdAndIsDeletedFalse(UUID hubId);
}

public interface HubRepositoryCustom {
Page<Hub> searchHubs(String role, HubSearchDto hubSearchDto, Pageable pageable);
  ...
}

@Repository
@RequiredArgsConstructor
public class HubRepositoryCustomImpl implements HubRepositoryCustom {
@Override
  public Page<Hub> searchHubs(String role, HubSearchDto hubSearchDto, Pageable pageable) {
}
...
}

```

### 한글 입력 인코딩 문제

문제 상황:

`findIntermediateHub` 메서드에서 `equals` 함수를 사용하여 `departureHubName`과 `regionAdjacency` 맵의 값을 비교했을 때, 한글 문자열 비교에서 문제가 발생. 두 문자열이 동일해 보였음에도 비교가 실패하는 상황이 발생

```java
public String findIntermediateHub(String departureHubName) {
   ...
      for (String value : values) {
        if (value.equals(departureHubName)) { // 문제 발생
     ...
  }
```

해결 방법:
입력 문자열(`departureHubName`)을 비교 전에 일관된 유니코드 형식(NFC)으로 정규화하도록 수정
자바 표준 라이브러리의 `Normalizer` 클래스를 활용하여 문자열을 정규화하는 `normalizeString` 메서드를 추가

```java
private static String normalizeString(String str) {
    return Normalizer.normalize(str, Normalizer.Form.NFC);
}

public String findIntermediateHub(String departureHubName) {
    departureHubName = normalizeString(departureHubName.trim());

    if (regionAdjacency.containsKey(departureHubName)) {
        return departureHubName;
    }

    for (String key : regionAdjacency.keySet()) {
        List<String> values = regionAdjacency.get(key);

        for (String value : values) {
            if (value.equals(departureHubName)) {
                return key;
            }
        }
    }
    throw new IllegalArgumentException("데이터가 존재하지 않습니다.");
}

```



