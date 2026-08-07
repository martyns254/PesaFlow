package com.pesaflow.paymentservice.service;

import com.pesaflow.paymentservice.dto.PaymentRequest;
import com.pesaflow.paymentservice.entity.Payment;
import com.pesaflow.paymentservice.exception.PaymentNotFoundException;
import com.pesaflow.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service

public class PaymentService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PaymentRepository paymentRepository;

    public Payment initiatePayment(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setPhoneNumber(request.getPhoneNumber());
        payment.setAmount(request.getAmount());
        payment.setStatus("PENDING");
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        try {
            String walletUrl = "http://localhost:8081/api/wallets/credit?phoneNumber="
                    + request.getPhoneNumber() + "&amount=" + request.getAmount();
            restTemplate.postForObject(walletUrl, null, String.class);

            payment.setStatus("SUCCESS");
        } catch (Exception e) {
            payment.setStatus("FAILED");
        }

        paymentRepository.save(payment);
        return payment;
    }
    public Payment getpayment(Long id) {
        return paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException(id));
    }
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
