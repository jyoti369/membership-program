package com.firstclub.membership.strategy;

import com.firstclub.membership.entity.TierUpgradeCriteria;
import com.firstclub.membership.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class CohortEvaluationStrategy implements TierEvaluationStrategy {

    @Override
    public boolean evaluate(User user, TierUpgradeCriteria criteria) {
        if (criteria.getEligibleCohort() == null || criteria.getEligibleCohort().isBlank()) {
            return true; // No cohort requirement
        }

        if (user.getCohort() == null) {
            return false; // User has no cohort
        }

        List<String> eligibleCohorts = Arrays.asList(criteria.getEligibleCohort().split(","));
        boolean isEligible = eligibleCohorts.stream()
                .anyMatch(cohort -> cohort.trim().equalsIgnoreCase(user.getCohort().trim()));

        log.debug("User {} with cohort {} is eligible: {}",
                user.getId(), user.getCohort(), isEligible);

        return isEligible;
    }

    @Override
    public String getStrategyName() {
        return "COHORT";
    }
}
