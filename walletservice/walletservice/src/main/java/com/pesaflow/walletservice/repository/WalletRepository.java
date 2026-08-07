package com.pesaflow.walletservice.repository;

import com.pesaflow.walletservice.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findByPhoneNumber(String phoneNumber);
}
