# Digital Banking System

A modern digital banking application built with Spring Boot 3.5.0, implementing modular architecture with Spring Modulith and providing secure banking operations through REST APIs.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Database Setup](#database-setup)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [GraphQL Integration](#graphql-integration)
- [Security](#security)
- [Deployment](#deployment)

## 🚀 Overview

The Digital Banking System is a comprehensive banking application that provides essential banking operations including customer management, account management, and transaction processing. Built with modern Java technologies and following best practices for enterprise applications.

## ✨ Features

- **Customer Management**: Create, update, and manage customer profiles
- **Account Management**: Handle various types of bank accounts
- **Transaction Processing**: Process deposits, withdrawals, and transfers
- **Security**: Integrated Spring Security for authentication and authorization
- **API Documentation**: Interactive Swagger/OpenAPI documentation
- **Modular Architecture**: Built with Spring Modulith for better organization
- **Database Integration**: PostgreSQL with HikariCP connection pooling
- **GraphQL Support**: GraphQL code generation capabilities
- **RESTful APIs**: Comprehensive REST API endpoints
- **Comprehensive Logging**: Detailed logging configuration
- **Native Image Support**: GraalVM native compilation ready

## 🛠 Technology Stack

- **Java**: 17
- **Spring Boot**: 3.5.0
- **Spring Modulith**: 1.4.0
- **Spring Security**: Latest
- **Spring Data JDBC**: For database operations
- **PostgreSQL**: Primary database
- **HikariCP**: Connection pooling
- **Lombok**: Code generation
- **Swagger/OpenAPI**: API documentation
- **GraphQL**: Code generation with DGS
- **Maven**: Build tool
- **GraalVM**: Native image support

## 📁 Project Structure

```
Digital.Banking/
├── .idea/                           # IntelliJ IDEA configuration
├── .git/                           # Git repository
├── .mvn/                           # Maven wrapper
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/bankapp/bankingsystem/
│   │   │   │   └── DigitalBankingApplication.java  # Main application class
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java             # Security configuration
│   │   │   │   └── SwaggerConfig.java              # Swagger/OpenAPI config
│   │   │   ├── controller/
│   │   │   │   ├── advice/                         # Exception handlers
│   │   │   │   ├── AccountController.java          # Account REST endpoints
│   │   │   │   ├── CustomerController.java         # Customer REST endpoints
│   │   │   │   └── TransactionController.java      # Transaction REST endpoints
│   │   │   ├── model/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/                    # Request DTOs
│   │   │   │   │   └── response/                   # Response DTOs
│   │   │   │   └── entity/
│   │   │   │       ├── Account.java                # Account entity
│   │   │   │       ├── Customer.java               # Customer entity
│   │   │   │       └── Transaction.java            # Transaction entity
│   │   │   ├── repository/
│   │   │   │   ├── impl/                          # Repository implementations
│   │   │   │   ├── queries/                       # Custom queries
│   │   │   │   ├── AccountRepository.java         # Account repository
│   │   │   │   ├── BaseReadRepository.java        # Base read operations
│   │   │   │   ├── BaseWriteRepository.java       # Base write operations
│   │   │   │   ├── CustomerRepository.java        # Customer repository
│   │   │   │   ├── DeletableRepository.java       # Delete operations
│   │   │   │   └── TransactionRepository.java     # Transaction repository
│   │   │   ├── service/
│   │   │   │   ├── impl/                          # Service implementations
│   │   │   │   ├── AccountService.java            # Account business logic
│   │   │   │   ├── CustomerService.java           # Customer business logic
│   │   │   │   └── TransactionService.java        # Transaction business logic
│   │   │   └── utils/                             # Utility classes
│   │   └── resources/
│   │       ├── application.yml                    # Main configuration
│   │       ├── application.properties             # Additional properties
│   │       └── graphql-client/                    # GraphQL schemas
│   └── test/
│       └── java/
│           └── com/bankapp/bankingsystem/
│               └── ApplicationTests.java          # Test classes
├── target/                                        # Build output
├── .gitattributes                                # Git attributes
├── .gitignore                                    # Git ignore rules
├── HELP.md                                       # Spring Boot help
├── mvnw                                          # Maven wrapper (Unix)
├── mvnw.cmd                                      # Maven wrapper (Windows)
├── pom.xml                                       # Maven configuration
└── README.md                                     # This file
```

## 📋 Prerequisites

Before running this application, make sure you have the following installed:

- **Java 17** or higher
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Docker** (optional, for containerized deployment)
- **Git**

## 🔧 Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Digital.Banking
   ```

2. **Set up PostgreSQL Database**
   ```sql
   CREATE DATABASE bankingdb;
   CREATE USER bankuser WITH PASSWORD 'bankpass';
   GRANT ALL PRIVILEGES ON DATABASE bankingdb TO bankuser;
   ```

3. **Configure application properties**
   Update `src/main/resources/application.yml` with your database credentials if different from defaults.

4. **Install dependencies**
   ```bash
   ./mvnw clean install
   ```

## ⚙️ Configuration

### Database Configuration

The application is configured to use PostgreSQL with the following default settings:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bankingdb
    username: bankuser
    password: bankpass
    driver-class-name: org.postgresql.Driver
```

### Connection Pool Configuration

HikariCP is configured with optimized settings:

```yaml
hikari:
  connection-timeout: 20000
  maximum-pool-size: 20
  minimum-idle: 5
  idle-timeout: 300000
  max-lifetime: 1200000
  leak-detection-threshold: 60000
```

### Server Configuration

```yaml
server:
  port: 8080
  servlet:
    context-path: /
```

## 📚 API Documentation

The application includes comprehensive API documentation using Swagger/OpenAPI.

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

### Main API Endpoints

- **Customers**: `/api/customers`
- **Accounts**: `/api/accounts`
- **Transactions**: `/api/transactions`

## 🗄️ Database Setup

1. **Start PostgreSQL service**
2. **Create database and user** (as shown in prerequisites)
3. **The application will handle schema creation** based on your JPA configuration

## 🚀 Running the Application

### Development Mode

```bash
./mvnw spring-boot:run
```

### Production Mode

```bash
./mvnw clean package
java -jar target/banking-system-0.0.1-SNAPSHOT.jar
```

### Using Docker

```bash
# Build the image
./mvnw spring-boot:build-image

# Run the container
docker run -p 8080:8080 banking-system:0.0.1-SNAPSHOT
```

### Native Image (GraalVM)

```bash
# Compile native image
./mvnw native:compile -Pnative

# Run native executable
./target/banking-system
```


## 🎯 GraphQL Integration

The project is configured with Netflix DGS Codegen plugin for GraphQL client generation:

- Place GraphQL schema files in `src/main/resources/graphql-client/`
- Generated code will be available in `com.bankapp.bankingsystem.codegen` package
- Use `./mvnw generate-sources` to generate GraphQL client code

## 🔐 Security

The application implements Spring Security with:

- Authentication and authorization mechanisms
- Secure API endpoints
- CORS configuration
- Security headers

Security configuration can be found in `src/main/java/config/SecurityConfig.java`.

## 🚢 Deployment

### Traditional Deployment

1. Package the application: `./mvnw clean package`
2. Deploy the JAR file to your server
3. Ensure PostgreSQL is accessible
4. Run with: `java -jar banking-system-0.0.1-SNAPSHOT.jar`

### Docker Deployment

1. Build image: `./mvnw spring-boot:build-image`
2. Push to registry (if needed)
3. Deploy using Docker Compose or Kubernetes

### Native Image Deployment

1. Compile native image: `./mvnw native:compile -Pnative`
2. Deploy the native executable for faster startup and lower memory usage


**Built with ❤️ using Spring Boot and modern Java technologies** 