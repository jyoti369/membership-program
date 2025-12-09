package com.firstclub.membership.entity;

import com.firstclub.membership.enums.TierLevel;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tier_upgrade_criteria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TierUpgradeCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TierLevel targetTier;

    @Column(name = "min_order_count")
    private Integer minOrderCount;

    @Column(name = "min_monthly_order_value", precision = 10, scale = 2)
    private BigDecimal minMonthlyOrderValue;

    @Column(name = "eligible_cohort")
    private String eligibleCohort; // Comma-separated cohorts, null means any

    @Column(nullable = false)
    private Boolean active = true;

    @Column(columnDefinition = "TEXT")
    private String description;
}
