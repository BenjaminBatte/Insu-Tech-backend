package com.insurance.policy.insutech.converter;

import com.insurance.policy.insutech.model.PolicyStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class PolicyStatusConverter implements AttributeConverter<PolicyStatus, String> {

    @Override
    public String convertToDatabaseColumn(PolicyStatus status) {
        if (status == null) {
            return null;
        }
        return status.getCode();
    }

    @Override
    public PolicyStatus convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(PolicyStatus.values())
                .filter(s -> s.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown code: " + code));
    }
}
