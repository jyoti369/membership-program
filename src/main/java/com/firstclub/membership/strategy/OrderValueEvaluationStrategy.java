package com.firstclub.membership.strategy;

import com.firstclub.membership.entity.TierUpgradeCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderValueEvaluationStrategy implements TierEvaluationStrategy {

    private final OrderRepository orderRepository;

    @Override
    public boolean evaluate(User user, TierUpgradeCriteria criteria) {
        if (criteria.getMinMonthlyOrderValue() == null) {
            return true; // No order value requirement
        }

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        BigDecimal totalValue = orderRepository.sumOrderValueByUserSince(user.getId(), startOfMonth);

        log.debug("User {} has order value {} this month. Required: {}",
                user.getId(), totalValue, criteria.getMinMonthlyOrderValue());

        return totalValue.compareTo(criteria.getMinMonthlyOrderValue()) >= 0;
    }

    @Override
    public String getStrategyName() {
        return "ORDER_VALUE";
    }
}
