# 스프링 앱 테스트 세미나 실습 프로젝트

멋쟁이사자처럼 백엔드 세미나 - 스프링 앱 테스트 실습을 위한 프로젝트입니다.

## 🎯 학습 목표

- 단위 테스트(Unit Test) 작성 방법 익히기
- 통합 테스트(Integration Test) 작성 방법 익히기
- Mockito를 사용한 Mock 객체 활용
- Given-When-Then 패턴 이해하기

## 🚀 시작하기

### 사전 요구사항

- Java 17 이상
- IntelliJ IDEA (권장) 또는 다른 IDE
- Git

### 프로젝트 클론

```bash
git clone https://github.com/chaeminyu/spring-seminar-apptest.git
cd spring-seminar-apptest
```

### 프로젝트 실행 확인

```bash
# Gradle 빌드
./gradlew build

# 테스트 실행 (처음엔 실패하는 게 정상입니다!)
./gradlew test
```

## 📚 실습 가이드

1. **[Step 1: 단위 테스트 작성](docs/step1-guide.md)**
   - 정상 플로우 테스트
   - 예외 플로우 테스트
   - Mock 객체 사용법

2. **[Step 2: 통합 테스트 작성](docs/step2-guide.md)**
   - 스프링 컨텍스트 테스트
   - 단위 테스트와의 차이점

3. **[치트시트](docs/cheatsheet.md)**
   - Mockito & JUnit 5 주요 문법 정리

## 🏗️ 프로젝트 구조

```
src/main/java/com/likelion/banking/
├── domain/          # 도메인 모델
├── repository/      # 데이터 접근 계층
├── service/         # 비즈니스 로직
└── exception/       # 커스텀 예외

src/test/java/com/likelion/banking/
├── unit/           # 단위 테스트
└── integration/    # 통합 테스트
```

## 📖 도메인 설명

이 프로젝트는 간단한 **은행 계좌 이체** 시스템을 구현합니다.

### 주요 기능
- 계좌 간 송금 (`TransferService.transferMoney()`)
- 발신인/수취인 계좌 조회
- 계좌 잔액 업데이트

### 시나리오
- **정상 플로우**: 발신인 계좌에서 수취인 계좌로 금액 이체
- **예외 플로우**: 계좌를 찾을 수 없는 경우 예외 발생

## ❓ 문제 해결

### 테스트가 실행되지 않아요
- IntelliJ에서 Gradle 동기화: `File > Sync Project with Gradle Files`
- JDK 버전 확인: `File > Project Structure > Project SDK`

### Mock이 작동하지 않아요
- `@ExtendWith(MockitoExtension.class)` 어노테이션 확인
- `given().willReturn()` 문법 확인

## 🎓 추가 학습 자료

- [JUnit 5 공식 문서](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito 공식 문서](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)

## 📞 도움이 필요하면?

- 세미나 중: 손을 들어주세요!
- 세미나 후: GitHub Issues에 질문 남겨주세요

---

**Happy Testing! 🧪**