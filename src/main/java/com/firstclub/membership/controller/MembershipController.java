package com.firstclub.membership.controller;

import com.firstclub.membership.dto.MembershipResponse;
import com.firstclub.membership.dto.SubscriptionRequest;
import com.firstclub.membership.enums.TierLevel;
import com.firstclub.membership.service.MembershipService;
import com.firstclub.membership.service.TierEvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;
    private final TierEvaluationService tierEvaluationService;

    /**
     * POST /api/memberships/subscribe - Subscribe to a membership plan
     */
    @PostMapping("/subscribe")
    public ResponseEntity<MembershipResponse> subscribe(@Valid @RequestBody SubscriptionRequest request) {
        MembershipResponse response = membershipService.subscribe(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/memberships/user/{userId} - Get current membership
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<MembershipResponse> getCurrentMembership(@PathVariable Long userId) {
        MembershipResponse response = membershipService.getCurrentMembership(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/memberships/user/{userId}/upgrade - Upgrade membership tier
     */
    @PutMapping("/user/{userId}/upgrade")
    public ResponseEntity<MembershipResponse> upgradeTier(
            @PathVariable Long userId,
            @RequestParam TierLevel tierLevel) {
        MembershipResponse response = membershipService.upgradeTier(userId, tierLevel);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/memberships/user/{userId}/downgrade - Downgrade membership tier
     */
    @PutMapping("/user/{userId}/downgrade")
    public ResponseEntity<MembershipResponse> downgradeTier(
            @PathVariable Long userId,
            @RequestParam TierLevel tierLevel) {
        MembershipResponse response = membershipService.downgradeTier(userId, tierLevel);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/memberships/user/{userId}/cancel - Cancel membership
     */
    @DeleteMapping("/user/{userId}/cancel")
    public ResponseEntity<MembershipResponse> cancelMembership(@PathVariable Long userId) {
        MembershipResponse response = membershipService.cancelMembership(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/memberships/user/{userId}/evaluate-tier - Evaluate and auto-upgrade tier
     */
    @PostMapping("/user/{userId}/evaluate-tier")
    public ResponseEntity<Map<String, Object>> evaluateTier(@PathVariable Long userId) {
        Optional<TierLevel> upgradedTier = tierEvaluationService.evaluateAndUpgradeTier(userId);

        if (upgradedTier.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Tier upgraded to " + upgradedTier.get(),
                    "newTier", upgradedTier.get()
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "No tier upgrade available"
            ));
        }
    }

    /**
     * GET /api/memberships/user/{userId}/eligible-tier - Check eligible tier
     */
    @GetMapping("/user/{userId}/eligible-tier")
    public ResponseEntity<Map<String, Object>> checkEligibleTier(@PathVariable Long userId) {
        TierLevel eligibleTier = tierEvaluationService.checkEligibleTier(userId);

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "eligibleTier", eligibleTier
        ));
    }
}
