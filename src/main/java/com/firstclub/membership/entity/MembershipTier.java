package com.firstclub.membership.entity;

import com.firstclub.membership.enums.TierLevel;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "membership_tiers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TierLevel tierLevel;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "tier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<TierBenefit> benefits = new HashSet<>();

    public void addBenefit(TierBenefit benefit) {
        benefits.add(benefit);
        benefit.setTier(this);
    }

    public void removeBenefit(TierBenefit benefit) {
        benefits.remove(benefit);
        benefit.setTier(null);
    }
}
