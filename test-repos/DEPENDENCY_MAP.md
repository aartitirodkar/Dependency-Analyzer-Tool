# Dependency Map for Test Repositories

This document shows the dependency relationships between all test repositories.

## Repository Overview

1. **user-service** - User management service
2. **order-service** - Order management service  
3. **payment-service** - Payment processing service
4. **notification-service** - Notification service (email/SMS)
5. **inventory-service** - Inventory management service

## Feign Client Dependencies

### user-service
- → **order-service** (OrderServiceClient)
- → **notification-service** (NotificationServiceClient)

### order-service
- → **user-service** (UserServiceClient)
- → **payment-service** (PaymentServiceClient)
- → **inventory-service** (InventoryServiceClient)

### payment-service
- → **notification-service** (NotificationServiceClient)

### notification-service
- No Feign clients (leaf service)

### inventory-service
- No Feign clients (leaf service)

## Common Library Dependencies

All services share:
- `org.springframework.boot:spring-boot-starter-web:2.7.14`
- `org.springframework.cloud:spring-cloud-starter-openfeign:3.1.8`
- `org.projectlombok:lombok:1.18.28`
- `com.fasterxml.jackson.core:jackson-databind:2.15.2`
- `org.apache.commons:commons-lang3:3.12.0`
- `junit:junit:4.13.2`

### Additional Shared Dependencies

**user-service, order-service, payment-service, inventory-service** share:
- `org.springframework.boot:spring-boot-starter-data-jpa:2.7.14`
- `mysql:mysql-connector-java:8.0.33`

**order-service, inventory-service, notification-service** share:
- `org.springframework.boot:spring-boot-starter-data-redis:2.7.14`

**order-service, payment-service** share:
- `org.hibernate.validator:hibernate-validator:6.2.5.Final`

## Common Import Patterns

All services use:
- `org.springframework.web.bind.annotation.RestController`
- `org.springframework.web.bind.annotation.RequestMapping`
- `org.springframework.beans.factory.annotation.Autowired`
- `org.springframework.stereotype.Service`
- `com.fasterxml.jackson.annotation.JsonProperty`
- `org.apache.commons.lang3.StringUtils`
- `java.util.List`, `java.util.ArrayList`

## Expected Graph Visualization

The dependency graph should show:

1. **5 repository nodes** (user-service, order-service, payment-service, notification-service, inventory-service)

2. **Multiple dependency edges**:
   - Library dependencies (blue) - connecting all services
   - File/Import dependencies (green) - connecting all services
   - Feign Client dependencies (red):
     - user-service → order-service
     - user-service → notification-service
     - order-service → user-service
     - order-service → payment-service
     - order-service → inventory-service
     - payment-service → notification-service

3. **Complex network** showing:
   - notification-service as a hub (receives calls from user-service and payment-service)
   - order-service as a central service (calls multiple services)
   - Strong coupling between user-service and order-service (bidirectional Feign clients)

