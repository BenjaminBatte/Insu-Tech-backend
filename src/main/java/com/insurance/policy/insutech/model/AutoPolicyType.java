package com.insurance.policy.insutech.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AutoPolicyType {
    LIABILITY("LIABILITY", "Covers damages to others caused by the insured driver"),
    COLLISION("COLLISION", "Covers damages to the insured's vehicle from a collision"),
    COMPREHENSIVE("COMPREHENSIVE", "Covers non-collision damages (e.g., theft, fire, vandalism)");

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
    public static AutoPolicyType fromString(String code) {
        for (AutoPolicyType type : AutoPolicyType.values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid AutoPolicyType: " + code);
    }
}
