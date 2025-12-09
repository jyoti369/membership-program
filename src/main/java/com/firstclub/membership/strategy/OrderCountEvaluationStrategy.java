package com.firstclub.membership.strategy;

import com.firstclub.membership.entity.TierUpgradeCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCountEvaluationStrategy implements TierEvaluationStrategy {

    private final OrderRepository orderRepository;

    @Override
    public boolean evaluate(User user, TierUpgradeCriteria criteria) {
        if (criteria.getMinOrderCount() == null) {
            return true; // No order count requirement
        }

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long orderCount = orderRepository.countOrdersByUserSince(user.getId(), startOfMonth);

        log.debug("User {} has {} orders this month. Required: {}",
                user.getId(), orderCount, criteria.getMinOrderCount());

        return orderCount >= criteria.getMinOrderCount();
    }

    @Override
    public String getStrategyName() {
        return "ORDER_COUNT";
    }
}
