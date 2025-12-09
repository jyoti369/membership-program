# System Architecture

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     REST API Layer                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Plan       │  │  Membership  │  │    Order     │      │
│  │  Controller  │  │  Controller  │  │  Controller  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    Service Layer                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Plan       │  │  Membership  │  │   Benefit    │      │
│  │   Service    │  │   Service    │  │  Calculation │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                                                               │
│  ┌──────────────┐  ┌──────────────┐                         │
│  │    Tier      │  │    Order     │                         │
│  │  Evaluation  │  │   Service    │                         │
│  └──────────────┘  └──────────────┘                         │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                  Strategy Layer                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ OrderCount   │  │ OrderValue   │  │   Cohort     │      │
│  │   Strategy   │  │   Strategy   │  │   Strategy   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│           (All implement TierEvaluationStrategy)            │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                 Repository Layer                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │     User     │  │  Membership  │  │    Order     │      │
│  │  Repository  │  │  Repository  │  │  Repository  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    Database (H2)                             │
│  Tables: users, memberships, membership_plans,               │
│  membership_tiers, tier_benefits, orders,                    │
│  tier_upgrade_criteria                                       │
└─────────────────────────────────────────────────────────────┘
```

## Domain Model

```
┌─────────────┐          ┌─────────────────┐
│    User     │          │ MembershipPlan  │
├─────────────┤          ├─────────────────┤
│ id          │          │ id              │
│ email       │          │ name            │
│ name        │          │ duration        │
│ cohort      │          │ price           │
└─────────────┘          │ description     │
      │                  └─────────────────┘
      │ 1                         │
      │                           │
      │                           │ *
      │                  ┌─────────────────┐
      │ 1                │  Membership     │
      └──────────────────┤─────────────────┤
                   *     │ id              │
      ┌─────────────────│ user            │
      │                 │ plan            │
      │                 │ tier            │
      │                 │ status          │
      │                 │ startDate       │
      │                 │ expiryDate      │
      │                 │ version (lock)  │
      │                 └─────────────────┘
      │                         │ *
┌─────────────┐                 │
│    Order    │                 │
├─────────────┤                 │ 1
│ id          │          ┌──────────────────┐
│ user        │          │ MembershipTier   │
│ orderValue  │          ├──────────────────┤
│ category    │          │ id               │
│ discount    │          │ tierLevel        │
└─────────────┘          │ name             │
                         │ description      │
                         └──────────────────┘
                                  │ 1
                                  │
                                  │ *
                         ┌──────────────────┐
                         │   TierBenefit    │
                         ├──────────────────┤
                         │ id               │
                         │ tier             │
                         │ benefitType      │
                         │ benefitValue     │
                         │ description      │
                         │ applicableCategory│
                         └──────────────────┘
```

## Design Patterns

### 1. Strategy Pattern - Tier Evaluation

```
┌──────────────────────────┐
│ TierEvaluationStrategy   │◄───────────────┐
├──────────────────────────┤                │
│ + evaluate()             │                │
│ + getStrategyName()      │                │
└──────────────────────────┘                │
           △                                 │
           │                                 │
           │ implements                      │
           │                                 │
    ┌──────┴──────────┬──────────────┐     │
    │                 │              │     │
┌───────────┐  ┌──────────┐  ┌────────────┐│
│OrderCount │  │OrderValue│  │  Cohort    ││
│ Strategy  │  │ Strategy │  │  Strategy  ││
└───────────┘  └──────────┘  └────────────┘│
                                            │
                                            │
                     ┌──────────────────────┴─┐
                     │ TierEvaluationService  │
                     ├────────────────────────┤
                     │ - strategies: List     │
                     │ + evaluateAndUpgrade() │
                     └────────────────────────┘
```

**Benefits:**
- Easy to add new evaluation criteria
- Each strategy is independent and testable
- Open/Closed Principle - open for extension, closed for modification

### 2. Builder Pattern - Entity Construction

```java
User user = User.builder()
    .email("john@example.com")
    .name("John Doe")
    .cohort("premium")
    .build();
```

**Benefits:**
- Readable and maintainable code
- Immutable object construction
- Optional parameters handling

### 3. Repository Pattern - Data Access

```
Service Layer ──► Repository Interface ──► JPA Implementation ──► Database
```

**Benefits:**
- Abstraction over data access
- Easy to switch database implementations
- Testability with mocking

### 4. Service Layer Pattern

```
Controller ──► Service ──► Repository
```

**Benefits:**
- Business logic separation
- Transaction management
- Reusability

## Key Architectural Decisions

### 1. Optimistic Locking for Concurrency

**Why?**
- Better performance than pessimistic locking
- Suitable for read-heavy scenarios
- Automatic retry mechanism handles conflicts

**Implementation:**
```java
@Version
private Long version;
```

**Flow:**
```
Request 1: Read membership (version=1) → Update tier → Save (version=2) ✓
Request 2: Read membership (version=1) → Update tier → Save (CONFLICT!)
           → Retry → Read (version=2) → Update → Save (version=3) ✓
```

### 2. Configurable Benefits

**Why?**
- No code changes needed to add benefits
- Tier benefits stored as key-value pairs
- Flexible benefit types

**Schema:**
```
tier_benefits
├── benefit_type (e.g., "DISCOUNT", "FREE_DELIVERY")
├── benefit_value (e.g., "10", "true")
├── description
└── applicable_category (nullable)
```

### 3. Separate Plan and Tier Concepts

**Why?**
- Plan = billing cycle (monthly/quarterly/yearly)
- Tier = benefit level (silver/gold/platinum)
- Users can be on any tier regardless of plan
- More flexible business model

### 4. Strategy Pattern for Extensibility

**Why?**
- Easy to add new tier criteria
- No modification to existing code
- Testable in isolation

**Adding a new strategy:**
```java
@Component
public class CustomStrategy implements TierEvaluationStrategy {
    public boolean evaluate(User user, TierUpgradeCriteria criteria) {
        // Custom logic
    }
}
// Automatically picked up by Spring!
```

## API Design Principles

### RESTful Conventions

```
GET    /api/plans                    - List resources
GET    /api/plans/{id}               - Get specific resource
POST   /api/memberships/subscribe    - Create resource
PUT    /api/memberships/{id}/upgrade - Update resource
DELETE /api/memberships/{id}/cancel  - Delete/Cancel resource
```

### Response Structure

**Success:**
```json
{
  "id": 1,
  "name": "...",
  ...
}
```

**Error:**
```json
{
  "timestamp": "2025-12-09T...",
  "status": 400,
  "error": "Bad Request",
  "message": "User already has an active membership"
}
```

## Scalability Considerations

### Current Implementation (Single Instance)

```
Client → Spring Boot App → H2 Database
```

### Production Architecture (Scalable)

```
            ┌──────────────┐
            │ Load Balancer│
            └───────┬──────┘
                    │
        ┌───────────┼───────────┐
        ▼           ▼           ▼
    ┌─────┐     ┌─────┐     ┌─────┐
    │App 1│     │App 2│     │App 3│
    └──┬──┘     └──┬──┘     └──┬──┘
       │           │           │
       └───────────┼───────────┘
                   ▼
        ┌─────────────────────┐
        │ PostgreSQL/MySQL    │
        │ (with replication)  │
        └─────────────────────┘
```

**Changes needed:**
- Replace H2 with production database
- Add caching layer (Redis) for tier benefits
- Implement distributed locking for tier upgrades
- Add message queue for async tier evaluation

## Security Considerations

### Authentication & Authorization (Not Implemented)

**Future additions:**
```java
@PreAuthorize("hasRole('USER')")
public MembershipResponse getCurrentMembership(Long userId) {
    // Ensure userId matches authenticated user
}
```

### Data Validation

- Input validation with `@Valid`
- Business rule validation in services
- Database constraints

### SQL Injection Prevention

- JPA parameterized queries
- No raw SQL strings

## Monitoring & Observability

### Logging Strategy

- **DEBUG**: Tier evaluation details
- **INFO**: Business operations (subscribe, upgrade)
- **ERROR**: Exceptions and failures

### Metrics (Future)

- Membership subscription rate
- Tier distribution
- Benefit usage statistics
- API response times

## Testing Strategy

### Unit Tests (Recommended)

- Service layer business logic
- Strategy implementations
- Benefit calculations

### Integration Tests (Recommended)

- API endpoint testing
- Repository operations
- End-to-end flows

### Concurrency Tests (Recommended)

- Optimistic lock handling
- Simultaneous tier upgrades
- Race condition scenarios

## Extensibility Points

### 1. Add New Benefit Type

1. Add benefit to tier in database
2. Update `BenefitCalculationService`
3. No changes to entities or repositories

### 2. Add New Tier Evaluation Strategy

1. Create class implementing `TierEvaluationStrategy`
2. Add `@Component` annotation
3. Spring auto-discovers and uses it

### 3. Add New Plan Duration

1. Add enum value to `PlanDuration`
2. Create plan in database
3. No code changes needed

### 4. Add Scheduled Tier Evaluation

```java
@Scheduled(cron = "0 0 0 * * *") // Daily at midnight
public void evaluateAllUserTiers() {
    userRepository.findAll().forEach(user ->
        tierEvaluationService.evaluateAndUpgradeTier(user.getId())
    );
}
```

## Performance Optimizations

### Current

- Eager loading of tier benefits (small dataset)
- Optimistic locking (minimal lock contention)
- Index on user_id in memberships table

### Future Optimizations

- Caching tier benefits (rarely change)
- Batch tier evaluation
- Database query optimization
- Connection pooling tuning

## Conclusion

This architecture provides:

✅ **Modularity** - Clear separation of concerns
✅ **Extensibility** - Easy to add features
✅ **Testability** - Independent components
✅ **Maintainability** - Clean code structure
✅ **Scalability** - Can be scaled horizontally
✅ **Reliability** - Concurrency handling
✅ **Performance** - Optimistic locking
