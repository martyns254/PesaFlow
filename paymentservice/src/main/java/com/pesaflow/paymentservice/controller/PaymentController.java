package com.pesaflow.paymentservice.controller;

import com.pesaflow.paymentservice.daraja.DarajaService;
import com.pesaflow.paymentservice.dto.PaymentRequest;
import com.pesaflow.paymentservice.entity.Payment;
import com.pesaflow.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/payments")

public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private DarajaService darajaService;

    @PostMapping
    public Payment initiatePayment(@Valid @RequestBody PaymentRequest request) {
        return paymentService.initiatePayment(request);
    }

    @PostMapping("/callback")

    public String handleCallback(@RequestBody Map<String, Object> callbackData) {
        System.out.println("Daraja callback received: " + callbackData);

        Map<String, Object> body = (Map<String, Object>) callbackData.get("Body");
        Map<String, Object> stkCallback = (Map<String, Object>) body.get("stkCallback");

        String checkoutRequestId = (String) stkCallback.get("CheckoutRequestID");
        Integer resultCode = (Integer) stkCallback.get("ResultCode");

        paymentService.processCallback(checkoutRequestId, resultCode);
        return "OK";
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
