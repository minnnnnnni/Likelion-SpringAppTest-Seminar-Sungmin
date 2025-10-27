package com.likelion.banking.exception;

/**
 * 계좌를 찾을 수 없을 때 발생하는 예외
 */
public class AccountNotFoundException extends RuntimeException {
    
    public AccountNotFoundException(String message) {
        super(message);
    }
}