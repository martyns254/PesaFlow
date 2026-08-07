PesaFlow

A microservices project built to learn Spring Boot the right way — Java, REST APIs, and service-to-service communication — using an M-Pesa-style payment flow as the working example. M-Pesa and the infrastructure behind Kenyan fintech is what this is built toward: two services that talk to each other over HTTP, each owning its own data, the same shape as the real thing at a much smaller scale.

This is a learning project, not a production system — built with real layering and error handling rather than a single-file demo.

Architecture diagram: architecture-diagram.svg

What it does

A client initiates a payment for a phone number and amount. payment-service records it, then calls wallet-service over HTTP to credit that phone number's wallet. No shared database, no shared code — only HTTP between them.

Services

payment-service (port 8080)

Method	Endpoint	Description
POST	/api/payments	Create a payment, credit the matching wallet
GET	/api/payments	List all payments
GET	/api/payments/{id}	Get one payment

wallet-service (port 8081)

Method	Endpoint	Description
POST	/api/wallets?phoneNumber=...	Create a wallet
GET	/api/wallets/{phoneNumber}	Get balance
POST	/api/wallets/credit?phoneNumber=...&amount=...	Credit a wallet
POST	/api/wallets/debit?phoneNumber=...&amount=...	Debit, rejected if funds are insufficient
A few real decisions behind it
DTOs on every request — a client can never set its own payment status
Every credit/debit writes two records: the running balance, and a permanent transaction row (an audit trail, not just a number)
If wallet-service is unreachable when a payment tries to credit it, the payment is marked FAILED, not silently left as SUCCESS
Each service is its own Spring Boot app, own pom.xml, own port — genuinely independent, not just separated by folder
Not done yet

H2 in-memory only, no auth, no real Daraja/M-Pesa API call yet, no retries on the payment → wallet call, no API gateway.

Roadmap

API authentication → real Daraja sandbox integration → notification service → Postgres → resilience (retries/circuit breaker).

Running locally
cd paymentservice && ./mvnw spring-boot:run
cd walletservice && ./mvnw spring-boot:run

Start wallet-service first — payment-service expects it at localhost:8081.

Stack

Java 17 · Spring Boot · Spring Data JPA · H2 · Maven
