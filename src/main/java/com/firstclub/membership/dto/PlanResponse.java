package com.firstclub.membership.dto;

import com.firstclub.membership.enums.PlanDuration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanResponse {
    private Long id;
    private String name;
    private PlanDuration duration;
    private BigDecimal price;
    private String description;
}
