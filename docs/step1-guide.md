# 실습 1: 단위 테스트 작성하기

## 🎯 학습 목표
- Given-When-Then 패턴 이해
- Mockito를 사용한 Mock 객체 생성 및 제어
- 정상 플로우와 예외 플로우 테스트

---

## 📝 실습 1-1: 정상 플로우 테스트

### 목표
계좌 이체가 정상적으로 수행되는지 테스트합니다.

### 시나리오
- 발신인 계좌(ID: 1)에 1000원이 있음
- 수취인 계좌(ID: 2)에 1000원이 있음
- 100원을 이체
- 결과: 발신인 900원, 수취인 1100원

### Step 1: Given - 테스트 데이터 준비
```java
// 1. 발신인 계좌 생성
Account sender = new Account(1L, "John", new BigDecimal(1000));

// 2. 수취인 계좌 생성
Account receiver = new Account(2L, "Jane", new BigDecimal(1000));

// 3. Mock 동작 설정
given(accountRepository.findById(1L))
    .willReturn(Optional.of(sender));
    
given(accountRepository.findById(2L))
    .willReturn(Optional.of(receiver));
```

**💡 설명:**
- `given()`: Mock 객체의 동작을 설정합니다
- `willReturn()`: 특정 값을 반환하도록 설정합니다
- `Optional.of()`: 값이 있는 Optional 객체를 생성합니다

### Step 2: When - 메서드 실행
```java
transferService.transferMoney(1L, 2L, new BigDecimal(100));
```

**💡 설명:**
- 테스트 대상 메서드를 실행합니다
- 실제로 이 메서드가 실행되면 Mock의 `findById()`가 호출됩니다

### Step 3: Then - 결과 검증
```java
// changeAmount()가 올바른 값으로 호출되었는지 확인
verify(accountRepository).changeAmount(1L, new BigDecimal(900));
verify(accountRepository).changeAmount(2L, new BigDecimal(1100));
```

**💡 설명:**
- `verify()`: Mock 객체의 메서드가 호출되었는지 검증합니다
- 발신인은 1000 - 100 = 900원이 되어야 합니다
- 수취인은 1000 + 100 = 1100원이 되어야 합니다

### 전체 코드
```java
@Test
@DisplayName("정상 플로우: 계좌 이체가 성공적으로 수행된다")
void moneyTransferHappyFlow() {
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
```

---

## 📝 실습 1-2: 예외 플로우 테스트

### 목표
발신인 계좌를 찾을 수 없을 때 예외가 발생하는지 테스트합니다.

### 시나리오
- ID가 999인 계좌는 존재하지 않음
- 계좌 이체 시도
- 결과: `AccountNotFoundException` 발생

### Step 1: Given - 계좌가 없는 상황 설정
```java
given(accountRepository.findById(999L))
    .willReturn(Optional.empty());
```

**💡 설명:**
- `Optional.empty()`: 값이 없는 Optional 객체를 반환합니다
- 이렇게 하면 계좌를 찾을 수 없는 상황을 시뮬레이션합니다

### Step 2 & 3: When & Then - 예외 발생 확인
```java
assertThrows(AccountNotFoundException.class, () -> {
    transferService.transferMoney(999L, 2L, new BigDecimal(100));
});

// changeAmount()가 호출되지 않았는지 확인
verify(accountRepository, never())
    .changeAmount(anyLong(), any(BigDecimal.class));
```

**💡 설명:**
- `assertThrows()`: 특정 예외가 발생하는지 검증합니다
- `never()`: 메서드가 절대 호출되지 않았음을 검증합니다
- `anyLong()`, `any()`: 어떤 값이든 상관없이 체크합니다

### 전체 코드
```java
@Test
@DisplayName("예외 플로우: 발신인 계좌를 찾을 수 없으면 예외가 발생한다")
void moneyTransferSenderAccountNotFound() {
    // Given
    given(accountRepository.findById(999L))
            .willReturn(Optional.empty());

    // When & Then
    assertThrows(AccountNotFoundException.class, () -> {
        transferService.transferMoney(999L, 2L, new BigDecimal(100));
    });

    verify(accountRepository, never())
            .changeAmount(anyLong(), any(BigDecimal.class));
}
```

---

## 📝 실습 1-3: 수취인 계좌 예외 테스트 (도전 과제)

### 목표
수취인 계좌를 찾을 수 없을 때 예외가 발생하는지 테스트합니다.

### 시나리오
- 발신인 계좌(ID: 1)는 존재함
- 수취인 계좌(ID: 999)는 존재하지 않음
- 계좌 이체 시도
- 결과: `AccountNotFoundException` 발생

### 힌트
```java
@Test
@DisplayName("예외 플로우: 수취인 계좌를 찾을 수 없으면 예외가 발생한다")
void moneyTransferReceiverAccountNotFound() {
    // Given
    Account sender = new Account(1L, "John", new BigDecimal(1000));
    
    // 1. 발신인 계좌는 존재하도록 설정
    // given(accountRepository.findById(1L))...
    
    // 2. 수취인 계좌는 없도록 설정
    // given(accountRepository.findById(999L))...
    
    // When & Then
    // assertThrows(...);
    
    // verify(...);
}
```

---

## 💡 자주하는 실수

### 1. Mock 설정을 잊어버림

❌ **잘못된 코드**
```java
Account sender = new Account();
// Mock 설정 없이 바로 메서드 호출
transferService.transferMoney(1L, 2L, new BigDecimal(100));
```

✅ **올바른 코드**
```java
Account sender = new Account();
given(accountRepository.findById(1L)).willReturn(Optional.of(sender));
transferService.transferMoney(1L, 2L, new BigDecimal(100));
```

### 2. BigDecimal 비교

❌ **잘못된 코드**
```java
new BigDecimal(100) == new BigDecimal(100)  // false!
```

✅ **올바른 코드**
```java
new BigDecimal(100).equals(new BigDecimal(100))  // true
```

### 3. verify() 순서

✅ **순서는 상관없습니다**
```java
// 이렇게 해도 되고
verify(accountRepository).changeAmount(1L, new BigDecimal(900));
verify(accountRepository).changeAmount(2L, new BigDecimal(1100));

// 이렇게 해도 됩니다
verify(accountRepository).changeAmount(2L, new BigDecimal(1100));
verify(accountRepository).changeAmount(1L, new BigDecimal(900));
```

---

## 🔍 디버깅 팁

### 테스트가 실패할 때

1. **에러 메시지를 꼼꼼히 읽기**
```
   org.mockito.exceptions.verification.WantedButNotInvoked:
   Wanted but not invoked:
   accountRepository.changeAmount(1L, 900);
```
   → `changeAmount()`가 호출되지 않았다는 의미

2. **중간 값 확인하기**
```java
   System.out.println("Sender amount: " + sender.getAmount());
   System.out.println("Receiver amount: " + receiver.getAmount());
```

3. **디버거 활용하기**
   - IntelliJ에서 테스트 메서드 왼쪽에 중단점(breakpoint) 설정
   - 우클릭 → "Debug 'testName()'"
   - F8로 한 줄씩 실행하며 값 확인

### 흔한 에러 메시지

| 에러 메시지 | 의미 | 해결 방법 |
|---------|------|---------|
| `WantedButNotInvoked` | 메서드가 호출되지 않음 | Mock 설정 확인 |
| `NullPointerException` | null 객체 사용 | 객체 생성 여부 확인 |
| `ArgumentsAreDifferent` | 다른 인자로 호출됨 | 인자 값 확인 |

---

## ✅ 체크리스트

실습을 완료했다면 체크해보세요:

- [ ] `moneyTransferHappyFlow` 테스트 통과
- [ ] `moneyTransferSenderAccountNotFound` 테스트 통과
- [ ] `moneyTransferReceiverAccountNotFound` 테스트 작성 및 통과
- [ ] Given-When-Then 패턴 이해
- [ ] Mock 객체 사용법 이해
- [ ] `given()`, `verify()` 문법 이해

---

**다음 단계:** [Step 2: 통합 테스트 작성](step2-guide.md)