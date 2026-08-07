package com.pesaflow.walletservice.exception;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String phoneNumber) {
        super("Insufficient balance for wallet: " + phoneNumber);
    }
}