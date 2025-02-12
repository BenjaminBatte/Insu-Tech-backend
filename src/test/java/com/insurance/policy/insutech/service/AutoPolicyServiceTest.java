package com.insurance.policy.insutech.service;

import com.insurance.policy.insutech.dto.AutoPolicyDTO;
import com.insurance.policy.insutech.exception.AutoPolicyNotFoundException;
import com.insurance.policy.insutech.mapper.AutoPolicyMapper;
import com.insurance.policy.insutech.model.AutoPolicy;
import com.insurance.policy.insutech.model.AutoPolicyType;
import com.insurance.policy.insutech.model.PolicyStatus;
import com.insurance.policy.insutech.repository.AutoPolicyRepository;
import com.insurance.policy.insutech.service.impl.AutoPolicyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AutoPolicyServiceTest {

    @Mock
    private AutoPolicyRepository autoPolicyRepository;

    @Spy
    private AutoPolicyMapper autoPolicyMapper = AutoPolicyMapper.INSTANCE;

    @InjectMocks
    private AutoPolicyServiceImpl autoPolicyService;

    private AutoPolicy policy;
    private AutoPolicyDTO policyDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        policy = new AutoPolicy(1L, "AP-101", PolicyStatus.fromCode("ACT"), AutoPolicyType.fromCode("COLL"),
                "Ford", "F-150", "2023", "Michael", "Johnson",
                LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31),
                BigDecimal.valueOf(700.00));

        policyDTO = new AutoPolicyDTO();
        policyDTO.setId(1L);
        policyDTO.setPolicyNumber("AP-101");
        policyDTO.setStatus(PolicyStatus.fromCode("ACT"));
        policyDTO.setPolicyType(AutoPolicyType.fromCode("COLL"));
        policyDTO.setVehicleMake("Ford");
        policyDTO.setVehicleModel("F-150");
        policyDTO.setVehicleYear("2023");
        policyDTO.setFirstName("Michael");
        policyDTO.setLastName("Johnson");
        policyDTO.setStartDate(LocalDate.of(2023, 1, 1));
        policyDTO.setEndDate(LocalDate.of(2023, 12, 31));
        policyDTO.setPremiumAmount(BigDecimal.valueOf(700.00));
    }

    @Test
    void shouldCreatePolicy() {
        when(autoPolicyMapper.toEntity(any())).thenReturn(policy);
        when(autoPolicyRepository.save(any())).thenReturn(policy);
        when(autoPolicyMapper.toDTO(any())).thenReturn(policyDTO);

        AutoPolicyDTO savedPolicy = autoPolicyService.createPolicy(policyDTO);

        assertNotNull(savedPolicy);
        assertEquals("AP-101", savedPolicy.getPolicyNumber());
        assertEquals(PolicyStatus.fromCode("ACT"), savedPolicy.getStatus());
        assertEquals(AutoPolicyType.fromCode("COLL"), savedPolicy.getPolicyType());
    }

    @Test
    void shouldFindPolicyById() {
        when(autoPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(autoPolicyMapper.toDTO(policy)).thenReturn(policyDTO);

        AutoPolicyDTO foundPolicy = autoPolicyService.getPolicyById(1L);

        assertNotNull(foundPolicy);
        assertEquals("AP-101", foundPolicy.getPolicyNumber());
    }

    @Test
    void shouldThrowExceptionWhenPolicyNotFound() {
        when(autoPolicyRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(AutoPolicyNotFoundException.class, () -> autoPolicyService.getPolicyById(2L));
    }

    @Test
    void shouldGetAllPolicies() {
        when(autoPolicyRepository.findAll()).thenReturn(List.of(policy));
        when(autoPolicyMapper.toDTO(policy)).thenReturn(policyDTO);

        List<AutoPolicyDTO> policies = autoPolicyService.getAllPolicies();

        assertFalse(policies.isEmpty());
        assertEquals(1, policies.size());
    }
}
