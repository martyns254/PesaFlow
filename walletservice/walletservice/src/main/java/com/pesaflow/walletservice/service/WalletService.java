package com.pesaflow.walletservice.service;

import com.pesaflow.walletservice.entity.Wallet;
import com.pesaflow.walletservice.entity.WalletTransaction;
import com.pesaflow.walletservice.exception.InsufficientBalanceException;
import com.pesaflow.walletservice.exception.WalletNotFoundException;
import com.pesaflow.walletservice.repository.WalletRepository;
import com.pesaflow.walletservice.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    public Wallet createWallet(String phoneNumber) {
        Wallet wallet = new Wallet();
        wallet.setPhoneNumber(phoneNumber);
        wallet.setBalance(0.0);
        wallet.setCreatedAt(LocalDateTime.now());
        return walletRepository.save(wallet);
    }
    public Wallet creditWallet(String phoneNumber, Double amount) {
        Wallet wallet = walletRepository.findByPhoneNumber(phoneNumber);

        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);

        WalletTransaction transaction = new WalletTransaction();
        transaction.setWalletId(wallet.getId());
        transaction.setType("CREDIT");
        transaction.setAmount(amount);
        transaction.setCreatedAt(LocalDateTime.now());
        walletTransactionRepository.save(transaction);

        return wallet;
    }
    public Wallet debitWallet(String phoneNumber, Double amount) {
        Wallet wallet = walletRepository.findByPhoneNumber(phoneNumber);

        if (wallet.getBalance() < amount) {
            throw new InsufficientBalanceException(phoneNumber);
        }

        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);

        WalletTransaction transaction = new WalletTransaction();
        transaction.setWalletId(wallet.getId());
        transaction.setType("DEBIT");
        transaction.setAmount(amount);
        transaction.setCreatedAt(LocalDateTime.now());
        walletTransactionRepository.save(transaction);

        return wallet;
    }
    public Wallet getWallet(String phoneNumber) {
        Wallet wallet = walletRepository.findByPhoneNumber(phoneNumber);
        if (wallet == null) {
            throw new WalletNotFoundException(phoneNumber);
        }
        return wallet;
    }
}
