package com.firstclub.membership.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Plan ID is required")
    private Long planId;
}
