# CDR Patient Service

A microservice responsible for managing patient data in the CDR (Call Detail Records) system. This service handles the storage, retrieval, and management of patient information.

## Features

- CRUD operations for patient entities
- Integration with MySQL database
- Event-driven architecture with RabbitMQ
- Circuit breaking with Resilience4j
- Health monitoring and metrics
- API documentation with Swagger/OpenAPI

## Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- MySQL 8.0 or higher
- RabbitMQ 3.8 or higher
- Eureka Discovery Server (running on port 8761 by default)
- Spring Cloud Config Server (running on port 8071 by default, optional)

## Environment Variables

### Required Configuration

```properties
# Server Configuration
SERVER_PORT=8030

# Spring Application Name
SPRING_APPLICATION_NAME=patient

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/patient?createDatabaseIfNotExist=true
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root

# JPA/Hibernate Configuration
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQL8Dialect

# Eureka Client Configuration
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://localhost:8761/eureka/
```

### Optional Configuration

```properties
# JPA/Hibernate
SPRING_JPA_SHOW_SQL=true
SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL=true

# Logging
LOGGING_LEVEL_ORG_HIBERNATE_SQL=DEBUG
LOGGING_LEVEL_ORG_HIBERNATE_TYPE_DESCRIPTOR_SQL_BASICBINDER=TRACE
LOGGING_LEVEL_COM_ALIENWORKSPACE_CDR_PATIENT=ERROR

# Actuator Endpoints
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always
```

## API Endpoints

### Patient Management

- `GET /api/patients` - Get all patients
- `GET /api/patients/{id}` - Get patient by ID
- `POST /api/patients` - Create new patient
- `PUT /api/patients/{id}` - Update existing patient
- `DELETE /api/patients/{id}` - Delete patient

### Health and Monitoring

- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

## Getting Started

1. **Set up the database**
   ```sql
   CREATE DATABASE IF NOT EXISTS patient;
   ```

2. **Build the application**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   java -jar target/patient-1.0.0.jar
   ```
   Or using Maven:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the API documentation**
   ```
   http://localhost:8030/swagger-ui.html
   ```

## Database Schema

The service uses the following main entity:

- **Patient**: Stores patient information including personal and medical details

## Event Handling

The service publishes and subscribes to the following events:

- **Published Events**:
  - `PatientCreatedEvent`
  - `PatientUpdatedEvent`
  - `PatientDeletedEvent`

## Monitoring

The service exposes the following monitoring endpoints:

- Health: `GET /actuator/health`
- Info: `GET /actuator/info`
- Metrics: `GET /actuator/metrics`
- Prometheus: `GET /actuator/prometheus`

## Dependencies

- Spring Boot Web
- Spring Data JPA
- Spring Cloud OpenFeign
- Spring Cloud Circuit Breaker
- Spring Boot Actuator
- Spring Cloud Config Client
- Spring Cloud Netflix Eureka Client
- Spring AMQP (RabbitMQ)
- MySQL Connector/J
- Lombok
- MapStruct

## License

[Specify your license here]

## Contact

[Your contact information]
