package com.pesaflow.walletservice.controller;

import com.pesaflow.walletservice.entity.Wallet;
import com.pesaflow.walletservice.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @PostMapping
    public Wallet createWallet(@RequestParam String phoneNumber) {
        return walletService.createWallet(phoneNumber);
    }
    @GetMapping("/{phoneNumber}")
    public Wallet getWallet(@PathVariable String phoneNumber) {
        return walletService.getWallet(phoneNumber);
    }
    @PostMapping("/credit")
    public Wallet creditWallet(@RequestParam String phoneNumber, @RequestParam Double amount) {
        return walletService.creditWallet(phoneNumber, amount);
    }

    @PostMapping("/debit")
    public Wallet debitWallet(@RequestParam String phoneNumber, @RequestParam Double amount) {
        return walletService.debitWallet(phoneNumber, amount);
    }
}
