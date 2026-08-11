package com.pesaflow.paymentservice.repository;

import com.pesaflow.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByCheckoutRequestId(String checkoutRequestId);
}
