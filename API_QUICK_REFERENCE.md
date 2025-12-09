# API Quick Reference

## Base URL
```
http://localhost:8080
```

## Important Note
The root path (`http://localhost:8080`) will return an error because there's no endpoint defined there.
All APIs are under `/api/*` paths.

---

## 📋 All Available Endpoints

### Membership Plans & Tiers

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/plans` | Get all active membership plans |
| GET | `/api/plans/{id}` | Get specific plan by ID |
| GET | `/api/plans/tiers` | Get all tiers with benefits |

### Membership Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/memberships/subscribe` | Subscribe user to a plan |
| GET | `/api/memberships/user/{userId}` | Get current membership |
| PUT | `/api/memberships/user/{userId}/upgrade?tierLevel=GOLD` | Manually upgrade tier |
| PUT | `/api/memberships/user/{userId}/downgrade?tierLevel=SILVER` | Manually downgrade tier |
| DELETE | `/api/memberships/user/{userId}/cancel` | Cancel membership |
| POST | `/api/memberships/user/{userId}/evaluate-tier` | Auto-evaluate and upgrade tier |
| GET | `/api/memberships/user/{userId}/eligible-tier` | Check eligible tier |

### Orders & Benefits

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Create order with auto-applied benefits |
| GET | `/api/orders/benefits/free-delivery?userId={id}&category={cat}` | Check free delivery eligibility |
| GET | `/api/orders/benefits/discount?userId={id}&category={cat}` | Get applicable discount % |

---

## 🚀 Quick Test Commands

### 1. View Plans
```bash
curl http://localhost:8080/api/plans
```

### 2. View Tiers (Pretty)
```bash
curl http://localhost:8080/api/plans/tiers | python3 -m json.tool
```

### 3. Subscribe User
```bash
curl -X POST http://localhost:8080/api/memberships/subscribe \
  -H 'Content-Type: application/json' \
  -d '{"userId": 2, "planId": 1}'
```

### 4. Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"userId": 2, "orderValue": 75.00, "category": "electronics"}'
```

### 5. Check Membership
```bash
curl http://localhost:8080/api/memberships/user/2
```

### 6. Check Free Delivery Eligibility
```bash
curl "http://localhost:8080/api/orders/benefits/free-delivery?userId=2&category=electronics"
```

### 7. Check Discount
```bash
curl "http://localhost:8080/api/orders/benefits/discount?userId=2"
```

### 8. Evaluate Tier
```bash
curl -X POST http://localhost:8080/api/memberships/user/2/evaluate-tier
```

---

## 📝 Request Body Examples

### Subscribe Request
```json
{
  "userId": 1,
  "planId": 1
}
```

### Create Order Request
```json
{
  "userId": 1,
  "orderValue": 100.00,
  "category": "electronics"
}
```

---

## 🎯 Complete Demo Flow

```bash
# 1. View available plans
curl http://localhost:8080/api/plans

# 2. Subscribe user 3 to yearly plan
curl -X POST http://localhost:8080/api/memberships/subscribe \
  -H 'Content-Type: application/json' \
  -d '{"userId": 3, "planId": 3}'

# 3. Create first order (5% discount with SILVER)
curl -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"userId": 3, "orderValue": 60.00}'

# 4. Create 4 more orders to meet GOLD criteria
for i in {1..4}; do
  curl -X POST http://localhost:8080/api/orders \
    -H 'Content-Type: application/json' \
    -d '{"userId": 3, "orderValue": 50.00}'
done

# 5. Evaluate tier (should upgrade to GOLD)
curl -X POST http://localhost:8080/api/memberships/user/3/evaluate-tier

# 6. Verify upgrade
curl http://localhost:8080/api/memberships/user/3

# 7. Create order with new 10% discount
curl -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"userId": 3, "orderValue": 100.00}'
```

---

## 🔍 H2 Database Console

Access the database directly:

**URL:** http://localhost:8080/h2-console

**Connection Settings:**
- JDBC URL: `jdbc:h2:mem:membershipdb`
- Username: `sa`
- Password: (leave blank)

**Tables to explore:**
- `users`
- `memberships`
- `membership_plans`
- `membership_tiers`
- `tier_benefits`
- `orders`
- `tier_upgrade_criteria`

---

## 💡 Tips

1. **Pretty Print JSON:**
   ```bash
   curl http://localhost:8080/api/plans | python3 -m json.tool
   ```

2. **Save Response to File:**
   ```bash
   curl http://localhost:8080/api/plans > plans.json
   ```

3. **Check HTTP Status:**
   ```bash
   curl -i http://localhost:8080/api/plans
   ```

4. **Verbose Output:**
   ```bash
   curl -v http://localhost:8080/api/plans
   ```

---

## ⚠️ Common Errors

### Error: Internal Server Error at root path
```bash
curl http://localhost:8080
# ❌ Returns: {"error":"Internal Server Error"...}
```

**Solution:** Use proper API endpoints under `/api/*`
```bash
curl http://localhost:8080/api/plans
# ✅ Works!
```

### Error: User already has active membership
```json
{
  "error": "Conflict",
  "message": "User already has an active membership"
}
```

**Solution:** Either cancel existing membership or use a different user ID

### Error: Cannot upgrade to SILVER (already at GOLD)
**Solution:** Use downgrade endpoint or choose a higher tier

---

## 📦 Demo Users Pre-loaded

| User ID | Email | Cohort |
|---------|-------|--------|
| 1 | john.doe@example.com | regular |
| 2 | jane.smith@example.com | premium |
| 3 | bob.wilson@example.com | vip |

---

## 🎓 Tier Upgrade Criteria

### GOLD Tier
- **Order Count:** 5+ orders this month
- **Order Value:** $200+ total this month
- **Cohort:** Any

### PLATINUM Tier
- **Order Count:** 10+ orders this month
- **Order Value:** $500+ total this month
- **Cohort:** premium OR vip only

---

## 🔧 Application Control

### Stop Application
Press `Ctrl+C` in the terminal where it's running

### Restart Application
```bash
cd /Users/debojyoti.mandal/personal/membership-program
./run.sh
```

### Build Only
```bash
./run.sh build
```

### Clean Build
```bash
./run.sh clean
```

---

## 📊 Monitor Application Logs

Watch logs in real-time to see tier evaluations, benefit calculations, etc:
```bash
# Application logs show:
# - User subscriptions
# - Order creations
# - Benefit applications
# - Tier upgrades
# - Database operations
```

All logs are visible in the terminal where the application is running.

---

## ✅ Verification Checklist

After running the demo flow, verify:

- [ ] User subscribed with SILVER tier
- [ ] First order got 5% discount
- [ ] After 5+ orders and $200+, tier auto-upgraded to GOLD
- [ ] Next order got 10% discount
- [ ] Benefits include priority support
- [ ] Free delivery applied to all orders
