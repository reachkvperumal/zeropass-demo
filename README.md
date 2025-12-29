# zeropass-demo
Spring Boot Demo - ZeroPass with Passkey & WebAuthn

## Overview
This is a Spring Boot application demonstrating Passkey and WebAuthn authentication capabilities using WebAuthn4J library.

## Technologies Used

### Core Framework
- **Spring Boot 3.2.1** - Java 17
- **Spring Web** - RESTful web services
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence layer

### WebAuthn & Passkey
- **WebAuthn4J Core 0.29.1.RELEASE** - W3C Web Authentication API implementation
- **WebAuthn4J Spring Security Core 0.11.0.RELEASE** - Spring Security integration for WebAuthn

### Database
- **H2 Database** - In-memory database for development
- **Hibernate** - ORM framework

### Development Tools
- **Spring Boot DevTools** - Hot reload and development utilities
- **Lombok** - Reduce boilerplate code

## Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

## Getting Started

### Build the Project
```bash
mvn clean install
```

### Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Run Tests
```bash
mvn test
```

## Configuration

### Application Settings
The application is configured via `src/main/resources/application.yml`:

- **Server Port**: 8080
- **Database**: H2 in-memory database
- **H2 Console**: Available at `/h2-console`
- **Default Credentials**: admin/admin (Development only - configure secure credentials for production)

## Key Dependencies

```xml
<!-- Spring Boot Starters -->
spring-boot-starter-web
spring-boot-starter-security
spring-boot-starter-data-jpa
spring-boot-starter-validation

<!-- WebAuthn4J for Passkey/WebAuthn -->
webauthn4j-core (0.29.1.RELEASE)
webauthn4j-spring-security-core (0.11.0.RELEASE)
```

## Next Steps
1. Implement WebAuthn registration endpoint
2. Implement WebAuthn authentication endpoint
3. Create user entity and repository
4. Configure WebAuthn attestation and assertion validation
5. Add frontend for Passkey registration and authentication

## License
This project is licensed under the MIT License - see the LICENSE file for details.

