package com.insurance.policy.insutech.controller;

import com.insurance.policy.insutech.dto.AutoPolicyDTO;
import com.insurance.policy.insutech.model.AutoPolicyType;
import com.insurance.policy.insutech.model.PolicyStatus;
import com.insurance.policy.insutech.service.AutoPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:5173", "http://localhost:5174","http://localhost:64615"})
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
    @GetMapping("/policyNumber/{policyNumber}")
    public ResponseEntity<AutoPolicyDTO> getPolicyByPolicyNumber(@PathVariable String policyNumber) {
        return ResponseEntity.ok(autoPolicyService.getPolicyByPolicyNumber(policyNumber));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<Page<AutoPolicyDTO>> getAllPolicies(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(autoPolicyService.getAllPolicies(pageable));
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
            @RequestParam(required = false) PolicyStatus status,  // Change to Enum
            @RequestParam(required = false) AutoPolicyType type,  // Change to Enum
            @RequestParam(required = false) String vehicleMake,
            @RequestParam(required = false) Double minPremium,
            @RequestParam(required = false) Double maxPremium,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {

        return ResponseEntity.ok(autoPolicyService.getAllPolicies(
                startDate, endDate, status, type, vehicleMake, firstName, lastName, minPremium, maxPremium));
    }


}
