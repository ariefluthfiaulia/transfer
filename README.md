# Money Transfer APIs

This project implements a set of Money Transfer APIs using Java with a clean architecture design philosophy. The APIs include functionalities for validating bank accounts, transferring money, and handling callback status from the bank.

## Overview

The Money Transfer APIs provide a way to validate bank accounts, execute money transfers, and receive callback notifications from a bank. The architecture follows clean coding principles, ensuring separation of concerns and maintainability.

## Endpoints

### 1. Account Validation

- **URL**: `/api/v1/account/validate`
- **Method**: `POST`
- **Description**: Validates the account number and name of the bank account owner using a mock endpoint.
  
- **Request Headers**:
  - `Content-Type: application/json`

- **Request Body**:
  ```json
  {
      "accountNumber": "string",
      "ownerName": "string"
  }
  ```

- **Response**:
    * 200 OK: Successful validation.
    ```json
    {
    "isValid": true,
    "message": "Account is valid."
    }
    ```

### 2. Transfer/Disbursement

- **URL**: `/api/v1/transfer`
- **Method**: `POST`
- **Description**: Transfers money to the destination account using a mock endpoint.
  
- **Request Headers**:
  - `Content-Type: application/json`

- **Request Body**:
  ```json
  {
    "fromAccount": "string",
    "toAccount": "string",
    "amount": "decimal"
  }
  ```

- **Response**:
    * 201 Created: Successful transfer.
    ```json
    {
    "transactionId": "string",
    "status": "pending",
    "message": "Transfer initiated."
    }
    ```

### 3. Transfer/Disbursement Callback

- **URL**: `/api/v1/transfer/callback`
- **Method**: `POST`
- **Description**: Receives callback status of transfer from the bank.
  
- **Request Headers**:
  - `Content-Type: application/json`

- **Request Body**:
  ```json
  {
    "transactionId": "string",
    "status": "string"
  }
  ```

- **Response**:
    * 200 Ok: Callback received successfully.
    ```json
    {
      "transactionId": "string",
      "status": "string"
    }
    ```

## Mock API Endpoints
- Account Validation Mock API: `URL: https://66ffbde14da5bd237551c3fe.mockapi.io/api/v1/accounts?accountNumber=68351553&accountOwner=Mamie%20Nader`

- Transfer Mock API: `URL: https://66ffbde14da5bd237551c3fe.mockapi.io/api/v1/transfer`

## Database Schema
The database used for storing transaction data is PostgreSQL. The following schema can be used for the transactions:
```sql
CREATE TABLE transfers (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    transaction_id VARCHAR(255) UNIQUE NOT NULL,
    source_account VARCHAR(255) NOT NULL,
    destination_account VARCHAR(255) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

## Technologies Used
- Java
- Spring Boot
- PostgreSQL
- MockAPI (or other preferred service)
- Maven (for dependency management)

## Installation
- Clone the repository: `git clone https://github.com/yourusername/money-transfer-api.git`
- Navigate to the project directory: `cd money-transfer-api`
- Install dependencies: `mvn clean install`
- Configure your PostgreSQL database in the 'application.properties'

## Running the Application
- To run the application, use the following command: `mvn spring-boot:run`
 - The application will start on `http://localhost:8080`.
