# Mockito & JUnit 5 치트시트

빠른 참고를 위한 주요 문법 모음입니다.

---

## 📌 어노테이션

### 테스트 클래스
```java
// 단위 테스트
@ExtendWith(MockitoExtension.class)
class MyUnitTest { }

// 통합 테스트
@SpringBootTest
class MyIntegrationTest { }
```

### 필드
```java
// Mock 객체 생성
@Mock
private MyRepository repository;

// Mock 주입받는 테스트 대상
@InjectMocks
private MyService service;

// 스프링 빈을 Mock으로 대체
@MockBean
private MyRepository repository;

// 스프링 빈 주입
@Autowired
private MyService service;
```

### 테스트 메서드
```java
@Test  // 테스트 메서드
@DisplayName("테스트 설명")  // 테스트 이름
@Disabled("아직 구현 안 됨")  // 테스트 비활성화
```

---

## 📌 Mockito 기본 문법

### Mock 동작 설정
```java
// 기본 패턴
given(mock.method(args)).willReturn(value);

// 예제
given(repository.findById(1L))
    .willReturn(Optional.of(user));

// 여러 번 호출 시 다른 값 반환
given(mock.method())
    .willReturn(value1)
    .willReturn(value2)
    .willReturn(value3);

// 예외 발생시키기
given(mock.method())
    .willThrow(new RuntimeException("Error"));

// void 메서드 예외 발생
willThrow(new RuntimeException()).given(mock).voidMethod();

// 아무 것도 안 함
willDoNothing().given(mock).voidMethod();
```

### 메서드 호출 검증
```java
// 한 번 호출되었는지 확인 (기본)
verify(mock).method();
verify(mock, times(1)).method();

// N번 호출 확인
verify(mock, times(3)).method();

// 최소 N번 호출
verify(mock, atLeast(2)).method();

// 최대 N번 호출
verify(mock, atMost(5)).method();

// 호출되지 않았는지 확인
verify(mock, never()).method();

// 특정 값으로 호출되었는지
verify(mock).method(eq(expectedValue));

// 어떤 값이든 상관없이
verify(mock).method(any());
verify(mock).method(anyString());
verify(mock).method(anyInt());
verify(mock).method(anyLong());
verify(mock).method(any(MyClass.class));
```

### ArgumentCaptor (고급)
```java
// 전달된 인자를 캡처
ArgumentCaptor<MyClass> captor = ArgumentCaptor.forClass(MyClass.class);
verify(mock).method(captor.capture());

MyClass captured = captor.getValue();
assertEquals(expected, captured.getSomething());
```

---

## 📌 JUnit 5 Assertions

### 기본 비교
```java
// 같은지 확인
assertEquals(expected, actual);
assertEquals(expected, actual, "실패 메시지");

// 다른지 확인
assertNotEquals(unexpected, actual);

// 참/거짓 확인
assertTrue(condition);
assertFalse(condition);

// null 확인
assertNull(object);
assertNotNull(object);

// 같은 객체인지 (동일성)
assertSame(expected, actual);
assertNotSame(unexpected, actual);
```

### 예외 검증
```java
// 예외 발생 확인
assertThrows(ExceptionClass.class, () -> {
    // 예외를 발생시킬 코드
});

// 예외 메시지도 확인
Exception exception = assertThrows(MyException.class, () -> {
    service.throwException();
});
assertEquals("Expected message", exception.getMessage());

// 예외가 발생하지 않는지 확인
assertDoesNotThrow(() -> {
    service.normalMethod();
});
```

### 컬렉션 검증
```java
// 배열/리스트 비교
assertArrayEquals(expectedArray, actualArray);
assertIterableEquals(expectedList, actualList);

// 컬렉션 크기
assertEquals(3, list.size());
assertTrue(list.isEmpty());
```

### 복합 검증
```java
// 여러 검증을 한번에 (하나 실패해도 나머지 실행)
assertAll(
    () -> assertEquals(expected1, actual1),
    () -> assertEquals(expected2, actual2),
    () -> assertTrue(condition)
);

// 시간 제한
assertTimeout(Duration.ofSeconds(1), () -> {
    // 1초 안에 완료되어야 하는 코드
});
```

---

## 📌 Given-When-Then 패턴

### 기본 구조
```java
@Test
void testExample() {
    // Given: 테스트 준비 (Setup)
    MyObject obj = new MyObject();
    given(repository.findById(1L)).willReturn(Optional.of(obj));
    
    // When: 테스트 실행 (Exercise)
    Result result = service.doSomething(1L);
    
    // Then: 결과 검증 (Verify)
    assertNotNull(result);
    assertEquals(expected, result.getValue());
    verify(repository).findById(1L);
}
```

### 예외 테스트
```java
@Test
void testException() {
    // Given
    given(repository.findById(999L)).willReturn(Optional.empty());
    
    // When & Then
    assertThrows(NotFoundException.class, () -> {
        service.doSomething(999L);
    });
    
    verify(repository).findById(999L);
    verify(repository, never()).save(any());
}
```

---

## 📌 자주 사용하는 패턴

### 1. Optional 처리
```java
// Optional이 값을 가진 경우
given(repository.findById(1L))
    .willReturn(Optional.of(entity));

// Optional이 비어있는 경우
given(repository.findById(999L))
    .willReturn(Optional.empty());
```

### 2. void 메서드 테스트
```java
// void 메서드 호출 검증
@Test
void testVoidMethod() {
    // When
    service.voidMethod();
    
    // Then
    verify(repository).save(any());
}

// void 메서드 예외 발생
willThrow(new RuntimeException()).given(repository).delete(anyLong());
```

### 3. 리스트 반환
```java
// 빈 리스트
given(repository.findAll()).willReturn(Collections.emptyList());

// 값이 있는 리스트
given(repository.findAll()).willReturn(Arrays.asList(item1, item2));
```

### 4. 여러 Mock 설정
```java
@Test
void multipleMocks() {
    // Given
    given(repository1.findById(1L)).willReturn(Optional.of(entity1));
    given(repository2.findById(2L)).willReturn(Optional.of(entity2));
    given(service2.process(any())).willReturn(result);
    
    // When
    Result result = mainService.complexOperation(1L, 2L);
    
    // Then
    verify(repository1).findById(1L);
    verify(repository2).findById(2L);
    verify(service2).process(any());
}
```

---

## 📌 실전 팁

### 테스트 이름 짓기
```java
// ❌ 나쁜 예
@Test
void test1() { }

// ✅ 좋은 예
@Test
@DisplayName("사용자 ID로 조회 시 사용자를 반환한다")
void findById_WithValidId_ReturnsUser() { }

@Test
@DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
void findById_WithInvalidId_ThrowsException() { }
```

### 테스트 데이터 생성
```java
// 테스트용 빌더 패턴
private Account createTestAccount(Long id, BigDecimal amount) {
    return new Account(id, "Test User", amount);
}

@Test
void test() {
    Account account = createTestAccount(1L, new BigDecimal(1000));
    // ...
}
```

### 공통 설정
```java
@ExtendWith(MockitoExtension.class)
class MyTest {
    @Mock
    private MyRepository repository;
    
    @InjectMocks
    private MyService service;
    
    // 각 테스트 전에 실행
    @BeforeEach
    void setUp() {
        // 공통 설정
    }
    
    // 각 테스트 후에 실행
    @AfterEach
    void tearDown() {
        // 정리 작업
    }
}
```

---

## 📌 자주 하는 실수

### 1. Mock 설정 잊어버림
```java
// ❌ 잘못된 코드
@Test
void test() {
    // Mock 설정 없이 바로 호출
    service.method();  // NullPointerException!
}

// ✅ 올바른 코드
@Test
void test() {
    given(repository.findById(1L)).willReturn(Optional.of(entity));
    service.method();
}
```

### 2. BigDecimal 비교
```java
// ❌ 잘못된 코드
new BigDecimal("100") == new BigDecimal("100")  // false!

// ✅ 올바른 코드
new BigDecimal("100").equals(new BigDecimal("100"))  // true
```

### 3. verify() 위치
```java
// ❌ 잘못된 코드
@Test
void test() {
    verify(repository).save(any());  // 아직 호출 안 했는데!
    service.method();
}

// ✅ 올바른 코드
@Test
void test() {
    service.method();
    verify(repository).save(any());  // 호출 후에 검증
}
```

---

## 📚 추가 학습 자료

- [JUnit 5 공식 문서](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito 공식 문서](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ (더 읽기 쉬운 assertion)](https://assertj.github.io/doc/)

---

**이 치트시트를 북마크해두고 필요할 때마다 참고하세요! 📖**