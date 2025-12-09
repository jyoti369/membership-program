package com.firstclub.membership.service;

import com.firstclub.membership.entity.Membership;
import com.firstclub.membership.entity.Order;
import com.firstclub.membership.entity.TierBenefit;
import com.firstclub.membership.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * Service for calculating benefits based on membership tier.
 * Uses Strategy-like pattern for different benefit types.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BenefitCalculationService {

    private final MembershipRepository membershipRepository;

    /**
     * Apply membership benefits to an order.
     */
    @Transactional
    public Order applyBenefits(Order order) {
        Optional<Membership> membershipOpt = membershipRepository.findByUserId(order.getUser().getId());

        if (membershipOpt.isEmpty() || !membershipOpt.get().isActive()) {
            log.debug("No active membership for user {}", order.getUser().getId());
            return order;
        }

        Membership membership = membershipOpt.get();

        // Apply free delivery benefit
        applyFreeDelivery(order, membership);

        // Apply discount benefit
        applyDiscount(order, membership);

        log.info("Applied benefits to order {} for user {}", order.getId(), order.getUser().getId());
        return order;
    }

    /**
     * Check if user is eligible for free delivery.
     */
    public boolean isEligibleForFreeDelivery(Long userId, String category) {
        Optional<Membership> membershipOpt = membershipRepository.findByUserId(userId);

        if (membershipOpt.isEmpty() || !membershipOpt.get().isActive()) {
            return false;
        }

        Membership membership = membershipOpt.get();

        return membership.getTier().getBenefits().stream()
                .filter(b -> "FREE_DELIVERY".equals(b.getBenefitType()))
                .filter(b -> "true".equalsIgnoreCase(b.getBenefitValue()))
                .anyMatch(b -> b.getApplicableCategory() == null ||
                        b.getApplicableCategory().equalsIgnoreCase(category));
    }

    /**
     * Get applicable discount percentage for user and category.
     */
    public BigDecimal getApplicableDiscount(Long userId, String category) {
        Optional<Membership> membershipOpt = membershipRepository.findByUserId(userId);

        if (membershipOpt.isEmpty() || !membershipOpt.get().isActive()) {
            return BigDecimal.ZERO;
        }

        Membership membership = membershipOpt.get();

        return membership.getTier().getBenefits().stream()
                .filter(b -> "DISCOUNT".equals(b.getBenefitType()))
                .filter(b -> b.getApplicableCategory() == null ||
                        b.getApplicableCategory().equalsIgnoreCase(category))
                .map(b -> new BigDecimal(b.getBenefitValue()))
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private void applyFreeDelivery(Order order, Membership membership) {
        boolean hasFreeDelivery = membership.getTier().getBenefits().stream()
                .filter(b -> "FREE_DELIVERY".equals(b.getBenefitType()))
                .filter(b -> "true".equalsIgnoreCase(b.getBenefitValue()))
                .anyMatch(b -> isBenefitApplicable(b, order.getCategory()));

        if (hasFreeDelivery) {
            order.setFreeDeliveryApplied(true);
            log.debug("Free delivery applied for order {}", order.getId());
        }
    }

    private void applyDiscount(Order order, Membership membership) {
        Optional<TierBenefit> applicableBenefit = membership.getTier().getBenefits().stream()
                .filter(b -> "DISCOUNT".equals(b.getBenefitType()))
                .filter(b -> isBenefitApplicable(b, order.getCategory()))
                .findFirst();

        if (applicableBenefit.isPresent()) {
            BigDecimal discountPercent = new BigDecimal(applicableBenefit.get().getBenefitValue());
            BigDecimal discountAmount = order.getOrderValue()
                    .multiply(discountPercent)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            order.setDiscountPercentage(discountPercent);
            order.setDiscountAmount(discountAmount);

            log.debug("Applied {}% discount (amount: {}) for order {}",
                    discountPercent, discountAmount, order.getId());
        }
    }

    private boolean isBenefitApplicable(TierBenefit benefit, String orderCategory) {
        // If benefit has no category restriction, it applies to all
        if (benefit.getApplicableCategory() == null || benefit.getApplicableCategory().isBlank()) {
            return true;
        }

        // If order has no category, benefit only applies if it has no category restriction
        if (orderCategory == null || orderCategory.isBlank()) {
            return false;
        }

        return benefit.getApplicableCategory().equalsIgnoreCase(orderCategory);
    }
}
