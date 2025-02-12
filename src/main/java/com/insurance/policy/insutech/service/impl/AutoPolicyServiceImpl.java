package com.insurance.policy.insutech.service.impl;

import com.insurance.policy.insutech.dto.AutoPolicyDTO;
import com.insurance.policy.insutech.exception.AutoPolicyNotFoundException;
import com.insurance.policy.insutech.mapper.AutoPolicyMapper;
import com.insurance.policy.insutech.model.AutoPolicy;
import com.insurance.policy.insutech.model.AutoPolicyType;
import com.insurance.policy.insutech.model.PolicyStatus;
import com.insurance.policy.insutech.repository.AutoPolicyRepository;
import com.insurance.policy.insutech.service.AutoPolicyService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutoPolicyServiceImpl implements AutoPolicyService {

    private final AutoPolicyRepository autoPolicyRepository;
    private final AutoPolicyMapper autoPolicyMapper = AutoPolicyMapper.INSTANCE;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public AutoPolicyDTO createPolicy(AutoPolicyDTO autoPolicyDTO) {
        AutoPolicy policy = autoPolicyMapper.toEntity(autoPolicyDTO);
        return autoPolicyMapper.toDTO(autoPolicyRepository.save(policy));
    }

    @Override
    public AutoPolicyDTO getPolicyById(Long id) {
        return autoPolicyRepository.findById(id)
                .map(autoPolicyMapper::toDTO)
                .orElseThrow(() -> new AutoPolicyNotFoundException("AutoPolicy with ID " + id + " not found"));
    }

    @Override
    public List<AutoPolicyDTO> getAllPolicies() {
        List<AutoPolicy> policies = autoPolicyRepository.findAll();
        if (policies.isEmpty()) {
            throw new AutoPolicyNotFoundException("No auto policies found in the system.");
        }
        return policies.stream().map(autoPolicyMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public AutoPolicyDTO updatePolicy(Long id, AutoPolicyDTO autoPolicyDTO) {
        if (!autoPolicyRepository.existsById(id)) {
            throw new AutoPolicyNotFoundException("AutoPolicy with ID " + id + " not found");
        }
        AutoPolicy updatedPolicy = autoPolicyMapper.toEntity(autoPolicyDTO);
        updatedPolicy.setId(id);
        return autoPolicyMapper.toDTO(autoPolicyRepository.save(updatedPolicy));
    }

    @Override
    public void deletePolicy(Long id) {
        if (!autoPolicyRepository.existsById(id)) {
            throw new AutoPolicyNotFoundException("AutoPolicy with ID " + id + " not found");
        }
        autoPolicyRepository.deleteById(id);
    }

    @Override
    public List<AutoPolicyDTO> createPolicies(List<AutoPolicyDTO> autoPolicyDTOs) {
        List<AutoPolicy> policies = autoPolicyDTOs.stream()
                .map(autoPolicyMapper::toEntity)
                .collect(Collectors.toList());
        List<AutoPolicy> savedPolicies = autoPolicyRepository.saveAll(policies);
        return savedPolicies.stream()
                .map(autoPolicyMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AutoPolicyDTO> getAllPolicies(LocalDate startDate, LocalDate endDate, PolicyStatus status, AutoPolicyType type, String vehicleMake, String firstName, String lastName, Double minPremium, Double maxPremium) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AutoPolicy> query = cb.createQuery(AutoPolicy.class);
        Root<AutoPolicy> root = query.from(AutoPolicy.class);

        List<Predicate> predicates = new ArrayList<>();

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), endDate));
        }
        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }
        if (type != null) {
            predicates.add(cb.equal(root.get("policyType"), type));
        }
        if (vehicleMake != null && !vehicleMake.isEmpty()) {
            predicates.add(cb.like(root.get("vehicleMake"), "%" + vehicleMake + "%"));
        }
        if (firstName != null && !firstName.isEmpty()) {
            predicates.add(cb.like(root.get("firstName"), "%" + firstName + "%"));
        }
        if (minPremium != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("premiumAmount"), minPremium));
        }
        if (maxPremium != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("premiumAmount"), maxPremium));
        }
        if (lastName != null && !lastName.isEmpty()) {
            predicates.add(cb.like(root.get("lastName"), "%" + lastName + "%"));
        }

        query.select(root).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList()
                .stream()
                .map(autoPolicyMapper::toDTO)
                .collect(Collectors.toList());
    }
}
