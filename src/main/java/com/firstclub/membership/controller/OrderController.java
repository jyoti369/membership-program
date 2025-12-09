package com.firstclub.membership.controller;

import com.firstclub.membership.dto.CreateOrderRequest;
import com.firstclub.membership.dto.OrderResponse;
import com.firstclub.membership.service.BenefitCalculationService;
import com.firstclub.membership.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final BenefitCalculationService benefitCalculationService;

    /**
     * POST /api/orders - Create a new order with benefits applied
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/orders/benefits/free-delivery - Check free delivery eligibility
     */
    @GetMapping("/benefits/free-delivery")
    public ResponseEntity<Map<String, Object>> checkFreeDelivery(
            @RequestParam Long userId,
            @RequestParam(required = false) String category) {

        boolean eligible = benefitCalculationService.isEligibleForFreeDelivery(userId, category);

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "category", category != null ? category : "all",
                "eligibleForFreeDelivery", eligible
        ));
    }

    /**
     * GET /api/orders/benefits/discount - Get applicable discount
     */
    @GetMapping("/benefits/discount")
    public ResponseEntity<Map<String, Object>> getDiscount(
            @RequestParam Long userId,
            @RequestParam(required = false) String category) {

        BigDecimal discount = benefitCalculationService.getApplicableDiscount(userId, category);

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "category", category != null ? category : "all",
                "discountPercentage", discount
        ));
    }
}
