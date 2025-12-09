package com.firstclub.membership.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Order value is required")
    @Positive(message = "Order value must be positive")
    private BigDecimal orderValue;

    private String category;
}
