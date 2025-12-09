# API Testing Examples

This document contains practical examples for testing all API endpoints.

## Prerequisites

- Application running on `http://localhost:8080`
- curl or Postman installed

## Demo Users (Pre-loaded)

1. User ID: 1 - john.doe@example.com (cohort: regular)
2. User ID: 2 - jane.smith@example.com (cohort: premium)
3. User ID: 3 - bob.wilson@example.com (cohort: vip)

## Demo Plans (Pre-loaded)

1. Plan ID: 1 - Monthly Plan ($9.99)
2. Plan ID: 2 - Quarterly Plan ($24.99)
3. Plan ID: 3 - Yearly Plan ($89.99)

---

## 1. Membership Plans & Tiers

### Get All Plans

```bash
curl http://localhost:8080/api/plans
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "name": "Monthly Plan",
    "duration": "MONTHLY",
    "price": 9.99,
    "description": "Pay monthly, cancel anytime"
  },
  ...
]
```

### Get Specific Plan

```bash
curl http://localhost:8080/api/plans/1
```

### Get All Tiers

```bash
curl http://localhost:8080/api/plans/tiers
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "tierLevel": "SILVER",
    "name": "Silver Member",
    "description": "Entry level membership with basic benefits",
    "benefits": [
      {
        "benefitType": "DISCOUNT",
        "benefitValue": "5",
        "description": "5% discount on all items",
        "applicableCategory": null
      },
      ...
    ]
  },
  ...
]
```

---

## 2. Membership Subscription Flow

### Step 1: Subscribe User to a Plan

```bash
curl -X POST http://localhost:8080/api/memberships/subscribe \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "planId": 1
  }'
```

**Expected Response:**
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
  "startDate": "2025-12-09T...",
  "expiryDate": "2026-01-09T...",
  "benefits": [...],
  "isActive": true
}
```

### Step 2: Check Current Membership

```bash
curl http://localhost:8080/api/memberships/user/1
```

---

## 3. Orders with Benefits

### Create Order (Benefits Auto-Applied)

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "orderValue": 100.00,
    "category": "electronics"
  }'
```

**Expected Response:**
```json
{
  "orderId": 1,
  "userId": 1,
  "orderValue": 100.00,
  "category": "electronics",
  "orderDate": "2025-12-09T...",
  "freeDeliveryApplied": true,
  "discountPercentage": 5,
  "discountAmount": 5.00,
  "finalAmount": 95.00
}
```

### Check Free Delivery Eligibility

```bash
curl "http://localhost:8080/api/orders/benefits/free-delivery?userId=1&category=electronics"
```

### Check Applicable Discount

```bash
curl "http://localhost:8080/api/orders/benefits/discount?userId=1"
```

---

## 4. Tier Management

### Manual Tier Upgrade

```bash
curl -X PUT "http://localhost:8080/api/memberships/user/1/upgrade?tierLevel=GOLD"
```

### Manual Tier Downgrade

```bash
curl -X PUT "http://localhost:8080/api/memberships/user/1/downgrade?tierLevel=SILVER"
```

### Check Eligible Tier

```bash
curl http://localhost:8080/api/memberships/user/1/eligible-tier
```

---

## 5. Automatic Tier Upgrade Testing

### Complete Flow to Auto-Upgrade to GOLD

```bash
# 1. Subscribe user 2
curl -X POST http://localhost:8080/api/memberships/subscribe \
  -H "Content-Type: application/json" \
  -d '{"userId": 2, "planId": 2}'

# 2. Create 5 orders totaling $200+ (to meet GOLD criteria)
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 2, "orderValue": 50.00, "category": "books"}'

curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 2, "orderValue": 50.00, "category": "electronics"}'

curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 2, "orderValue": 50.00, "category": "clothing"}'

curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 2, "orderValue": 30.00, "category": "books"}'

curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 2, "orderValue": 25.00, "category": "electronics"}'

# 3. Evaluate tier (should auto-upgrade to GOLD)
curl -X POST http://localhost:8080/api/memberships/user/2/evaluate-tier

# 4. Verify membership tier is now GOLD
curl http://localhost:8080/api/memberships/user/2
```

**Expected Response from evaluate-tier:**
```json
{
  "success": true,
  "message": "Tier upgraded to GOLD",
  "newTier": "GOLD"
}
```

### Complete Flow to Auto-Upgrade to PLATINUM

```bash
# 1. Subscribe user 3 (VIP cohort)
curl -X POST http://localhost:8080/api/memberships/subscribe \
  -H "Content-Type: application/json" \
  -d '{"userId": 3, "planId": 3}'

# 2. Create 10 orders totaling $500+ (to meet PLATINUM criteria)
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/orders \
    -H "Content-Type: application/json" \
    -d '{"userId": 3, "orderValue": 55.00, "category": "premium"}'
done

# 3. Evaluate tier (should auto-upgrade to PLATINUM)
curl -X POST http://localhost:8080/api/memberships/user/3/evaluate-tier

# 4. Verify membership tier is now PLATINUM
curl http://localhost:8080/api/memberships/user/3
```

---

## 6. Membership Cancellation

### Cancel Membership

```bash
curl -X DELETE http://localhost:8080/api/memberships/user/1/cancel
```

**Expected Response:**
```json
{
  "membershipId": 1,
  "userId": 1,
  "status": "CANCELLED",
  ...
}
```

---

## 7. Error Scenarios

### Try to Subscribe When Already Subscribed

```bash
# Subscribe user 1
curl -X POST http://localhost:8080/api/memberships/subscribe \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "planId": 1}'

# Try to subscribe again (should fail)
curl -X POST http://localhost:8080/api/memberships/subscribe \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "planId": 1}'
```

**Expected Error:**
```json
{
  "timestamp": "2025-12-09T...",
  "status": 409,
  "error": "Conflict",
  "message": "User already has an active membership"
}
```

### Invalid Tier Upgrade

```bash
# Try to upgrade to SILVER when already GOLD
curl -X PUT "http://localhost:8080/api/memberships/user/1/upgrade?tierLevel=SILVER"
```

**Expected Error:**
```json
{
  "timestamp": "2025-12-09T...",
  "status": 400,
  "error": "Bad Request",
  "message": "Cannot upgrade to SILVER"
}
```

---

## 8. Postman Collection

Import this JSON into Postman for easy testing:

```json
{
  "info": {
    "name": "FirstClub Membership API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Get All Plans",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/api/plans"
      }
    },
    {
      "name": "Subscribe",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/api/memberships/subscribe",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"userId\": 1,\n  \"planId\": 1\n}"
        }
      }
    },
    {
      "name": "Create Order",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/api/orders",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"userId\": 1,\n  \"orderValue\": 100.00,\n  \"category\": \"electronics\"\n}"
        }
      }
    }
  ]
}
```

---

## 9. Testing Concurrency

To test the optimistic locking mechanism, you need to simulate concurrent requests. Here's a simple test:

```bash
# Run these two commands simultaneously in different terminals
# Both try to upgrade the same user's tier at the same time

# Terminal 1:
curl -X PUT "http://localhost:8080/api/memberships/user/1/upgrade?tierLevel=GOLD"

# Terminal 2 (run immediately after):
curl -X PUT "http://localhost:8080/api/memberships/user/1/upgrade?tierLevel=GOLD"
```

One request should succeed, the other will retry and handle the optimistic lock exception gracefully.

---

## Notes

- All timestamps are in ISO-8601 format
- Tier upgrade criteria evaluates orders from the current month
- Benefits are automatically applied when creating orders
- The system uses optimistic locking for concurrency control
