package com.insurance.policy.insutech.model;


import lombok.Getter;

@Getter
public enum PolicyStatus {
    ACTIVE("ACT", "Policy is currently active"),
    EXPIRED("EXP", "Policy has expired"),
    CANCELLED("CAN", "Policy has been cancelled");

    private final String code;
    private final String description;

    PolicyStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PolicyStatus fromCode(String code) {
        for (PolicyStatus status : PolicyStatus.values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid PolicyStatus: " + code);
    }
}
