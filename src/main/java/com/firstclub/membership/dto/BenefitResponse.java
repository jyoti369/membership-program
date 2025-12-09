package com.firstclub.membership.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitResponse {
    private String benefitType;
    private String benefitValue;
    private String description;
    private String applicableCategory;
}
