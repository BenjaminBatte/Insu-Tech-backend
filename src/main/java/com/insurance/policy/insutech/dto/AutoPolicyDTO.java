package com.insurance.policy.insutech.dto;

import com.insurance.policy.insutech.model.AutoPolicyType;
import com.insurance.policy.insutech.model.PolicyStatus;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class AutoPolicyDTO {
    private Long id;
    private String policyNumber;
    private PolicyStatus status;
    private AutoPolicyType policyType;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleYear;
    private String firstName;
    private String lastName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal premiumAmount;
}
