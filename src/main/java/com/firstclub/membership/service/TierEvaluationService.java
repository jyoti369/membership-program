package com.firstclub.membership.service;

import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.repository.MembershipRepository;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.TierUpgradeCriteriaRepository;
import com.firstclub.membership.repository.UserRepository;
import com.firstclub.membership.strategy.TierEvaluationStrategy;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TierEvaluationService {

    private final List<TierEvaluationStrategy> evaluationStrategies;
    private final TierUpgradeCriteriaRepository criteriaRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipTierRepository tierRepository;
    private final UserRepository userRepository;

    private static final int MAX_RETRY_ATTEMPTS = 3;

    /**
     * Evaluates and automatically upgrades user tier based on criteria.
     * Uses optimistic locking to handle concurrent updates.
     */
    @Transactional
    public Optional<TierLevel> evaluateAndUpgradeTier(Long userId) {
        return evaluateAndUpgradeTierWithRetry(userId, 0);
    }

    private Optional<TierLevel> evaluateAndUpgradeTierWithRetry(Long userId, int attempt) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Optional<Membership> membershipOpt = membershipRepository.findByUserIdWithLock(userId);
            if (membershipOpt.isEmpty()) {
                log.warn("No active membership found for user {}", userId);
                return Optional.empty();
            }

            Membership membership = membershipOpt.get();
            TierLevel currentTier = membership.getTier().getTierLevel();

            // Find the highest tier user qualifies for
            TierLevel newTier = findHighestEligibleTier(user, currentTier);

            if (newTier != null && newTier.isHigherThan(currentTier)) {
                log.info("Upgrading user {} from {} to {}", userId, currentTier, newTier);

                MembershipTier tierEntity = tierRepository.findByTierLevel(newTier)
                        .orElseThrow(() -> new IllegalStateException("Tier not found: " + newTier));

                membership.setTier(tierEntity);
                membershipRepository.save(membership);

                return Optional.of(newTier);
            }

            return Optional.empty();

        } catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
            if (attempt < MAX_RETRY_ATTEMPTS) {
                log.warn("Optimistic lock exception on attempt {}. Retrying...", attempt + 1);
                try {
                    Thread.sleep(100 * (attempt + 1)); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                return evaluateAndUpgradeTierWithRetry(userId, attempt + 1);
            }
            log.error("Failed to upgrade tier after {} attempts", MAX_RETRY_ATTEMPTS);
            throw new RuntimeException("Failed to upgrade tier due to concurrent modification", e);
        }
    }

    /**
     * Finds the highest tier the user qualifies for.
     */
    private TierLevel findHighestEligibleTier(User user, TierLevel currentTier) {
        List<TierUpgradeCriteria> criteriaList = criteriaRepository.findByActiveTrue();

        TierLevel highestEligibleTier = null;

        for (TierUpgradeCriteria criteria : criteriaList) {
            if (criteria.getTargetTier().isHigherThan(currentTier) &&
                meetsAllCriteria(user, criteria)) {

                if (highestEligibleTier == null ||
                    criteria.getTargetTier().isHigherThan(highestEligibleTier)) {
                    highestEligibleTier = criteria.getTargetTier();
                }
            }
        }

        return highestEligibleTier;
    }

    /**
     * Checks if user meets all evaluation criteria using Strategy pattern.
     */
    private boolean meetsAllCriteria(User user, TierUpgradeCriteria criteria) {
        for (TierEvaluationStrategy strategy : evaluationStrategies) {
            if (!strategy.evaluate(user, criteria)) {
                log.debug("User {} failed {} strategy for tier {}",
                        user.getId(), strategy.getStrategyName(), criteria.getTargetTier());
                return false;
            }
        }
        return true;
    }

    /**
     * Check what tier a user currently qualifies for without upgrading.
     */
    @Transactional(readOnly = true)
    public TierLevel checkEligibleTier(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<Membership> membershipOpt = membershipRepository.findByUserId(userId);
        TierLevel currentTier = membershipOpt
                .map(m -> m.getTier().getTierLevel())
                .orElse(TierLevel.SILVER); // Default to SILVER if no membership

        return findHighestEligibleTier(user, TierLevel.values()[0]) != null
                ? findHighestEligibleTier(user, TierLevel.values()[0])
                : currentTier;
    }
}
