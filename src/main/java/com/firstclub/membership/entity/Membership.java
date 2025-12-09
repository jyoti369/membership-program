package com.firstclub.membership.entity;

import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.enums.TierLevel;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "memberships")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private MembershipPlan plan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;

    @Version
    private Long version; // For optimistic locking to handle concurrency

    @PrePersist
    protected void onCreate() {
        lastModified = LocalDateTime.now();
        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastModified = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == MembershipStatus.ACTIVE && LocalDateTime.now().isBefore(expiryDate);
    }

    public boolean canUpgradeTo(TierLevel newTier) {
        return newTier.isHigherThan(tier.getTierLevel());
    }

    public boolean canDowngradeTo(TierLevel newTier) {
        return newTier.isLowerThan(tier.getTierLevel());
    }
}
