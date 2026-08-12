# PesaFlow

A microservices project built to learn Spring Boot the right way — Java, REST APIs, and service-to-service communication — using an M-Pesa-style payment flow as the working example. M-Pesa and the infrastructure behind Kenyan fintech is what this is built toward: two services that talk to each other over HTTP, each owning its own PostgreSQL database, integrated with the real Safaricom Daraja sandbox.

This is a learning project, not a production system — built with real layering and error handling rather than a single-file demo.

Architecture diagram: [`architecture-diagram.svg`](./architecture-diagram.svg)

## What it does

A client initiates a payment for a phone number and amount. `payment-service` records it as `PENDING` and sends a real STK Push through Safaricom's Daraja sandbox. The flow is asynchronous — Safaricom calls back later (30-90+ seconds) with the result, and only then does the payment resolve to `SUCCESS` or `FAILED`. On confirmed success, `payment-service` calls `wallet-service` over HTTP to credit that phone number's wallet, never optimistically, only after Daraja confirms the money moved.

## Services

**payment-service** (port 8080)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/payments` | Start a payment: saves `PENDING`, sends STK Push |
| GET | `/api/payments` | List all payments |
| GET | `/api/payments/{id}` | Get one payment |
| POST | `/api/payments/callback` | Receives Daraja's async result (no API key, Safaricom can't send one) |

**wallet-service** (port 8081)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/wallets?phoneNumber=...` | Create a wallet |
| GET | `/api/wallets/{phoneNumber}` | Get balance |
| POST | `/api/wallets/credit?phoneNumber=...&amount=...` | Credit a wallet |
| POST | `/api/wallets/debit?phoneNumber=...&amount=...` | Debit, rejected if funds are insufficient |

## A few real decisions behind it

- Every endpoint requires an `X-API-KEY` header, including calls `payment-service` makes to `wallet-service` internally. Securing a service breaks anything that calls it, including your own other services, and this project handles that rather than ignoring it
- The Daraja callback endpoint is the one deliberate exception, since Safaricom can't send a custom header, so it's left open by path
- `CheckoutRequestID` is stored on the payment at STK Push time and used to match the later callback back to the right record, since the two are separate HTTP requests, seconds to minutes apart
- Every credit/debit writes two records: the running balance, and a permanent transaction row (an audit trail, not just a number)
- If `wallet-service` is unreachable when a confirmed payment tries to credit it, the payment is marked `FAILED`, not silently left as `SUCCESS`
- Each service is its own Spring Boot app, own `pom.xml`, own port, own PostgreSQL database, genuinely independent, not just separated by folder

## Not done yet

No notification service yet, no retries/circuit breaker on the payment → wallet call, no API gateway, no automated tests, Daraja "Go Live" (real Paybill, real phones) not applied for, sandbox only.

## Roadmap

Automated testing (JUnit/Mockito) → Docker → notification service → resilience (retries/circuit breaker) → API gateway.

## Running locally

Requires a local PostgreSQL instance with two databases created: `pesaflow_payments` and `pesaflow_wallets`. Set the connection details (URL, username, password) in each service's `application.properties`.

```
cd paymentservice && ./mvnw spring-boot:run
cd walletservice && ./mvnw spring-boot:run
```
Start `wallet-service` first. For Daraja callbacks to reach `payment-service` locally, tunnel it with `ngrok http 8080` and set `mpesa.callback.url` in `application.properties` to the generated URL, it changes on every Ngrok restart.

## Stack

Java 17 · Spring Boot · Spring Data JPA · PostgreSQL · Maven · Safaricom Daraja API

## Author

Martins Kosgei — [github.com/martyns254](https://github.com/martyns254)
