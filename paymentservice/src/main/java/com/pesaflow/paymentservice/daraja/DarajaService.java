package com.pesaflow.paymentservice.daraja;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Service
public class DarajaService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${mpesa.consumer.key}")
    private String consumerKey;

    @Value("${mpesa.consumer.secret}")
    private String consumerSecret;

    @Value("${mpesa.shortcode}")
    private String shortcode;

    @Value("${mpesa.passkey}")
    private String passkey;

    @Value("${mpesa.callback.url}")
    private String callbackUrl;

    public String getAccessToken() {
        String url = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";

        String credentials = consumerKey + ":" + consumerSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedCredentials);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        return (String) response.getBody().get("access_token");
    }
    public Map initiateStkPush(String phoneNumber, Double amount) {
        String accessToken = getAccessToken();

        String timestamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        String password = shortcode + passkey + timestamp;
        String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());

        String url = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.ofEntries(
                Map.entry("BusinessShortCode", shortcode),
                Map.entry("Password", encodedPassword),
                Map.entry("Timestamp", timestamp),
                Map.entry("TransactionType", "CustomerPayBillOnline"),
                Map.entry("Amount", amount.intValue()),
                Map.entry("PartyA", phoneNumber),
                Map.entry("PartyB", shortcode),
                Map.entry("PhoneNumber", phoneNumber),
                Map.entry("CallBackURL", callbackUrl),
                Map.entry("AccountReference", "PesaFlow"),
                Map.entry("TransactionDesc", "PesaFlow payment")
        );
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        return response.getBody();
    }
    }

