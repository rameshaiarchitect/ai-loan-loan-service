# Loan Service

Spring Boot microservice responsible for handling loan application
requests and publishing events to Kafka.

------------------------------------------------------------------------

## Responsibilities

-   Expose REST API to accept loan applications
-   Validate incoming requests
-   Publish loan application events to Kafka
-   Act as producer in event-driven architecture

------------------------------------------------------------------------

## API

### Submit Loan Application

POST /loan/apply

### Request Body

``` json
{
  "applicationId": "APP123",
  "amount": 10000,
  "salary": 5000
}
```

### Response

    Application Submitted

------------------------------------------------------------------------

## Kafka Integration

-   Topic: `loan.application.submitted`
-   Producer: Spring Kafka (`KafkaTemplate`)
-   Message format: String (serialized LoanRequest)
-   Kafka is configured for both local and container access

------------------------------------------------------------------------

## Tech Stack

-   Java 17+
-   Spring Boot
-   Spring Kafka
-   Maven

------------------------------------------------------------------------

## 🧪 Testing

The service includes automated tests to validate API behavior and Kafka
event publishing.

### Test Types

-   Unit Tests (controller and service layer)
-   Integration Tests using Testcontainers

------------------------------------------------------------------------

## 🧪 Testcontainers Integration

Kafka is tested using Testcontainers to simulate a real environment
during tests.

-   Starts Kafka container dynamically
-   No dependency on local Kafka setup
-   Ensures isolation and consistency
-   Validates real Kafka event publishing

------------------------------------------------------------------------

## ▶️ Run Application

``` bash
mvn spring-boot:run
```

------------------------------------------------------------------------

## ▶️ Run Tests

``` bash
mvn clean test
```

------------------------------------------------------------------------

## 📊 Code Coverage

JaCoCo is used for coverage reporting.

``` bash
mvn clean verify
```

Coverage report:

    target/site/jacoco/index.html

------------------------------------------------------------------------

## Logs

On successful request:

    Sending event to Kafka: LoanRequest(applicationId=..., amount=..., salary=...)

------------------------------------------------------------------------

## Configuration

Kafka runs via Docker:

``` yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
```

------------------------------------------------------------------------

## Notes

-   Service does not contain business decision logic
-   Designed to be lightweight and scalable
-   Kafka integration is validated using Testcontainers
-   Suitable for CI/CD pipelines

------------------------------------------------------------------------

## Future Enhancements

-   Switch to JSON-based event payload
-   Add request validation (Bean Validation)
-   Persist loan applications in Postgres
-   Add retry and DLQ handling for Kafka
