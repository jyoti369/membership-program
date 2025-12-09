package com.firstclub.membership.service;

import com.firstclub.membership.dto.CreateOrderRequest;
import com.firstclub.membership.dto.OrderResponse;
import com.firstclub.membership.entity.Order;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.repository.OrderRepository;
import com.firstclub.membership.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BenefitCalculationService benefitCalculationService;

    /**
     * Create an order and apply membership benefits.
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Order order = Order.builder()
                .user(user)
                .orderValue(request.getOrderValue())
                .category(request.getCategory())
                .build();

        // Apply membership benefits
        order = benefitCalculationService.applyBenefits(order);

        order = orderRepository.save(order);
        log.info("Created order {} for user {}", order.getId(), user.getId());

        return convertToResponse(order);
    }

    private OrderResponse convertToResponse(Order order) {
        BigDecimal finalAmount = order.getOrderValue();
        if (order.getDiscountAmount() != null) {
            finalAmount = finalAmount.subtract(order.getDiscountAmount());
        }

        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUser().getId())
                .orderValue(order.getOrderValue())
                .category(order.getCategory())
                .orderDate(order.getOrderDate())
                .freeDeliveryApplied(order.getFreeDeliveryApplied())
                .discountPercentage(order.getDiscountPercentage())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(finalAmount)
                .build();
    }
}
