package com.firstclub.membership.service;

import com.firstclub.membership.dto.PlanResponse;
import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.repository.MembershipPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipPlanService {

    private final MembershipPlanRepository planRepository;

    @Transactional(readOnly = true)
    public List<PlanResponse> getAllActivePlans() {
        return planRepository.findByActiveTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlanResponse getPlanById(Long planId) {
        MembershipPlan plan = planRepository.findByIdAndActiveTrue(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found or inactive"));
        return convertToResponse(plan);
    }

    private PlanResponse convertToResponse(MembershipPlan plan) {
        return PlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .duration(plan.getDuration())
                .price(plan.getPrice())
                .description(plan.getDescription())
                .build();
    }
}
