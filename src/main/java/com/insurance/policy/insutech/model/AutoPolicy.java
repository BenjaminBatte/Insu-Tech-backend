package com.insurance.policy.insutech.model;

import com.insurance.policy.insutech.converter.AutoPolicyTypeConverter;
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

    @Convert(converter = AutoPolicyTypeConverter.class)
    @Column(name = "policy_type", length = 20, nullable = false)
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
