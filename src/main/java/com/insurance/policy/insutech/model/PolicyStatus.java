package com.insurance.policy.insutech.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PolicyStatus {
    ACTIVE("ACTIVE", "Policy is currently active"),
    EXPIRED("EXPIRED", "Policy has expired"),
    CANCELLED("CANCELLED", "Policy has been cancelled");

    private final String code;
    private final String description;

    PolicyStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static PolicyStatus fromString(String code) {
        for (PolicyStatus status : PolicyStatus.values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid PolicyStatus: " + code);
    }
}
