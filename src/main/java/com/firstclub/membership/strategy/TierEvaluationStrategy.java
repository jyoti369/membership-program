package com.firstclub.membership.strategy;

import com.firstclub.membership.entity.TierUpgradeCriteria;
import com.firstclub.membership.entity.User;

/**
 * Strategy interface for evaluating tier upgrade criteria.
 * Allows flexible and extensible tier evaluation logic.
 */
public interface TierEvaluationStrategy {
    boolean evaluate(User user, TierUpgradeCriteria criteria);
    String getStrategyName();
}
