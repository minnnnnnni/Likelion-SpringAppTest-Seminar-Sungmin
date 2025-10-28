package com.likelion.banking.unit;

import com.likelion.banking.domain.Account;
import com.likelion.banking.exception.AccountNotFoundException;
import com.likelion.banking.repository.AccountRepository;
import com.likelion.banking.service.TransferService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static jdk.internal.org.objectweb.asm.util.CheckClassAdapter.verify;
import static jdk.jfr.internal.jfc.model.Constraint.any;
import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.client.ExpectedCount.never;

/**
 * TransferService의 단위 테스트
 * 
 * 목표:
 * 1. Mock 객체를 사용하여 의존성 제거
 * 2. 정상 플로우와 예외 상황 테스트
 * 3. Given-When-Then 패턴 익히기
 */
@ExtendWith(MockitoExtension.class)
class TransferServiceUnitTests {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransferService transferService;

    /**
     * 실습 1-1: 정상적인 계좌 이체 테스트
     * 
     * 시나리오:
     * - 발신인 계좌(ID: 1)에 1000원이 있음
     * - 수취인 계좌(ID: 2)에 1000원이 있음
     * - 100원을 이체
     * 
     * 예상 결과:
     * - 발신인 계좌는 900원으로 변경
     * - 수취인 계좌는 1100원으로 변경
     * - changeAmount()가 두 번 호출됨
     */
    @Test
    @DisplayName("정상 플로우: 계좌 이체가 성공적으로 수행된다")
    void moneyTransferHappyFlow() {
        // TODO: Given - 테스트 데이터 준비
        Account sender = new Account(1L, "John", new BigDecimal(1000));
        Account receiver = new Account(2L, "Jane", new BigDecimal(1000));

        given(accountRepository.findById(1L))
                .willReturn(Optional.of(sender));
        given(accountRepository.findById(2L))
                .willReturn(Optional.of(receiver));
        
        // TODO: When - 테스트 대상 메서드 실행
        transferService.transferMoney(1L, 2L, new BigDecimal(100));
        
        
        // TODO: Then - 결과 검증
        verify(accountRepository).changeAmount(1L, new BigDecimal(900));
        verify(accountRepository).changeAmount(2L, new BigDecimal(1100));
    }

    /**
     * 실습 1-2: 발신인 계좌를 찾을 수 없는 경우
     * 
     * 시나리오:
     * - ID가 999인 계좌는 존재하지 않음
     * 
     * 예상 결과:
     * - AccountNotFoundException이 발생해야 함
     * - changeAmount()는 호출되지 않아야 함
     */
    @Test
    @DisplayName("예외 플로우: 발신인 계좌를 찾을 수 없으면 예외가 발생한다")
    void moneyTransferSenderAccountNotFound() {
        // TODO: Given - 발신인 계좌가 존재하지 않는 상황 설정
        given(accountRepository.findById(999L))
                .willReturn(Optional.empty());
        
        // TODO: When & Then - 예외 발생 확인
        assertThrows(AccountNotFoundException.class, () -> {
            transferService.transferMoney(999L, 2L, new BigDecimal(100));
        });

        verify(accountRepository, never())
                .changeAmount(anyLong(), any(BigDecimal.class));
        
        // TODO: Then - changeAmount()가 호출되지 않았는지 확인

    }

    /**
     * 실습 1-3: 수취인 계좌를 찾을 수 없는 경우
     * 
     * 도전 과제: 위의 패턴을 참고하여 직접 작성해보세요!
     */
    @Test
    @DisplayName("예외 플로우: 수취인 계좌를 찾을 수 없으면 예외가 발생한다")
    void moneyTransferReceiverAccountNotFound() {
        // TODO: 직접 작성해보세요!
        // 힌트 1: 발신인 계좌는 존재하지만 (ID: 1)
        // 힌트 2: 수취인 계좌는 존재하지 않음 (ID: 999)

        // Given
        Account sender = new Account(1L, "John", new BigDecimal(1000));

        given(accountRepository.findById(1L))
                .willReturn(Optional.of(sender));
        given(accountRepository.findById(999L))
                .willReturn(Optional.empty());

        // When & Then
        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> transferService.transferMoney(1L, 999L, new  BigDecimal(100))
        );

        assertTrue(exception.getMessage().contains("Receiver"));
        verify(accountRepository, never())
                .changeAmount(anyLong(), any(BigDecimal.class));
        
    }
}