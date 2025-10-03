# Rewards Program API

A Spring Boot REST API for calculating and managing customer reward points.  
This project simulates a retailer’s rewards program where customers earn points based on transactions.

---

## Features

- Rewards calculation rules:
  - 2 points for every $1 spent over $100
  - 1 point for every $1 spent between $50 and $100
  - Example: $120 purchase → (20 × 2) + (50 × 1) = **90 points**
- Calculate rewards:
  - Per **transaction**
  - Per **month**
  - For a **custom date range** or **last N months**
- Record transactions for **existing customers** or auto-create **new customers** if not found
- Expose REST endpoints for managing customers and transactions
- **Validation** on request data (amount > 0, valid email, valid dates, etc.)
- **Global exception handling** for clear error responses (400, 404, 500)
- **MySQL database** for easy testing (,   preloaded with sample data
- **Postman testing ** included for  manual testing
- **JUnit 5 tests** for repositories, services, and controllers

---

## 🛠️ Tech Stack

- Java 23
- Spring Boot 3.x  
- Spring Data JPA  
- MYSQL Database   
- Maven   
- JUnit 5 

---

##  Project Structure

```
src/main/java/com/rewards
 ├── controller       # REST Controllers (Customers, Transactions, Rewards)
 ├── dto              # Request & Response DTOs
 ├── mapper           # DTO ↔ Entity Mappers
 ├── model            # JPA Entities (Customer, Transaction)
 ├── repository       # Spring Data Repositories
 ├── service          # Business Services & Interfaces
 ├── util             # Reward rules & constants
 ├── exception        # Global exception handling
 └── RewardsApiApplication.java


## ⚙️ Setup & Run

### 1. Clone repository
```bash
git clone https://github.com/siva35811/reward-points.git
cd rewards-program
```

### 2. Build & run
```bash
mvn clean install
mvn spring-boot:run
```

### 3. Access application
- **API** Base URL:http://localhost:8080/api 
- **MySQL** URL:jdbc:mysql://localhost:3306/rewards
  - JDBC URL:com.mysql.cj.jdbc.Drive
  - Username:  
  - Password:

---

## 📌 API Endpoints

### Customers
- POST /api/customers/add → Create a new customer  
- GET /api/customers/{customer_id} → Fetch customer by ID  
- GET /api/customers/get  → List all customers  

---

### Transactions
- `POST /api/transactions` → Create a transaction  
  - If `customerId` exists → attach transaction to that customer  
  - If not →Message thrown to add customer (requires name & email)  

**Example Request:**
```json
{
  "customerId":1,
  "amount": 120.0,
  "transactionDate": "2025-09-20"
}
```

**Example Response:**
```json
{
  "transactionId": 1,
  "amount": 120.0,
  "transactionDate": "2025-09-20",
  "points": 90,
  "customerId": 1,
  "customerName": "test",
  "customerEmail": "test@example.com"
}
```

---

### Rewards
- `GET /api/rewards/customer/{customerId}?months=3` → Rewards for last **3 months**  
- `GET /api/rewards/customer/[customer_id}?from=2025-09-22&to=2025-08-10` → Rewards for **custom date range**  

**Example Response:**
```json
{
    "customerId": 2,
    "customerName": "Jane Smith",
    "customerEmail": "jane@example.com",
    "from": "2025-06-23",
    "to": "2025-09-23",
    "transactions": [
        {
            "transactionId": 5,
            "transactionDate": "2025-07-22",
            "transactionAmount": 155.00,
            "points": 160
        }
    ],
    "monthlyRewards": {
        "2025-07": 160
    },
    "totalRewards": 160.0
}
```

---

## 🧪 Testing

### Run all tests
```bash
mvn test
```

### Test coverage
- **Repository tests** → MYSQL DB + `@DataJpaTest`  
- **Service tests** → Mockito (reward calculation edge cases)  
- **Controller tests** → MockMvc (`/api/rewards`, `/api/transactions`)  
- **Exception handler tests** → Invalid dates, missing customers  

---

## 🌐 Postman Testing
1. Endpoints to test:
   - Create customer
   - Create transaction (existing)
   - Get rewards ( months, custom range)

---

## 📖 Notes
Rewards for Minamount to be spent in transaction are kept in the application.yml so it can be configured based on the requirement from the retailer 

- Input validation ensures:
  - Invalid date → **400 Bad Request**
  - Missing customer → request to add (if name & email provided)  
  - Otherwise → **404 Not Found**
  - Reward points can be altered in application.yml
