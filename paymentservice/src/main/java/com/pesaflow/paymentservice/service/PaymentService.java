package com.pesaflow.paymentservice.service;

import com.pesaflow.paymentservice.daraja.DarajaService;
import com.pesaflow.paymentservice.dto.PaymentRequest;
import com.pesaflow.paymentservice.entity.Payment;
import com.pesaflow.paymentservice.exception.PaymentNotFoundException;
import com.pesaflow.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service

public class PaymentService {
    @Value("${pesaflow.api.key}")
    private String apiKey;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private DarajaService darajaService;
    public Payment initiatePayment(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setPhoneNumber(request.getPhoneNumber());
        payment.setAmount(request.getAmount());
        payment.setStatus("PENDING");
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        try {
            Map stkResponse = darajaService.initiateStkPush(request.getPhoneNumber(), request.getAmount());
            String checkoutRequestId = (String) stkResponse.get("CheckoutRequestID");
            payment.setCheckoutRequestId(checkoutRequestId);
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
    public void processCallback(String checkoutRequestId, Integer resultCode) {
        Payment payment = paymentRepository.findByCheckoutRequestId(checkoutRequestId);

        if (payment == null) {
            return;
        }

        if (resultCode == 0) {
            payment.setStatus("SUCCESS");
            paymentRepository.save(payment);

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("X-API-KEY", apiKey);
                HttpEntity<Void> entity = new HttpEntity<>(headers);

                String walletUrl = "http://localhost:8081/api/wallets/credit?phoneNumber="
                        + payment.getPhoneNumber() + "&amount=" + payment.getAmount();
                restTemplate.exchange(walletUrl, HttpMethod.POST, entity, String.class);
            } catch (Exception e) {
                System.out.println("Wallet credit failed after successful payment: " + e.getMessage());
            }
        } else {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
        }
    }
}
