package com.pesaflow.walletservice.service;

import com.pesaflow.walletservice.entity.Wallet;
import com.pesaflow.walletservice.entity.WalletTransaction;
import com.pesaflow.walletservice.exception.InsufficientBalanceException;
import com.pesaflow.walletservice.repository.WalletRepository;
import com.pesaflow.walletservice.repository.WalletTransactionRepository;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletTransactionRepository walletTransactionRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    void debitWallet_throwsWhenBalanceInsufficient(){
        Wallet wallet = new Wallet();
        wallet.setPhoneNumber("254712345678");
        wallet.setBalance(100.0);

        when(walletRepository.findByPhoneNumber("254712345678")).thenReturn(wallet);


        assertThrows(InsufficientBalanceException.class, () -> {
            walletService.debitWallet("254712345678", 500.0);
        });
    }
}
