package com.firstclub.membership.config;

import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.PlanDuration;
import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;
    private final TierUpgradeCriteriaRepository criteriaRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing demo data...");

        initializePlans();
        initializeTiers();
        initializeUpgradeCriteria();
        initializeDemoUsers();

        log.info("Demo data initialized successfully!");
    }

    private void initializePlans() {
        if (planRepository.count() > 0) {
            return;
        }

        MembershipPlan monthly = MembershipPlan.builder()
                .name("Monthly Plan")
                .duration(PlanDuration.MONTHLY)
                .price(new BigDecimal("9.99"))
                .description("Pay monthly, cancel anytime")
                .active(true)
                .build();

        MembershipPlan quarterly = MembershipPlan.builder()
                .name("Quarterly Plan")
                .duration(PlanDuration.QUARTERLY)
                .price(new BigDecimal("24.99"))
                .description("Save 17% with quarterly billing")
                .active(true)
                .build();

        MembershipPlan yearly = MembershipPlan.builder()
                .name("Yearly Plan")
                .duration(PlanDuration.YEARLY)
                .price(new BigDecimal("89.99"))
                .description("Best value - Save 25% annually")
                .active(true)
                .build();

        planRepository.save(monthly);
        planRepository.save(quarterly);
        planRepository.save(yearly);

        log.info("Created 3 membership plans");
    }

    private void initializeTiers() {
        if (tierRepository.count() > 0) {
            return;
        }

        // SILVER Tier
        MembershipTier silver = MembershipTier.builder()
                .tierLevel(TierLevel.SILVER)
                .name("Silver Member")
                .description("Entry level membership with basic benefits")
                .build();

        TierBenefit silverDiscount = TierBenefit.builder()
                .benefitType("DISCOUNT")
                .benefitValue("5")
                .description("5% discount on all items")
                .build();

        TierBenefit silverDelivery = TierBenefit.builder()
                .benefitType("FREE_DELIVERY")
                .benefitValue("true")
                .description("Free delivery on orders above $50")
                .build();

        silver.addBenefit(silverDiscount);
        silver.addBenefit(silverDelivery);

        // GOLD Tier
        MembershipTier gold = MembershipTier.builder()
                .tierLevel(TierLevel.GOLD)
                .name("Gold Member")
                .description("Mid-tier membership with enhanced benefits")
                .build();

        TierBenefit goldDiscount = TierBenefit.builder()
                .benefitType("DISCOUNT")
                .benefitValue("10")
                .description("10% discount on all items")
                .build();

        TierBenefit goldDelivery = TierBenefit.builder()
                .benefitType("FREE_DELIVERY")
                .benefitValue("true")
                .description("Free delivery on all orders")
                .build();

        TierBenefit goldSupport = TierBenefit.builder()
                .benefitType("PRIORITY_SUPPORT")
                .benefitValue("true")
                .description("24/7 priority customer support")
                .build();

        gold.addBenefit(goldDiscount);
        gold.addBenefit(goldDelivery);
        gold.addBenefit(goldSupport);

        // PLATINUM Tier
        MembershipTier platinum = MembershipTier.builder()
                .tierLevel(TierLevel.PLATINUM)
                .name("Platinum Member")
                .description("Premium membership with exclusive benefits")
                .build();

        TierBenefit platinumDiscount = TierBenefit.builder()
                .benefitType("DISCOUNT")
                .benefitValue("15")
                .description("15% discount on all items")
                .build();

        TierBenefit platinumDelivery = TierBenefit.builder()
                .benefitType("FREE_DELIVERY")
                .benefitValue("true")
                .description("Free express delivery on all orders")
                .build();

        TierBenefit platinumSupport = TierBenefit.builder()
                .benefitType("PRIORITY_SUPPORT")
                .benefitValue("true")
                .description("Dedicated account manager")
                .build();

        TierBenefit platinumEarlyAccess = TierBenefit.builder()
                .benefitType("EARLY_ACCESS")
                .benefitValue("true")
                .description("Early access to sales and exclusive deals")
                .build();

        platinum.addBenefit(platinumDiscount);
        platinum.addBenefit(platinumDelivery);
        platinum.addBenefit(platinumSupport);
        platinum.addBenefit(platinumEarlyAccess);

        tierRepository.save(silver);
        tierRepository.save(gold);
        tierRepository.save(platinum);

        log.info("Created 3 membership tiers with benefits");
    }

    private void initializeUpgradeCriteria() {
        if (criteriaRepository.count() > 0) {
            return;
        }

        // Criteria to upgrade to GOLD
        TierUpgradeCriteria goldCriteria = TierUpgradeCriteria.builder()
                .targetTier(TierLevel.GOLD)
                .minOrderCount(5)
                .minMonthlyOrderValue(new BigDecimal("200.00"))
                .description("Make 5 orders with total value of $200+ in a month")
                .active(true)
                .build();

        // Criteria to upgrade to PLATINUM
        TierUpgradeCriteria platinumCriteria = TierUpgradeCriteria.builder()
                .targetTier(TierLevel.PLATINUM)
                .minOrderCount(10)
                .minMonthlyOrderValue(new BigDecimal("500.00"))
                .eligibleCohort("premium,vip")
                .description("Make 10 orders with total value of $500+ in a month (Premium/VIP cohort)")
                .active(true)
                .build();

        criteriaRepository.save(goldCriteria);
        criteriaRepository.save(platinumCriteria);

        log.info("Created tier upgrade criteria");
    }

    private void initializeDemoUsers() {
        if (userRepository.count() > 0) {
            return;
        }

        User user1 = User.builder()
                .email("john.doe@example.com")
                .name("John Doe")
                .cohort("regular")
                .build();

        User user2 = User.builder()
                .email("jane.smith@example.com")
                .name("Jane Smith")
                .cohort("premium")
                .build();

        User user3 = User.builder()
                .email("bob.wilson@example.com")
                .name("Bob Wilson")
                .cohort("vip")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        log.info("Created 3 demo users");
    }
}
