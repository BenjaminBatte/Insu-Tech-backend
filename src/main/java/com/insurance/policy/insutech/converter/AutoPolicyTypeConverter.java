package com.insurance.policy.insutech.converter;

import com.insurance.policy.insutech.model.AutoPolicyType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;
@Converter(autoApply = true)
public class AutoPolicyTypeConverter implements AttributeConverter<AutoPolicyType, String> {
    @Override
    public String convertToDatabaseColumn(AutoPolicyType type) {
        if (type == null) {
            return null;
        }
        return type.getCode();
    }
    @Override
    public AutoPolicyType convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(AutoPolicyType.values())
                .filter(t -> t.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown code: " + code));
    }
}