package com.likelion.banking.integration;

import com.likelion.banking.domain.Account;
import com.likelion.banking.repository.AccountRepository;
import com.likelion.banking.service.TransferService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.BDDMockito.*;

/**
 * TransferService의 통합 테스트
 * 
 * 단위 테스트와의 차이점:
 * - @SpringBootTest로 스프링 컨텍스트가 실행됨
 * - @Autowired로 실제 빈을 주입받음
 * - @MockBean으로 스프링 컨텍스트의 빈을 Mock으로 대체
 * 
 * 목표:
 * - 스프링 컨텍스트 내에서 테스트 수행
 * - @Transactional 등 스프링 기능이 정상 작동하는지 확인
 */
@SpringBootTest
class TransferServiceIntegrationTests {

    @MockBean
    private AccountRepository accountRepository;

    @Autowired
    private TransferService transferService;

    /**
     * 실습 2-1: 스프링 통합 테스트
     * 
     * 단위 테스트와 동일한 시나리오지만,
     * 이번에는 스프링 컨텍스트가 실행된 상태에서 테스트합니다.
     */
    @Test
    @DisplayName("통합 테스트: 스프링 컨텍스트 내에서 계좌 이체가 수행된다")
    void transferMoneyIntegrationTest() {
        // TODO: Given - 테스트 데이터 준비
        // 힌트: 단위 테스트와 동일하게 작성하면 됩니다
        
        
        // TODO: When - 메서드 실행
        
        
        // TODO: Then - 결과 검증
        
    }

    /**
     * 실습 2-2: 단위 테스트와 실행 시간 비교 (선택)
     * 
     * 이 테스트는 작성하지 않아도 됩니다.
     * 대신 전체 테스트 실행 시간을 비교해보세요!
     */
}