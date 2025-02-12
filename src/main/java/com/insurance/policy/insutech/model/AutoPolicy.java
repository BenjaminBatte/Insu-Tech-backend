package com.insurance.policy.insutech.model;

import com.insurance.policy.insutech.converter.AutoPolicyTypeConverter;
import com.insurance.policy.insutech.converter.PolicyStatusConverter;
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
@Builder
public class AutoPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String policyNumber;

    @Convert(converter = PolicyStatusConverter.class)
    @Column(nullable = false, length = 3)
    private PolicyStatus status;

    @Convert(converter = AutoPolicyTypeConverter.class)
    @Column(nullable = false, length = 4)
    private AutoPolicyType policyType;

    private String vehicleMake;
    private String vehicleModel;
    private String vehicleYear;
    private String firstName;
    private String lastName;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal premiumAmount;
}
