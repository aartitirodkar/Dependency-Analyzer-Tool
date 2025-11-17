# Test Repositories for Dependency Analyzer

This directory contains **5 sample microservices** to test the dependency analyzer tool with a complex dependency network.

## Repositories

### 1. user-service
User management service with:
- Spring Boot Web, Data JPA, Security
- Feign Clients: order-service, notification-service
- Dependencies: MySQL, Lombok, Jackson, Apache Commons

### 2. order-service
Order management service with:
- Spring Boot Web, Data JPA, Redis
- Feign Clients: user-service, payment-service, inventory-service
- Dependencies: MySQL, Redis, Hibernate Validator, Lombok, Jackson

### 3. payment-service
Payment processing service with:
- Spring Boot Web, Data JPA
- Feign Client: notification-service
- Dependencies: MySQL, Hibernate Validator, Lombok, Jackson

### 4. notification-service
Notification service (email/SMS) with:
- Spring Boot Web, Mail, Redis
- No Feign clients (leaf service)
- Dependencies: Redis, Lombok, Jackson

### 5. inventory-service
Inventory management service with:
- Spring Boot Web, Data JPA, Redis
- No Feign clients (leaf service)
- Dependencies: MySQL, Redis, Lombok, Jackson

## Common Dependencies

**All 5 repositories share:**
- `org.springframework.boot:spring-boot-starter-web:2.7.14`
- `org.springframework.cloud:spring-cloud-starter-openfeign:3.1.8`
- `org.projectlombok:lombok:1.18.28`
- `com.fasterxml.jackson.core:jackson-databind:2.15.2`
- `org.apache.commons:commons-lang3:3.12.0`
- `junit:junit:4.13.2` (test scope)

**4 repositories share (user, order, payment, inventory):**
- `org.springframework.boot:spring-boot-starter-data-jpa:2.7.14`
- `mysql:mysql-connector-java:8.0.33`

**3 repositories share (order, inventory, notification):**
- `org.springframework.boot:spring-boot-starter-data-redis:2.7.14`

**2 repositories share (order, payment):**
- `org.hibernate.validator:hibernate-validator:6.2.5.Final`

## Feign Client Dependencies

- **user-service** → order-service, notification-service
- **order-service** → user-service, payment-service, inventory-service
- **payment-service** → notification-service

## Common Patterns

All services use:
- `@RestController` and `@RequestMapping`
- `@Service` annotation
- `@Autowired` for dependency injection
- `com.fasterxml.jackson.annotation.JsonProperty`
- `org.apache.commons.lang3.StringUtils`
- `java.util.List`, `java.util.ArrayList`

## Testing the Analyzer

Run the analyzer from the parent directory:

```bash
cd C:\Users\bipin\dependency-analyzer
java -jar target/dependency-analyzer-1.0.0.jar test-repos/user-service output.html test-repos
```

This should detect:
1. **Common Library Dependencies**: All the shared Maven dependencies listed above
2. **Common File Dependencies**: Similar imports and patterns
3. **Feign Client Dependencies**: Both have Feign clients (though they point to different services, the pattern is similar)

