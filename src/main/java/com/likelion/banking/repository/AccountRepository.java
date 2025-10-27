package com.likelion.banking.repository;

import com.likelion.banking.domain.Account;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * 계좌 정보를 관리하는 Repository 인터페이스
 * 
 * 실제 프로젝트에서는 JPA Repository나 JDBC를 사용하지만,
 * 테스트 실습을 위해 간단한 인터페이스로 구성했습니다.
 */
public interface AccountRepository {
    
    /**
     * ID로 계좌 정보 조회
     * 
     * @param id 계좌 ID
     * @return 계좌 정보 (Optional)
     */
    Optional<Account> findById(Long id);
    
    /**
     * 계좌 금액 변경
     * 
     * @param id 계좌 ID
     * @param newAmount 변경할 금액
     */
    void changeAmount(Long id, BigDecimal newAmount);
}