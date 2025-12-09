package com.firstclub.membership.dto;

import com.firstclub.membership.enums.TierLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TierResponse {
    private Long id;
    private TierLevel tierLevel;
    private String name;
    private String description;
    private List<BenefitResponse> benefits;
}
