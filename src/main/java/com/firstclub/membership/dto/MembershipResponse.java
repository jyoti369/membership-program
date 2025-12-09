package com.firstclub.membership.dto;

import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.enums.PlanDuration;
import com.firstclub.membership.enums.TierLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipResponse {
    private Long membershipId;
    private Long userId;
    private String userName;
    private String userEmail;
    private String planName;
    private PlanDuration planDuration;
    private TierLevel tierLevel;
    private String tierName;
    private MembershipStatus status;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private List<BenefitResponse> benefits;
    private boolean isActive;
}
