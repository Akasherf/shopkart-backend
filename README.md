# ShopKart – E-Commerce Backend

A production-ready e-commerce backend built using **Spring Boot**, **JWT Security**, **MySQL**, and **Docker**.  
Designed with clean architecture, role-based access control, and transaction-safe order & payment handling.

---

## 🚀 Features

### 👤 Authentication & Authorization
- JWT-based authentication
- Role-based access control (USER / ADMIN)
- Secure password hashing (BCrypt)
- Swagger JWT authorization support

### 🛒 Customer APIs
- Browse products
- Add/remove items from cart
- Place orders
- View order history
- Cancel orders (state-safe)

### 🧑‍💼 Admin APIs
- Manage products (CRUD)
- View all orders
- Update order status
- Dashboard metrics (orders, revenue, users)

### 💳 Payments
- Idempotent payment confirmation
- Gateway verification abstraction
- Strong Order ↔ Payment consistency
- Transaction-safe state transitions

### 🧱 Engineering Quality
- Global exception handling
- Consistent API response wrapper
- Clean layered architecture
- Swagger OpenAPI documentation
- Dockerized setup (App + MySQL)

---

## 🏗️ Tech Stack

- **Backend**: Java 17, Spring Boot 3
- **Security**: Spring Security, JWT
- **Database**: MySQL 8
- **ORM**: Hibernate / JPA
- **Docs**: Swagger (springdoc-openapi)
- **DevOps**: Docker, Docker Compose
- **Build**: Maven

---

## 🐳 Run with Docker

```bash
docker-compose up --build
