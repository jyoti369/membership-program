package com.firstclub.membership.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tier_benefits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TierBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MembershipTier tier;

    @Column(nullable = false)
    private String benefitType; // e.g., "DISCOUNT", "FREE_DELIVERY", "PRIORITY_SUPPORT"

    @Column(nullable = false)
    private String benefitValue; // e.g., "10", "true", "24h"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String applicableCategory; // null means all categories
}
