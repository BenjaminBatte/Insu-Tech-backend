package com.insurance.policy.insutech.service;

import com.insurance.policy.insutech.dto.AutoPolicyDTO;
import com.insurance.policy.insutech.model.AutoPolicyType;
import com.insurance.policy.insutech.model.PolicyStatus;

import java.time.LocalDate;
import java.util.List;

public interface AutoPolicyService {
  AutoPolicyDTO getPolicyByPolicyNumber(String policyNumber);
    AutoPolicyDTO createPolicy(AutoPolicyDTO autoPolicyDTO);
    AutoPolicyDTO getPolicyById(Long id);
    List<AutoPolicyDTO> getAllPolicies();
    AutoPolicyDTO updatePolicy(Long id, AutoPolicyDTO autoPolicyDTO);
    void deletePolicy(Long id);
    List<AutoPolicyDTO> createPolicies(List<AutoPolicyDTO> autoPolicyDTOs);
    List<AutoPolicyDTO> getAllPolicies(LocalDate startDate,
                                       LocalDate endDate,
                                       PolicyStatus status,
                                       AutoPolicyType type,
                                       String vehicleMake,
                                       String firstName,
                                       String lastName,
                                       Double minPremium,
                                       Double maxPremium);
}
