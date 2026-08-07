package com.pesaflow.paymentservice.controller;

import com.pesaflow.paymentservice.dto.PaymentRequest;
import com.pesaflow.paymentservice.entity.Payment;
import com.pesaflow.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@RestController
@RequestMapping("/api/payments")

public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @PostMapping
    public Payment initiatePayment(@Valid @RequestBody PaymentRequest request) {
        return paymentService.initiatePayment(request);
    }
    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable Long id) {
        return paymentService.getpayment(id);
    }
    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }
}
