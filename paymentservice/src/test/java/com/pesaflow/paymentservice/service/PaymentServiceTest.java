package com.pesaflow.paymentservice.service;

import com.pesaflow.paymentservice.daraja.DarajaService;
import com.pesaflow.paymentservice.dto.PaymentRequest;
import com.pesaflow.paymentservice.entity.Payment;
import com.pesaflow.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private DarajaService darajaService;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void initiatePayment_savesPaymentWithPendingStatus() {
        PaymentRequest request = new PaymentRequest();
        request.setPhoneNumber("254712345678");
        request.setAmount(500.0);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> fakeStkResponse = new HashMap<>();
        fakeStkResponse.put("CheckoutRequestID", "ws_CO_test123");
        when(darajaService.initiateStkPush(anyString(), anyDouble())).thenReturn(fakeStkResponse);

        Payment result = paymentService.initiatePayment(request);

        assertEquals("PENDING", result.getStatus());
        assertEquals("254712345678", result.getPhoneNumber());
        assertEquals(500.0, result.getAmount());
        assertEquals("ws_CO_test123", result.getCheckoutRequestId());

        verify(paymentRepository, times(2)).save(any(Payment.class));

    }
    @Test
    void initiatePayment_marksFailedWhenDarajaThrows() {
        PaymentRequest request = new PaymentRequest();
        request.setPhoneNumber("254712345678");
        request.setAmount(500.0);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(darajaService.initiateStkPush(anyString(), anyDouble())).thenThrow(new RuntimeException("Daraja unreachable"));

        Payment result = paymentService.initiatePayment(request);

        assertEquals("FAILED", result.getStatus());
        verify(paymentRepository, times(2)).save(any(Payment.class));
    }
}
