package com.insurance.policy.insutech.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AutoPolicyType {
    LIABILITY("LIAB", "Covers damages to others caused by the insured driver"),
    COLLISION("COLL", "Covers damages to the insured's vehicle from a collision"),
    COMPREHENSIVE("COMP", "Covers non-collision damages (e.g., theft, fire, vandalism)");

    private final String code;
    private final String description;

    AutoPolicyType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static AutoPolicyType fromCode(String code) {
        for (AutoPolicyType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid AutoPolicyType code: " + code);
    }
}
