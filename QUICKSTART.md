# Quick Start Guide

## Prerequisites

- Java 17 or higher installed
- Maven 3.6+ installed (or use Maven Wrapper)

## Step 1: Build the Project

If you have Maven installed:

```bash
cd membership-program
mvn clean install
```

Or use the Maven Wrapper (no Maven installation needed):

```bash
cd membership-program
./mvnw clean install   # On macOS/Linux
mvnw.cmd clean install # On Windows
```

## Step 2: Run the Application

```bash
mvn spring-boot:run
# OR
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

You should see:
```
Started MembershipApplication in X seconds
Initializing demo data...
Created 3 membership plans
Created 3 membership tiers with benefits
Created tier upgrade criteria
Created 3 demo users
Demo data initialized successfully!
```

## Step 3: Verify Application is Running

```bash
curl http://localhost:8080/api/plans
```

You should see a JSON response with 3 membership plans.

## Step 4: Quick Demo - Complete User Journey

Run these commands in order:

### 1. View Available Plans

```bash
curl http://localhost:8080/api/plans
```

### 2. View Available Tiers

```bash
curl http://localhost:8080/api/plans/tiers
```

### 3. Subscribe User 1 to Monthly Plan

```bash
curl -X POST http://localhost:8080/api/memberships/subscribe \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "planId": 1}'
```

### 4. Create an Order (See Benefits Applied)

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "orderValue": 100.00, "category": "electronics"}'
```

Notice the `discountPercentage: 5` and `freeDeliveryApplied: true` in the response!

### 5. Check Current Membership

```bash
curl http://localhost:8080/api/memberships/user/1
```

### 6. Create More Orders to Qualify for GOLD Tier

```bash
# Order 2
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "orderValue": 40.00}'

# Order 3
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "orderValue": 40.00}'

# Order 4
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "orderValue": 30.00}'

# Order 5
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "orderValue": 20.00}'
```

### 7. Evaluate and Auto-Upgrade Tier

```bash
curl -X POST http://localhost:8080/api/memberships/user/1/evaluate-tier
```

Response should be:
```json
{
  "success": true,
  "message": "Tier upgraded to GOLD",
  "newTier": "GOLD"
}
```

### 8. Verify Tier Upgrade

```bash
curl http://localhost:8080/api/memberships/user/1
```

You should now see `"tierLevel": "GOLD"` with 10% discount benefits!

### 9. Create Another Order (See Enhanced Benefits)

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "orderValue": 100.00}'
```

Notice the discount is now 10% instead of 5%!

## Step 5: Access H2 Console (Optional)

1. Open browser: `http://localhost:8080/h2-console`
2. JDBC URL: `jdbc:h2:mem:membershipdb`
3. Username: `sa`
4. Password: (leave blank)
5. Click "Connect"

Now you can browse the database tables and see all the data!

## Stopping the Application

Press `Ctrl+C` in the terminal where the application is running.

## Next Steps

- Check [API_EXAMPLES.md](API_EXAMPLES.md) for complete API documentation
- Check [README.md](README.md) for architecture details
- Test concurrency by running simultaneous tier upgrade requests

## Troubleshooting

### Port 8080 already in use

Change the port in `src/main/resources/application.yml`:

```yaml
server:
  port: 8081
```

### Java version error

Ensure you have Java 17+:

```bash
java -version
```

### Maven not found

Use the Maven Wrapper included in the project:

```bash
./mvnw spring-boot:run  # macOS/Linux
mvnw.cmd spring-boot:run  # Windows
```
