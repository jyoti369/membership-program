package com.firstclub.membership.service;

import com.firstclub.membership.dto.BenefitResponse;
import com.firstclub.membership.dto.TierResponse;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.TierBenefit;
import com.firstclub.membership.repository.MembershipTierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipTierService {

    private final MembershipTierRepository tierRepository;

    @Transactional(readOnly = true)
    public List<TierResponse> getAllTiers() {
        return tierRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private TierResponse convertToResponse(MembershipTier tier) {
        return TierResponse.builder()
                .id(tier.getId())
                .tierLevel(tier.getTierLevel())
                .name(tier.getName())
                .description(tier.getDescription())
                .benefits(tier.getBenefits().stream()
                        .map(this::convertBenefitToResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private BenefitResponse convertBenefitToResponse(TierBenefit benefit) {
        return BenefitResponse.builder()
                .benefitType(benefit.getBenefitType())
                .benefitValue(benefit.getBenefitValue())
                .description(benefit.getDescription())
                .applicableCategory(benefit.getApplicableCategory())
                .build();
    }
}
