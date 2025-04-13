
# Payment Service Test

This project is a payment processing system that involves handling payments, student-parent relationships, user authentication, and more. It includes several services such as:

- **Payment**: Processing payments between parents and students.
- **Student Management**: Fetching student data and managing student-parent relationships.
- **Parent Management**: Fetching parent data.
- **User Authentication**: Login functionality with JWT token generation.

The backend is built with Spring Boot, utilizing repositories, services, and controllers to handle the business logic.

## Features
- **Payment Management**: Allows for the processing of payments between parents and students, updating their respective balances.
- **Student and Parent Management**: Provides APIs to get details about students and parents.
- **User Authentication**: JWT-based authentication system for user login.
- **API Documentation**: Swagger UI for easy interaction with the API endpoints.

## Setup

### Prerequisites

- Java 17 or later
- Maven

### Clone the Repository

```bash
git clone https://github.com/your-username/tredbase-payment-service.git
cd tredbase-payment-service
```

### Install Dependencies

Make sure all dependencies are resolved by running the following:

```bash
mvn clean install
```

### Configure Database

It is located in the  `application.properties`.

### Run the Application

With Maven
```bash
mvn spring-boot:run
```

With Java
```bash
mvn clean package
```
```bash
 java -jar .\payment-0.0.1-SNAPSHOT.jar
```

This will start the application on `http://localhost:5000`.

##  Entities

### ️ Parent

| Field      | Type       | Description                        |
|------------|------------|------------------------------------|
| `id`       | UUID       | Unique identifier for the parent   |
| `firstName`| String     | Parent's first name (required)     |
| `lastName` | String     | Parent's last name (required)      |
| `balance`  | BigDecimal | Wallet balance for the parent      |
| `students` | Set        | Linked students (many-to-many)     |

- Defined with `@ManyToMany(mappedBy = "parents")`
- Uses `@JsonBackReference` to avoid recursive serialization

---

###  Student

| Field       | Type       | Description                        |
|-------------|------------|------------------------------------|
| `id`        | UUID       | Unique identifier for the student  |
| `first_name`| String     | Student's first name (required)    |
| `last_name` | String     | Student's last name (required)     |
| `balance`   | BigDecimal | Wallet balance for the student     |
| `parents`   | Set        | Linked parents (many-to-many)      |

- Owns the relationship with `@JoinTable`
- Uses `@JsonManagedReference` for JSON control

### Payment Ledger
| Field         | Type             | Description                                |
|---------------|------------------|--------------------------------------------|
| `id`          | `Long`           | Auto-generated unique ID                   |
| `parentId`    | `UUID`           | Reference to the parent making the payment |
| `studentId`   | `UUID`           | Reference to the student receiving payment |
| `amount`      | `BigDecimal`     | Amount transferred                         |
| `paymentDate` | `LocalDateTime`  | Date and time when the payment occurred    |
| `status`      | `String`         | Payment status (e.g., `SUCCESS`, `FAILED`) |
---

##  Relationship Overview

A **many-to-many** relationship exists between students and parents:

- A student can have one or more parents.
- A parent can be associated with multiple students.
- The join table `parent_student` maps their relationship.

### JSON Serialization

To handle bidirectional relationship without recursion:

- `Student` uses `@JsonManagedReference`
- `Parent` uses `@JsonBackReference`

## Design Decisions

### Security Implementation

- JWT tokens generated on login
- BCrypt password hashing
- Token validation via filters

### Multi-Table Payment Processing

- Many-to-many between parents and students
- Transactions handled with `@Transactional`
- Payments recorded in a ledger
- Shared parenting scenarios divide payment equally

## Arithmetic Logic and Balance Updates

- **Dynamic Fee Adjustment**: `adjustedAmount = originalAmount * (1 + dynamicAmount)`
- **Split Payment Logic**: `splitAmount = adjustedAmount / 2` (rounded using HALF_EVEN)
- **Balance Validation**: Parents must have sufficient balance
- **Student Balance Update**: Always receives full adjusted amount
- **Precision Handling**: `BigDecimal` used throughout

## Test
To run test in the terminal of the root project type in
```bash
mvn test
```

---


## API Endpoints

The API provides several endpoints for handling users, students, parents, and payments. You can interact with the API via Swagger UI or Postman.

### Swagger UI

Once the application is running, you can access Swagger UI to interact with the API at:

```
http://localhost:5000/swagger-ui/index.html
```

### Authentication

- **POST** `/api/v1/payment/auth/login`: Logs in a user and returns a JWT token for further use.

#### Request Body:
```json
{
  "username": "your-username",
  "password": "your-password"
}
```

#### Response:
```json
{
  "message": "user logged in successfully",
  "data": {
    "token": "your-jwt-token"
  }
}
```

### Parents

- **GET** `/api/v1/payment/parent/`: Retrieves a list of all parents.

- **GET** `/api/v1/payment/parent/{id}`: Retrieves a specific parent by ID.

#### Example Request:
```bash
GET http://localhost:5000/api/v1/payment/parent/{id}
```

#### Example Response:
```json
{
  "message": "success",
  "data": {
    "id": "parent-id",
    "firstName": "John",
    "lastName": "Doe",
    "balance": 1000.0
  }
}
```

### Students

- **GET** `/api/v1/payment/student/`: Retrieves a list of all students.

- **GET** `/api/v1/payment/student/{id}`: Retrieves a specific student by ID.

#### Example Request:
```bash
GET http://localhost:5000/api/v1/payment/student/{id}
```

#### Example Response:
```json
{
  "message": "success",
  "data": {
    "id": "student-id",
    "first_name": "Jane",
    "last_name": "Doe",
    "balance": 500.0,
    "parent": [
      {
        "id": "parent-id",
        "firstName": "John",
        "lastName": "Doe",
        "balance": 1000.0
      }
    ]
  }
}
```

### Payment

- **POST** `/api/v1/payment/makePayment`: Processes a payment from a parent to a student, updating their balances.

#### Request Body:
```json
{
  "parentId": "parent-id",
  "studentId": "student-id",
  "amount": 100.0
}
```

#### Response:
```json
{
  "message": "Payment processed",
  "data": {
    "amount": 100.0,
    "studentBalance": 600.0
  }
}
```

## Architecture

### Services

> **Note**: For the dynamic amount, it can be changed from the `application.properties`

1. **Payment Service**: Handles payment processing and balance updates for both parents and students. For the Shared use-case, the amount deducted is 50/50 from Parent A and Parent B account to fund the child account
2. **Student Service**: Manages fetching and listing students and their details.
3. **Parent Service**: Manages fetching and listing parents and their details.
4. **User Service**: Handles user login and JWT generation.



 