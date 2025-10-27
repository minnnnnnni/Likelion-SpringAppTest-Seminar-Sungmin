# 실습 2: 통합 테스트 작성하기

## 🎯 학습 목표
- 스프링 통합 테스트의 개념 이해
- 단위 테스트와 통합 테스트의 차이점 파악
- `@SpringBootTest`와 `@MockBean` 사용법 익히기

---

## 🤔 단위 테스트 vs 통합 테스트

### 단위 테스트
```java
@ExtendWith(MockitoExtension.class)  // Mockito만 사용
class TransferServiceUnitTests {
    @Mock  // 순수 Mock 객체
    private AccountRepository accountRepository;
    
    @InjectMocks  // 직접 인스턴스 생성
    private TransferService transferService;
}
```

**특징:**
- ⚡ 매우 빠름 (밀리초 단위)
- 🔸 스프링 컨텍스트 없음
- 🎯 순수 자바 코드만 테스트

### 통합 테스트
```java
@SpringBootTest  // 스프링 컨텍스트 실행
class TransferServiceIntegrationTests {
    @MockBean  // 스프링 빈을 Mock으로 대체
    private AccountRepository accountRepository;
    
    @Autowired  // 스프링이 주입
    private TransferService transferService;
}
```

**특징:**
- ⏱️ 상대적으로 느림 (스프링 컨텍스트 로딩)
- 🌐 실제 스프링 환경과 동일
- 🔗 스프링 기능 검증 가능 (@Transactional, 의존성 주입 등)

---

## 📝 실습 2-1: 스프링 통합 테스트 작성

### 목표
스프링 컨텍스트 내에서 계좌 이체가 정상 작동하는지 확인합니다.

### 왜 통합 테스트가 필요한가?

**시나리오 1: 의존성 주입 문제**
```java
@Service
public class TransferService {
    // 실수로 @Autowired를 빼먹음!
    private AccountRepository accountRepository;
}
```
→ 단위 테스트는 통과하지만, 실제 앱은 실행 안 됨!  
→ 통합 테스트가 이런 문제를 잡아냅니다.

**시나리오 2: 트랜잭션 문제**
```java
@Transactional  // 이게 제대로 작동하는지 확인
public void transferMoney(...) {
    // ...
}
```
→ 단위 테스트는 `@Transactional`을 무시합니다.  
→ 통합 테스트는 실제로 트랜잭션이 작동하는지 확인합니다.

### 코드 작성
```java
@SpringBootTest
class TransferServiceIntegrationTests {

    @MockBean
    private AccountRepository accountRepository;

    @Autowired
    private TransferService transferService;

    @Test
    @DisplayName("통합 테스트: 스프링 컨텍스트 내에서 계좌 이체가 수행된다")
    void transferMoneyIntegrationTest() {
        // Given
        Account sender = new Account(1L, "John", new BigDecimal(1000));
        Account receiver = new Account(2L, "Jane", new BigDecimal(1000));

        given(accountRepository.findById(1L))
                .willReturn(Optional.of(sender));
        given(accountRepository.findById(2L))
                .willReturn(Optional.of(receiver));

        // When
        transferService.transferMoney(1L, 2L, new BigDecimal(100));

        // Then
        verify(accountRepository).changeAmount(1L, new BigDecimal(900));
        verify(accountRepository).changeAmount(2L, new BigDecimal(1100));
    }
}
```

**💡 설명:**
- `@SpringBootTest`: 실제 스프링 애플리케이션처럼 컨텍스트를 실행합니다
- `@MockBean`: 스프링 빈을 Mock으로 대체합니다
- `@Autowired`: 스프링이 자동으로 빈을 주입합니다

### 단위 테스트와의 차이점

| 항목 | 단위 테스트 | 통합 테스트 |
|-----|-----------|-----------|
| 스프링 컨텍스트 | ❌ 없음 | ✅ 있음 |
| 실행 속도 | ⚡ 매우 빠름 | ⏱️ 느림 |
| `@Autowired` | ❌ 사용 불가 | ✅ 사용 가능 |
| `@Transactional` | ❌ 무시됨 | ✅ 작동함 |
| 의존성 주입 검증 | ❌ 불가 | ✅ 가능 |

---

## 📊 실습 2-2: 실행 시간 비교 실험

### 실험 방법

1. **모든 단위 테스트 실행**
```bash
   ./gradlew test --tests "*.unit.*"
```
   → 실행 시간 기록

2. **모든 통합 테스트 실행**
```bash
   ./gradlew test --tests "*.integration.*"
```
   → 실행 시간 기록

3. **비교하기**
   - 단위 테스트: 약 X초
   - 통합 테스트: 약 Y초
   - 차이: 약 Z배

### 왜 차이가 날까?

**단위 테스트:**
```
테스트 시작 → 테스트 실행 → 끝
(밀리초 단위)
```

**통합 테스트:**
```
스프링 컨텍스트 로딩 → 빈 생성 → 의존성 주입 → 테스트 실행 → 끝
(수 초 소요)
```

---

## 💡 실무 베스트 프랙티스

### 1. 테스트 피라미드 전략
```
        /\
       /E2E\        ← 소수만 작성
      /------\
     / 통합테스트 \    ← 중요한 것만
    /------------\
   /  단위 테스트   \  ← 대부분 여기!
  /----------------\
```

**비율 예시:**
- 단위 테스트: 70%
- 통합 테스트: 20%
- E2E 테스트: 10%

### 2. 무엇을 단위 테스트로?

✅ **단위 테스트로 작성:**
- 비즈니스 로직 (계산, 검증 등)
- 알고리즘
- 유틸리티 함수
- 예외 처리 로직

❌ **단위 테스트 불필요:**
- Getter/Setter
- 단순 DTO
- Configuration 클래스

### 3. 무엇을 통합 테스트로?

✅ **통합 테스트로 작성:**
- 스프링 기능 사용 여부 (DI, AOP, Transaction)
- 여러 레이어 간 상호작용
- 설정(Configuration) 검증

❌ **통합 테스트 과용 금지:**
- 모든 비즈니스 로직 시나리오
  → 이건 단위 테스트로!

### 4. 예시: 실제 프로젝트 구조
```java
// 단위 테스트 - 많이 작성
@ExtendWith(MockitoExtension.class)
class TransferServiceUnitTests {
    // ✅ 정상 플로우
    // ✅ 발신인 계좌 없음
    // ✅ 수취인 계좌 없음
    // ✅ 잔액 부족
    // ✅ 음수 금액
    // ... (시나리오별로 10개+)
}

// 통합 테스트 - 핵심만 작성
@SpringBootTest
class TransferServiceIntegrationTests {
    // ✅ 정상 플로우 (스프링 통합 확인)
    // (나머지는 단위 테스트로 충분)
}
```

---

## 🔍 통합 테스트 디버깅

### 스프링 컨텍스트 로딩 실패

**에러 예시:**
```
Failed to load ApplicationContext
...
Field accountRepository in ... required a bean of type '...' that could not be found.
```

**원인:**
- 필요한 빈이 없음
- 의존성 설정 오류

**해결:**
1. `@MockBean`으로 필요한 의존성 추가
2. 또는 `@SpringBootTest(classes = {...})`로 필요한 것만 로드

### 테스트가 너무 느림

**해결 방법:**
1. **불필요한 통합 테스트 제거**
   - 단위 테스트로 충분한 것들

2. **테스트 컨텍스트 재사용**
   - 같은 설정의 테스트는 컨텍스트 재사용됨
   - 다른 설정마다 새로 로딩됨

3. **프로파일 사용**
```java
   @SpringBootTest
   @ActiveProfiles("test")  // test용 가벼운 설정
```

---

## ✅ 체크리스트

실습을 완료했다면 체크해보세요:

- [ ] `transferMoneyIntegrationTest` 테스트 통과
- [ ] `@SpringBootTest` 이해
- [ ] `@MockBean` vs `@Mock` 차이 이해
- [ ] 단위 테스트와 통합 테스트의 차이 이해
- [ ] 실행 시간 비교 완료
- [ ] 언제 무엇을 사용할지 판단 가능

---

## 🎓 추가 학습 주제

시간이 더 있다면 다음 주제를 탐구해보세요:

1. **테스트 컨테이너 (Testcontainers)**
   - 실제 데이터베이스를 Docker로 띄워서 테스트
   - H2 대신 실제 MySQL/PostgreSQL 사용

2. **슬라이스 테스트 (Slice Tests)**
   - `@WebMvcTest`: Controller만 테스트
   - `@DataJpaTest`: Repository만 테스트
   - 전체 컨텍스트보다 빠름

3. **테스트 커버리지**
   - Jacoco로 코드 커버리지 측정
   - 목표: 70-80% 이상

---

**축하합니다! 🎉 모든 실습을 완료했습니다!**

더 궁금한 점은 [치트시트](cheatsheet.md)를 참고하거나 질문해주세요!