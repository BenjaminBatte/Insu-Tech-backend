package com.insurance.policy.insutech.controller;

import com.insurance.policy.insutech.dto.AutoPolicyDTO;
import com.insurance.policy.insutech.model.AutoPolicyType;
import com.insurance.policy.insutech.model.PolicyStatus;
import com.insurance.policy.insutech.service.AutoPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
public class AutoPolicyController {

    private final AutoPolicyService autoPolicyService;

    @PostMapping
    public ResponseEntity<AutoPolicyDTO> createPolicy(@RequestBody AutoPolicyDTO autoPolicyDTO) {
        AutoPolicyDTO createdPolicy = autoPolicyService.createPolicy(autoPolicyDTO);
        URI location = URI.create("/api/v1/policies/" + createdPolicy.getId());
        return ResponseEntity.created(location).body(createdPolicy);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<AutoPolicyDTO>> createPolicies(@RequestBody List<AutoPolicyDTO> autoPolicyDTOs) {
        System.out.println("Received batch request with " + autoPolicyDTOs.size() + " policies");
        return ResponseEntity.ok(autoPolicyService.createPolicies(autoPolicyDTOs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutoPolicyDTO> getPolicyById(@PathVariable Long id) {
        return ResponseEntity.ok(autoPolicyService.getPolicyById(id));
    }

    @GetMapping
    public ResponseEntity<List<AutoPolicyDTO>> getAllPolicies() {
        return ResponseEntity.ok(autoPolicyService.getAllPolicies());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutoPolicyDTO> updatePolicy(@PathVariable Long id, @RequestBody AutoPolicyDTO autoPolicyDTO) {
        return ResponseEntity.ok(autoPolicyService.updatePolicy(id, autoPolicyDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        autoPolicyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/filter")
    public ResponseEntity<List<AutoPolicyDTO>> getFilteredPolicies(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String vehicleMake,
            @RequestParam(required = false) Double minPremium,
            @RequestParam(required = false) Double maxPremium,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {

        PolicyStatus policyStatus = null;
        if (status != null) {
            try {
                policyStatus = PolicyStatus.fromCode(status);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        AutoPolicyType policyType = null;
        if (type != null) {
            try {
                policyType = AutoPolicyType.fromCode(type);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok(autoPolicyService.getAllPolicies(startDate, endDate, policyStatus, policyType, vehicleMake, firstName, lastName,minPremium,maxPremium));
    }


}
