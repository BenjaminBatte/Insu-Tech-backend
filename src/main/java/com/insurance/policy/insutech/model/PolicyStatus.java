package com.insurance.policy.insutech.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PolicyStatus {
    ACTIVE("ACT", "Active Policy"),
    EXPIRED("EXP", "Expired Policy"),
    CANCELLED("CAN", "Cancelled Policy");

    private final String code;
    private final String description;

    PolicyStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;  // Serialize as "ACT", "EXP", "CAN"
    }

    @JsonCreator
    public static PolicyStatus fromCode(String code) {
        for (PolicyStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid PolicyStatus code: " + code);
    }
}
