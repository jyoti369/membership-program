package com.firstclub.membership.service;

import com.firstclub.membership.dto.BenefitResponse;
import com.firstclub.membership.dto.MembershipResponse;
import com.firstclub.membership.dto.SubscriptionRequest;
import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.repository.*;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;
    private final UserRepository userRepository;
    private final TierEvaluationService tierEvaluationService;

    private static final int MAX_RETRY_ATTEMPTS = 3;

    /**
     * Subscribe a user to a membership plan.
     */
    @Transactional
    public MembershipResponse subscribe(SubscriptionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if user already has an active membership
        if (membershipRepository.existsByUserAndStatus(user, MembershipStatus.ACTIVE)) {
            throw new IllegalStateException("User already has an active membership");
        }

        MembershipPlan plan = planRepository.findByIdAndActiveTrue(request.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found or inactive"));

        // Default tier is SILVER for new subscriptions
        MembershipTier tier = tierRepository.findByTierLevel(TierLevel.SILVER)
                .orElseThrow(() -> new IllegalStateException("SILVER tier not found"));

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime expiryDate = startDate.plusMonths(plan.getDuration().getMonths());

        Membership membership = Membership.builder()
                .user(user)
                .plan(plan)
                .tier(tier)
                .status(MembershipStatus.ACTIVE)
                .startDate(startDate)
                .expiryDate(expiryDate)
                .build();

        membership = membershipRepository.save(membership);
        log.info("User {} subscribed to plan {} with tier {}", user.getId(), plan.getName(), tier.getName());

        return convertToResponse(membership);
    }

    /**
     * Upgrade membership tier manually.
     */
    @Transactional
    public MembershipResponse upgradeTier(Long userId, TierLevel newTierLevel) {
        return upgradeTierWithRetry(userId, newTierLevel, 0);
    }

    private MembershipResponse upgradeTierWithRetry(Long userId, TierLevel newTierLevel, int attempt) {
        try {
            Membership membership = membershipRepository.findByUserIdWithLock(userId)
                    .orElseThrow(() -> new IllegalArgumentException("No membership found for user"));

            if (!membership.isActive()) {
                throw new IllegalStateException("Membership is not active");
            }

            if (!membership.canUpgradeTo(newTierLevel)) {
                throw new IllegalArgumentException("Cannot upgrade to " + newTierLevel);
            }

            MembershipTier newTier = tierRepository.findByTierLevel(newTierLevel)
                    .orElseThrow(() -> new IllegalStateException("Tier not found: " + newTierLevel));

            membership.setTier(newTier);
            membership = membershipRepository.save(membership);

            log.info("User {} upgraded to tier {}", userId, newTierLevel);
            return convertToResponse(membership);

        } catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
            if (attempt < MAX_RETRY_ATTEMPTS) {
                log.warn("Optimistic lock exception on attempt {}. Retrying...", attempt + 1);
                try {
                    Thread.sleep(100 * (attempt + 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                return upgradeTierWithRetry(userId, newTierLevel, attempt + 1);
            }
            throw new RuntimeException("Failed to upgrade tier due to concurrent modification", e);
        }
    }

    /**
     * Downgrade membership tier.
     */
    @Transactional
    public MembershipResponse downgradeTier(Long userId, TierLevel newTierLevel) {
        return downgradeTierWithRetry(userId, newTierLevel, 0);
    }

    private MembershipResponse downgradeTierWithRetry(Long userId, TierLevel newTierLevel, int attempt) {
        try {
            Membership membership = membershipRepository.findByUserIdWithLock(userId)
                    .orElseThrow(() -> new IllegalArgumentException("No membership found for user"));

            if (!membership.isActive()) {
                throw new IllegalStateException("Membership is not active");
            }

            if (!membership.canDowngradeTo(newTierLevel)) {
                throw new IllegalArgumentException("Cannot downgrade to " + newTierLevel);
            }

            MembershipTier newTier = tierRepository.findByTierLevel(newTierLevel)
                    .orElseThrow(() -> new IllegalStateException("Tier not found: " + newTierLevel));

            membership.setTier(newTier);
            membership = membershipRepository.save(membership);

            log.info("User {} downgraded to tier {}", userId, newTierLevel);
            return convertToResponse(membership);

        } catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
            if (attempt < MAX_RETRY_ATTEMPTS) {
                log.warn("Optimistic lock exception on attempt {}. Retrying...", attempt + 1);
                try {
                    Thread.sleep(100 * (attempt + 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                return downgradeTierWithRetry(userId, newTierLevel, attempt + 1);
            }
            throw new RuntimeException("Failed to downgrade tier due to concurrent modification", e);
        }
    }

    /**
     * Cancel membership.
     */
    @Transactional
    public MembershipResponse cancelMembership(Long userId) {
        Membership membership = membershipRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No membership found for user"));

        membership.setStatus(MembershipStatus.CANCELLED);
        membership = membershipRepository.save(membership);

        log.info("User {} cancelled membership", userId);
        return convertToResponse(membership);
    }

    /**
     * Get current membership for user.
     */
    @Transactional(readOnly = true)
    public MembershipResponse getCurrentMembership(Long userId) {
        Membership membership = membershipRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No membership found for user"));

        return convertToResponse(membership);
    }

    private MembershipResponse convertToResponse(Membership membership) {
        return MembershipResponse.builder()
                .membershipId(membership.getId())
                .userId(membership.getUser().getId())
                .userName(membership.getUser().getName())
                .userEmail(membership.getUser().getEmail())
                .planName(membership.getPlan().getName())
                .planDuration(membership.getPlan().getDuration())
                .tierLevel(membership.getTier().getTierLevel())
                .tierName(membership.getTier().getName())
                .status(membership.getStatus())
                .startDate(membership.getStartDate())
                .expiryDate(membership.getExpiryDate())
                .isActive(membership.isActive())
                .benefits(membership.getTier().getBenefits().stream()
                        .map(b -> BenefitResponse.builder()
                                .benefitType(b.getBenefitType())
                                .benefitValue(b.getBenefitValue())
                                .description(b.getDescription())
                                .applicableCategory(b.getApplicableCategory())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
