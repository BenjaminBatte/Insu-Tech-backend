package com.insurance.policy.insutech.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class SuperPolicy implements Cloneable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String policyNumber;

    @Enumerated(EnumType.STRING)
    private PolicyStatus status;

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal premiumAmount;

    public SuperPolicy(String policyNumber, PolicyStatus status, LocalDate startDate, LocalDate endDate, BigDecimal premiumAmount) {
        this.policyNumber = policyNumber;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.premiumAmount = premiumAmount;
    }

    @Override
    public SuperPolicy clone() {
        try {
            return (SuperPolicy) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning failed", e);
        }
    }
}
