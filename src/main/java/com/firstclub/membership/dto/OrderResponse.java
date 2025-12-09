package com.firstclub.membership.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long orderId;
    private Long userId;
    private BigDecimal orderValue;
    private String category;
    private LocalDateTime orderDate;
    private Boolean freeDeliveryApplied;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
}
