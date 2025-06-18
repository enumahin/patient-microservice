# Patient Microservice

## Description
This microservice provides endpoints for managing patient data.
It depends on the data package for DTOs

## Requirements
- Java 17
- Maven

## Endpoints
- `/api/patients`: GET, POST
- `/api/patients/{id}`: GET, PUT, DELETE

## Usage
1. Install dependencies: `mvn clean install`
2. Run the application: `mvn spring-boot:run`

## Configuration
The application uses the following configuration files:
- `application.yml`: application configuration
- `application-dev.yml`: development-specific configuration (optional)

## Testing
Run tests: `mvn test`

## Deployment
Build the application: `mvn clean package`
Deploy to Kubernetes: `kubectl apply -f deployment.yaml`

## License
This project is licensed under the Apache License, Version 2.0.
