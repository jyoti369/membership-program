# FirstClub Membership Program

A comprehensive backend system for managing tiered membership programs with subscription-based benefits, built with Spring Boot.

## Architecture Overview

### Design Patterns Used

1. **Strategy Pattern** - Tier evaluation criteria (OrderCount, OrderValue, Cohort)
2. **Builder Pattern** - Entity and DTO construction
3. **Repository Pattern** - Data access abstraction
4. **Service Layer Pattern** - Business logic separation
5. **Factory Pattern** - Benefit calculation engine

### Key Features

- **Membership Plans**: Monthly, Quarterly, and Yearly subscriptions
- **Tiered Benefits**: Silver, Gold, and Platinum tiers with configurable benefits
- **Automatic Tier Upgrades**: Based on order count, order value, and user cohort
- **Benefit Calculation**: Dynamic discount and free delivery calculation
- **Concurrency Handling**: Optimistic locking with retry mechanism
- **Extensibility**: Easy to add new benefit types and evaluation strategies

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database** (in-memory)
- **Lombok**
- **Maven**

## Project Structure

```
src/main/java/com/firstclub/membership/
├── config/              # Configuration and data initialization
├── controller/          # REST API controllers
├── dto/                 # Data Transfer Objects
├── entity/              # JPA entities
├── enums/               # Enumerations
├── repository/          # Spring Data repositories
├── service/             # Business logic services
├── strategy/            # Strategy pattern implementations for tier evaluation
└── MembershipApplication.java
```

## Database Schema

### Core Entities

- **User**: User information and cohort
- **MembershipPlan**: Subscription plans (Monthly/Quarterly/Yearly)
- **MembershipTier**: Tier levels (Silver/Gold/Platinum)
- **TierBenefit**: Configurable benefits per tier
- **Membership**: User's active membership
- **Order**: User orders for benefit application
- **TierUpgradeCriteria**: Criteria for automatic tier upgrades

## API Endpoints

### Membership Plans & Tiers

```
GET    /api/plans                    - Get all active plans
GET    /api/plans/{planId}           - Get specific plan
GET    /api/plans/tiers              - Get all tiers with benefits
```

### Membership Management

```
POST   /api/memberships/subscribe           - Subscribe to a plan
GET    /api/memberships/user/{userId}       - Get current membership
PUT    /api/memberships/user/{userId}/upgrade?tierLevel=GOLD    - Upgrade tier
PUT    /api/memberships/user/{userId}/downgrade?tierLevel=SILVER - Downgrade tier
DELETE /api/memberships/user/{userId}/cancel - Cancel membership
POST   /api/memberships/user/{userId}/evaluate-tier - Evaluate and auto-upgrade
GET    /api/memberships/user/{userId}/eligible-tier - Check eligible tier
```

### Orders & Benefits

```
POST   /api/orders                   - Create order with benefits
GET    /api/orders/benefits/free-delivery?userId=1&category=electronics
GET    /api/orders/benefits/discount?userId=1&category=electronics
```

## Running the Application

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Build and Run

```bash
cd membership-program
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### H2 Console

Access the H2 database console at: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:membershipdb`
- Username: `sa`
- Password: (leave blank)

## Demo Data

The application initializes with:

### Plans
1. **Monthly Plan** - $9.99/month
2. **Quarterly Plan** - $24.99/3 months
3. **Yearly Plan** - $89.99/year

### Tiers
1. **Silver** - 5% discount, free delivery on orders $50+
2. **Gold** - 10% discount, free delivery on all orders, priority support
3. **Platinum** - 15% discount, free express delivery, dedicated manager, early access

### Users
1. john.doe@example.com (cohort: regular)
2. jane.smith@example.com (cohort: premium)
3. bob.wilson@example.com (cohort: vip)

### Upgrade Criteria
- **Gold**: 5 orders + $200 monthly value
- **Platinum**: 10 orders + $500 monthly value + premium/vip cohort

## API Usage Examples

### 1. Subscribe to a Plan

```bash
curl -X POST http://localhost:8080/api/memberships/subscribe \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "planId": 1
  }'
```

### 2. Create an Order with Benefits

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "orderValue": 100.00,
    "category": "electronics"
  }'
```

### 3. Check Current Membership

```bash
curl http://localhost:8080/api/memberships/user/1
```

### 4. Evaluate Tier Upgrade

```bash
curl -X POST http://localhost:8080/api/memberships/user/1/evaluate-tier
```

### 5. Manual Tier Upgrade

```bash
curl -X PUT "http://localhost:8080/api/memberships/user/1/upgrade?tierLevel=GOLD"
```

### 6. Check Free Delivery Eligibility

```bash
curl "http://localhost:8080/api/orders/benefits/free-delivery?userId=1&category=electronics"
```

### 7. Get All Plans and Tiers

```bash
curl http://localhost:8080/api/plans
curl http://localhost:8080/api/plans/tiers
```

## Real Request/Response Examples

### Get All Plans

**Request:**
```bash
curl http://localhost:8080/api/plans
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Monthly Plan",
    "duration": "MONTHLY",
    "price": 9.99,
    "description": "Pay monthly, cancel anytime"
  },
  {
    "id": 2,
    "name": "Quarterly Plan",
    "duration": "QUARTERLY",
    "price": 24.99,
    "description": "Save 17% with quarterly billing"
  },
  {
    "id": 3,
    "name": "Yearly Plan",
    "duration": "YEARLY",
    "price": 89.99,
    "description": "Best value - Save 25% annually"
  }
]
```

### Get All Tiers with Benefits

**Request:**
```bash
curl http://localhost:8080/api/plans/tiers
```

**Response:**
```json
[
  {
    "id": 1,
    "tierLevel": "SILVER",
    "name": "Silver Member",
    "description": "Entry level membership with basic benefits",
    "benefits": [
      {
        "benefitType": "FREE_DELIVERY",
        "benefitValue": "true",
        "description": "Free delivery on orders above $50",
        "applicableCategory": null
      },
      {
        "benefitType": "DISCOUNT",
        "benefitValue": "5",
        "description": "5% discount on all items",
        "applicableCategory": null
      }
    ]
  },
  {
    "id": 2,
    "tierLevel": "GOLD",
    "name": "Gold Member",
    "description": "Mid-tier membership with enhanced benefits",
    "benefits": [
      {
        "benefitType": "DISCOUNT",
        "benefitValue": "10",
        "description": "10% discount on all items",
        "applicableCategory": null
      },
      {
        "benefitType": "FREE_DELIVERY",
        "benefitValue": "true",
        "description": "Free delivery on all orders",
        "applicableCategory": null
      },
      {
        "benefitType": "PRIORITY_SUPPORT",
        "benefitValue": "true",
        "description": "24/7 priority customer support",
        "applicableCategory": null
      }
    ]
  },
  {
    "id": 3,
    "tierLevel": "PLATINUM",
    "name": "Platinum Member",
    "description": "Premium membership with exclusive benefits",
    "benefits": [
      {
        "benefitType": "FREE_DELIVERY",
        "benefitValue": "true",
        "description": "Free express delivery on all orders",
        "applicableCategory": null
      },
      {
        "benefitType": "EARLY_ACCESS",
        "benefitValue": "true",
        "description": "Early access to sales and exclusive deals",
        "applicableCategory": null
      },
      {
        "benefitType": "DISCOUNT",
        "benefitValue": "15",
        "description": "15% discount on all items",
        "applicableCategory": null
      },
      {
        "benefitType": "PRIORITY_SUPPORT",
        "benefitValue": "true",
        "description": "Dedicated account manager",
        "applicableCategory": null
      }
    ]
  }
]
```

### Subscribe to a Plan

**Request:**
```bash
curl -X POST http://localhost:8080/api/memberships/subscribe \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "planId": 1}'
```

**Response:**
```json
{
  "membershipId": 1,
  "userId": 1,
  "userName": "John Doe",
  "userEmail": "john.doe@example.com",
  "planName": "Monthly Plan",
  "planDuration": "MONTHLY",
  "tierLevel": "SILVER",
  "tierName": "Silver Member",
  "status": "ACTIVE",
  "startDate": "2025-12-09T16:29:21.30836",
  "expiryDate": "2026-01-09T16:29:21.30836",
  "benefits": [
    {
      "benefitType": "FREE_DELIVERY",
      "benefitValue": "true",
      "description": "Free delivery on orders above $50",
      "applicableCategory": null
    },
    {
      "benefitType": "DISCOUNT",
      "benefitValue": "5",
      "description": "5% discount on all items",
      "applicableCategory": null
    }
  ],
  "active": true
}
```

### Create Order with Benefits Applied

**Request:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "orderValue": 100.00, "category": "electronics"}'
```

**Response:**
```json
{
  "orderId": 1,
  "userId": 1,
  "orderValue": 100.00,
  "category": "electronics",
  "orderDate": "2025-12-09T16:29:35.612572",
  "freeDeliveryApplied": true,
  "discountPercentage": 5,
  "discountAmount": 5.00,
  "finalAmount": 95.00
}
```

**Note:** The order automatically received:
- 5% discount (SILVER tier benefit)
- Free delivery applied
- Final amount: $95.00 (saved $5.00)

### Check Discount for Non-Member

**Request:**
```bash
curl "http://localhost:8080/api/orders/benefits/discount?userId=2"
```

**Response:**
```json
{
  "category": "all",
  "userId": 2,
  "discountPercentage": 0
}
```

**Note:** User 2 has no active membership, so discount is 0%

### Evaluate Tier Upgrade

**Request:**
```bash
curl -X POST http://localhost:8080/api/memberships/user/1/evaluate-tier
```

**Response (After qualifying for GOLD):**
```json
{
  "message": "Tier upgraded to GOLD",
  "success": true,
  "newTier": "GOLD"
}
```

**Response (When not qualified):**
```json
{
  "success": false,
  "message": "No tier upgrade available"
}
```

### Get Membership After Tier Upgrade

**Request:**
```bash
curl http://localhost:8080/api/memberships/user/1
```

**Response:**
```json
{
  "membershipId": 1,
  "userId": 1,
  "userName": "John Doe",
  "userEmail": "john.doe@example.com",
  "planName": "Monthly Plan",
  "planDuration": "MONTHLY",
  "tierLevel": "GOLD",
  "tierName": "Gold Member",
  "status": "ACTIVE",
  "startDate": "2025-12-09T16:29:21.30836",
  "expiryDate": "2026-01-09T16:29:21.30836",
  "benefits": [
    {
      "benefitType": "DISCOUNT",
      "benefitValue": "10",
      "description": "10% discount on all items",
      "applicableCategory": null
    },
    {
      "benefitType": "FREE_DELIVERY",
      "benefitValue": "true",
      "description": "Free delivery on all orders",
      "applicableCategory": null
    },
    {
      "benefitType": "PRIORITY_SUPPORT",
      "benefitValue": "true",
      "description": "24/7 priority customer support",
      "applicableCategory": null
    }
  ],
  "active": true
}
```

**Note:** Tier upgraded from SILVER to GOLD:
- Discount increased from 5% to 10%
- Added priority support benefit
- Free delivery now on all orders (not just $50+)

### Create Order with Enhanced Benefits (GOLD Tier)

**Request:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "orderValue": 100.00}'
```

**Response:**
```json
{
  "orderId": 7,
  "userId": 1,
  "orderValue": 100.00,
  "category": null,
  "orderDate": "2025-12-09T16:35:20.762509",
  "freeDeliveryApplied": true,
  "discountPercentage": 10,
  "discountAmount": 10.00,
  "finalAmount": 90.00
}
```

**Note:** Same order value, but now with GOLD tier:
- Discount: 10% (was 5% with SILVER)
- Discount amount: $10.00 (was $5.00)
- Final amount: $90.00 (was $95.00)
- **Extra savings: $5.00!**

### Check Free Delivery Eligibility

**Request:**
```bash
curl "http://localhost:8080/api/orders/benefits/free-delivery?userId=1&category=electronics"
```

**Response:**
```json
{
  "userId": 1,
  "category": "electronics",
  "eligibleForFreeDelivery": true
}
```

### Error Example: Duplicate Subscription

**Request:**
```bash
# Try to subscribe user 1 again (already has active membership)
curl -X POST http://localhost:8080/api/memberships/subscribe \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "planId": 1}'
```

**Response:**
```json
{
  "timestamp": "2025-12-09T16:40:00.123456",
  "status": 409,
  "error": "Conflict",
  "message": "User already has an active membership"
}
```

### Error Example: Invalid Tier Upgrade

**Request:**
```bash
# Try to upgrade to SILVER when already at GOLD
curl -X PUT "http://localhost:8080/api/memberships/user/1/upgrade?tierLevel=SILVER"
```

**Response:**
```json
{
  "timestamp": "2025-12-09T16:41:00.123456",
  "status": 400,
  "error": "Bad Request",
  "message": "Cannot upgrade to SILVER"
}
```

## Testing Tier Auto-Upgrade Flow

To test automatic tier upgrades:

1. Subscribe a user to a plan
2. Create multiple orders to meet criteria
3. Call the evaluate-tier endpoint
4. Verify tier upgrade in membership response

Example:

```bash
# Subscribe user 2
curl -X POST http://localhost:8080/api/memberships/subscribe \
  -H "Content-Type: application/json" \
  -d '{"userId": 2, "planId": 1}'

# Create 5 orders totaling $200+
for i in {1..5}; do
  curl -X POST http://localhost:8080/api/orders \
    -H "Content-Type: application/json" \
    -d '{"userId": 2, "orderValue": 50.00, "category": "books"}'
done

# Evaluate tier (should upgrade to GOLD)
curl -X POST http://localhost:8080/api/memberships/user/2/evaluate-tier

# Verify membership
curl http://localhost:8080/api/memberships/user/2
```

## Concurrency Handling

The system uses **optimistic locking** with `@Version` annotation on the Membership entity:

- Prevents concurrent tier modifications
- Automatic retry with exponential backoff (max 3 attempts)
- Thread-safe tier upgrades and downgrades

## Extensibility

### Adding a New Benefit Type

1. Add benefit to tier in `DataInitializer` or via API
2. Update `BenefitCalculationService` to handle new type

### Adding a New Evaluation Strategy

1. Create class implementing `TierEvaluationStrategy`
2. Add `@Component` annotation
3. Strategy automatically picked up by `TierEvaluationService`

Example:

```java
@Component
public class PurchaseFrequencyStrategy implements TierEvaluationStrategy {
    @Override
    public boolean evaluate(User user, TierUpgradeCriteria criteria) {
        // Custom logic
    }

    @Override
    public String getStrategyName() {
        return "PURCHASE_FREQUENCY";
    }
}
```

## Key Design Decisions

1. **Optimistic Locking**: Chosen over pessimistic locking for better performance in read-heavy scenarios
2. **Strategy Pattern**: Allows easy addition of new tier evaluation criteria
3. **Configurable Benefits**: Benefits stored as key-value pairs for maximum flexibility
4. **Separate Tier and Plan**: Users can be on any tier regardless of plan
5. **Automatic Tier Evaluation**: Can be triggered manually or via scheduled job

## Future Enhancements

- Add scheduled job for automatic tier evaluation
- Implement membership renewal workflow
- Add analytics for membership metrics
- Implement coupon/promo code system
- Add notification system for tier upgrades
- Implement payment gateway integration

## Error Handling

The application includes comprehensive error handling:

- Validation errors (400 Bad Request)
- Business logic errors (409 Conflict)
- Resource not found (404 Not Found)
- Internal errors (500 Internal Server Error)

All errors return structured JSON responses with timestamps.

## Logging

The application uses SLF4J with Logback:

- DEBUG level for tier evaluation logic
- INFO level for business operations
- ERROR level for exceptions

Check logs for detailed operation traces.
