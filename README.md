# 📦 Order Processing System (Spring Boot)

## Overview

This project implements a backend **Order Processing System** for an e-commerce platform using **Java and Spring Boot**.

The system allows customers to place orders, track their status, cancel orders under defined rules, and supports automated background processing.  
It is designed with **production-readiness** in mind, focusing on clean architecture, separation of concerns, observability, and security.

---

## Features

### Core Order Management
- Create an order with multiple items
- Retrieve order details by order ID
- List all orders, optionally filtered by status
- Update order status
- Cancel an order (allowed only when the order is in `PENDING` state)

### Order Lifecycle
Supported order statuses:
- `PENDING`
- `PROCESSING`
- `SHIPPED`
- `DELIVERED`
- `CANCELLED`

Only valid state transitions are allowed and are explicitly enforced to prevent invalid updates.

### Background Processing
- A scheduled job periodically moves all `PENDING` orders to `PROCESSING`
- Scheduling logic is isolated from business logic
- Scheduler timing is configurable via application properties

### Security
- All APIs are secured using **HTTP Basic Authentication**
- Public endpoint:
  - `GET /health`
- Passwords are encoded using BCrypt
- Security is configured using modern Spring Security (`SecurityFilterChain`)

### Production Readiness
- Structured logging using SLF4J
- Global exception handling with consistent API error responses
- Input validation using Bean Validation (`@Valid`)
- Clean separation of concerns across layers

---

## Technology Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security 6
- H2 (in-memory database)
- Lombok
- JUnit 5 & Mockito

---

## Project Structure

```text
src/main/java/com/example/orderprocessor
 ├── controller      # REST controllers (Orders, Health)
 ├── service         # Business logic
 ├── scheduler       # Scheduled background jobs
 ├── security        # Spring Security configuration
 ├── repository      # JPA repositories
 ├── model           # Entities and enums
 ├── exception       # Custom exceptions and global handler
