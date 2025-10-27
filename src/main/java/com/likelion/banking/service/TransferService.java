package com.likelion.banking.service;

import com.likelion.banking.domain.Account;
import com.likelion.banking.exception.AccountNotFoundException;
import com.likelion.banking.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 계좌 이체 비즈니스 로직을 처리하는 서비스
 */
@Service
public class TransferService {
    
    private final AccountRepository accountRepository;
    
    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    /**
     * 계좌 이체를 수행하는 메서드
     * 
     * @param idSender 발신인 계좌 ID
     * @param idReceiver 수취인 계좌 ID
     * @param amount 이체 금액
     * @throws AccountNotFoundException 계좌를 찾을 수 없는 경우
     */
    @Transactional
    public void transferMoney(Long idSender, Long idReceiver, BigDecimal amount) {
        // 1. 발신인 계좌 조회
        Account sender = accountRepository.findById(idSender)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Sender account not found with id: " + idSender));
        
        // 2. 수취인 계좌 조회
        Account receiver = accountRepository.findById(idReceiver)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Receiver account not found with id: " + idReceiver));
        
        // 3. 새로운 금액 계산
        BigDecimal senderNewAmount = sender.getAmount().subtract(amount);
        BigDecimal receiverNewAmount = receiver.getAmount().add(amount);
        
        // 4. 금액 업데이트
        accountRepository.changeAmount(idSender, senderNewAmount);
        accountRepository.changeAmount(idReceiver, receiverNewAmount);
    }
}