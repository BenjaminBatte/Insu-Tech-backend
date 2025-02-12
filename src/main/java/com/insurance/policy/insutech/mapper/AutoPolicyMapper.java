package com.insurance.policy.insutech.mapper;

import com.insurance.policy.insutech.dto.AutoPolicyDTO;
import com.insurance.policy.insutech.model.AutoPolicy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AutoPolicyMapper {
    AutoPolicyMapper INSTANCE = Mappers.getMapper(AutoPolicyMapper.class);

    AutoPolicyDTO toDTO(AutoPolicy policy);

    AutoPolicy toEntity(AutoPolicyDTO dto);
}
