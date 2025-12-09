package com.firstclub.membership.controller;

import com.firstclub.membership.dto.PlanResponse;
import com.firstclub.membership.dto.TierResponse;
import com.firstclub.membership.service.MembershipPlanService;
import com.firstclub.membership.service.MembershipTierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class MembershipPlanController {

    private final MembershipPlanService planService;
    private final MembershipTierService tierService;

    /**
     * GET /api/plans - Get all active membership plans
     */
    @GetMapping
    public ResponseEntity<List<PlanResponse>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllActivePlans());
    }

    /**
     * GET /api/plans/{planId} - Get specific plan
     */
    @GetMapping("/{planId}")
    public ResponseEntity<PlanResponse> getPlanById(@PathVariable Long planId) {
        return ResponseEntity.ok(planService.getPlanById(planId));
    }

    /**
     * GET /api/plans/tiers - Get all membership tiers
     */
    @GetMapping("/tiers")
    public ResponseEntity<List<TierResponse>> getAllTiers() {
        return ResponseEntity.ok(tierService.getAllTiers());
    }
}
