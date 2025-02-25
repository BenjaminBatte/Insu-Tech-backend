package com.insurance.policy.insutech.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "auto_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AutoPolicy extends SuperPolicy {
    @Column(name = "policy_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AutoPolicyType policyType;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleYear;
    private String firstName;
    private String lastName;

    @Builder
    public AutoPolicy(String policyNumber, PolicyStatus status, LocalDate startDate, LocalDate endDate, BigDecimal premiumAmount,
                      AutoPolicyType policyType, String vehicleMake, String vehicleModel, String vehicleYear, String firstName, String lastName) {
        super(policyNumber, status, startDate, endDate, premiumAmount);
        this.policyType = policyType;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
        this.vehicleYear = vehicleYear;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
