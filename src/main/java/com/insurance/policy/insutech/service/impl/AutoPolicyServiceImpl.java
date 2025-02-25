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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.BeanUtils;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class AutoPolicyServiceImpl implements AutoPolicyService {

    private static final Logger logger = LoggerFactory.getLogger(AutoPolicyServiceImpl.class);

    private final AutoPolicyRepository autoPolicyRepository;
    private final AutoPolicyMapper autoPolicyMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public AutoPolicyDTO createPolicy(AutoPolicyDTO autoPolicyDTO) {
        logger.info("Creating new auto policy with policy number: {}", autoPolicyDTO.getPolicyNumber());
        AutoPolicy policy = autoPolicyMapper.toEntity(autoPolicyDTO);
        AutoPolicy savedPolicy = autoPolicyRepository.save(policy);
        logger.info("Successfully created policy with ID: {}", savedPolicy.getId());
        return autoPolicyMapper.toDTO(savedPolicy);
    }
    @Override
    public AutoPolicyDTO getPolicyById(Long id) {
        return autoPolicyRepository.findById(id)
                .map(autoPolicyMapper::toDTO)
                .orElseThrow(() ->new AutoPolicyNotFoundException("Auto Policy not found with ID: " + id));

    }
    @Override
    public AutoPolicyDTO updatePolicy(Long id, AutoPolicyDTO autoPolicyDTO) {
        AutoPolicy existingPolicy = autoPolicyRepository.findById(id)
                .orElseThrow(() -> new AutoPolicyNotFoundException("Auto Policy not found with ID: " + id));

        // Copy only non-null fields
        BeanUtils.copyProperties(autoPolicyDTO, existingPolicy, getNullPropertyNames(autoPolicyDTO));

        // Save updated entity
        AutoPolicy updatedPolicy = autoPolicyRepository.save(existingPolicy);
        return autoPolicyMapper.toDTO(updatedPolicy);
    }

    @Override
    public Page<AutoPolicyDTO> getAllPolicies(Pageable pageable) {
        return autoPolicyRepository.findAll(pageable)
                .map(autoPolicyMapper::toDTO);
    }

    // Helper method to get property names with null values
    private String[] getNullPropertyNames(Object source) {
        return Arrays.stream(BeanUtils.getPropertyDescriptors(source.getClass()))
                .map(PropertyDescriptor::getName)
                .filter(propertyName -> {
                    try {
                        return Objects.isNull(BeanUtils.getPropertyDescriptor(source.getClass(), propertyName)
                                .getReadMethod().invoke(source));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toArray(String[]::new);
    }

    @Override
    public AutoPolicyDTO getPolicyByPolicyNumber(String policyNumber) {
        AutoPolicy policy = autoPolicyRepository.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new AutoPolicyNotFoundException("AutoPolicy with policy number " + policyNumber + " not found"));

        return autoPolicyMapper.toDTO(policy);
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

    private <T> void addPredicateIfPresent(List<Predicate> predicates, CriteriaBuilder cb, Root<AutoPolicy> root,
                                           String field, T value) {
        Optional.ofNullable(value).ifPresent(v -> predicates.add(cb.equal(root.get(field), v)));
    }

    @Override
    public List<AutoPolicyDTO> getAllPolicies(LocalDate startDate, LocalDate endDate, PolicyStatus status, AutoPolicyType type,
                                              String vehicleMake, String firstName, String lastName, Double minPremium, Double maxPremium) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AutoPolicy> query = cb.createQuery(AutoPolicy.class);
        Root<AutoPolicy> root = query.from(AutoPolicy.class);

        List<Predicate> predicates = new ArrayList<>();
        addPredicateIfPresent(predicates, cb, root, "startDate", startDate);
        addPredicateIfPresent(predicates, cb, root, "endDate", endDate);
        addPredicateIfPresent(predicates, cb, root, "status", status);
        addPredicateIfPresent(predicates, cb, root, "policyType", type);
        addPredicateIfPresent(predicates, cb, root, "vehicleMake", vehicleMake);
        addPredicateIfPresent(predicates, cb, root, "firstName", firstName);
        addPredicateIfPresent(predicates, cb, root, "lastName", lastName);
        addPredicateIfPresent(predicates, cb, root, "premiumAmount", minPremium);
        addPredicateIfPresent(predicates, cb, root, "premiumAmount", maxPremium);

        query.select(root).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList()
                .stream()
                .map(autoPolicyMapper::toDTO)
                .collect(Collectors.toList());
    }

}
