# Task Management System

A backend application built with **Spring Boot 3.5** for efficient task and user management. This system provides a secure API for managing project tasks and tracking progress.

## Key Features

- **Task Management**: Full CRUD operations for tracking tasks.
- **DTO Architecture**: Uses Data Transfer Objects (DTOs) for secure and efficient data exchange.
- **Global Error Handling**: Consistent API responses and clear error messaging via a global exception handler.


## Tech Stack

- **Java 17**
- **Spring Boot 3.5.0**
- **Spring Data JPA**
- **MySQL** (Database)
- **Maven** (Build Tool)

## Getting Started

### Prerequisites

- JDK 17 or higher
- Maven 3.x
- MySQL Server

### Configuration

Create a `.env` file in the root directory and configure your database credentials:

```env
DB_URL=jdbc:mysql://localhost:3306/task_management
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

### Build and Run

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd task-management-system
   ```

2. Build the project:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Testing

The project includes a comprehensive test suite covering controllers and services.

To run all tests:
```bash
./mvnw test
```


