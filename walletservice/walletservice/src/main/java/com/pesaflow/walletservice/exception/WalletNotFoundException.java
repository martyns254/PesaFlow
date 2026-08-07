package com.pesaflow.walletservice.exception;

public class WalletNotFoundException extends RuntimeException{
    public WalletNotFoundException(String phoneNumber) {
        super("Wallet not found for phone number: " + phoneNumber);
    }
}
