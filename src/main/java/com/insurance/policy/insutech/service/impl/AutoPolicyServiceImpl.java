package com.insurance.policy.insutech.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutoPolicyServiceImpl implements AutoPolicyService {

    private final AutoPolicyRepository autoPolicyRepository;
    private final AutoPolicyMapper autoPolicyMapper = AutoPolicyMapper.INSTANCE;

    @PersistenceContext
    private EntityManager entityManager;

    // Manual cache for complex filtered queries - using manual cache for better control
    private final Cache<String, List<AutoPolicyDTO>> filteredPoliciesCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    /**
     * Strategy: Cache individual policy by ID in "policies" cache region
     * - Good for frequently accessed individual policies
     * - Cache miss loads from database and populates cache
     */
    @Override
    @Cacheable(value = "policies", key = "#id")
    public AutoPolicyDTO getPolicyById(Long id) {
        return autoPolicyRepository.findById(id)
                .map(autoPolicyMapper::toDTO)
                .orElseThrow(() -> new AutoPolicyNotFoundException("Auto Policy not found with ID: " + id));
    }

    /**
     * Strategy: Cache policy by policy number in separate "policyNumbers" cache region
     * - Useful for external API calls using policy numbers
     * - Different cache region to avoid key conflicts with ID-based caching
     */
    @Override
    @Cacheable(value = "policyNumbers", key = "#policyNumber")
    public AutoPolicyDTO getPolicyByPolicyNumber(String policyNumber) {
        AutoPolicy policy = autoPolicyRepository.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new AutoPolicyNotFoundException("AutoPolicy with policy number " + policyNumber + " not found"));
        return autoPolicyMapper.toDTO(policy);
    }

    /**
     * Strategy: Evict all relevant caches when creating new policy
     * - Prevents stale data in both individual and list caches
     * - New policies affect all cached lists and filtered queries
     */
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "allPolicies", allEntries = true),
                    @CacheEvict(value = "filteredPolicies", allEntries = true)
            }
    )
    public AutoPolicyDTO createPolicy(AutoPolicyDTO autoPolicyDTO) {
        AutoPolicy policy = autoPolicyMapper.toEntity(autoPolicyDTO);
        return autoPolicyMapper.toDTO(autoPolicyRepository.save(policy));
    }

    /**
     * Strategy: Cache all policies list with short TTL in "allPolicies" region
     * - Large datasets, so use short expiration
     * - Evicted on any write operation to maintain consistency
     */
    @Override
    @Cacheable(value = "allPolicies", key = "'all'")
    public List<AutoPolicyDTO> getAllPolicies() {
        List<AutoPolicy> policies = autoPolicyRepository.findAll();
        if (policies.isEmpty()) {
            throw new AutoPolicyNotFoundException("No auto policies found in the system.");
        }
        return policies.stream().map(autoPolicyMapper::toDTO).collect(Collectors.toList());
    }

    /**
     * Strategy: Update cache with fresh data on update
     * - @CachePut updates the individual policy cache with new data
     * - Evict list caches since they might contain the updated policy
     */
    @Override
    @Caching(
            put = @CachePut(value = "policies", key = "#id"),
            evict = {
                    @CacheEvict(value = "policyNumbers", allEntries = true), // Policy number might change
                    @CacheEvict(value = "allPolicies", allEntries = true),
                    @CacheEvict(value = "filteredPolicies", allEntries = true)
            }
    )
    public AutoPolicyDTO updatePolicy(Long id, AutoPolicyDTO autoPolicyDTO) {
        if (!autoPolicyRepository.existsById(id)) {
            throw new AutoPolicyNotFoundException("Auto Policy not found with ID: " + id);
        }
        AutoPolicy updatedPolicy = autoPolicyMapper.toEntity(autoPolicyDTO);
        updatedPolicy.setId(id);
        return autoPolicyMapper.toDTO(autoPolicyRepository.save(updatedPolicy));
    }

    /**
     * Strategy: Evict all relevant caches on deletion
     * - Remove individual policy from "policies" cache
     * - Evict all list caches that might contain the deleted policy
     */
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "policies", key = "#id"),
                    @CacheEvict(value = "policyNumbers", allEntries = true),
                    @CacheEvict(value = "allPolicies", allEntries = true),
                    @CacheEvict(value = "filteredPolicies", allEntries = true)
            }
    )
    public void deletePolicy(Long id) {
        if (!autoPolicyRepository.existsById(id)) {
            throw new AutoPolicyNotFoundException("AutoPolicy with ID " + id + " not found");
        }
        autoPolicyRepository.deleteById(id);
    }

    /**
     * Strategy: Evict all list caches on batch creation
     * - Multiple new policies affect all cached lists
     * - Complete cache refresh needed for list views
     */
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "allPolicies", allEntries = true),
                    @CacheEvict(value = "filteredPolicies", allEntries = true),
                    @CacheEvict(value = "policyNumbers", allEntries = true)
            }
    )
    public List<AutoPolicyDTO> createPolicies(List<AutoPolicyDTO> autoPolicyDTOs) {
        List<AutoPolicy> policies = autoPolicyDTOs.stream()
                .map(autoPolicyMapper::toEntity)
                .collect(Collectors.toList());
        List<AutoPolicy> savedPolicies = autoPolicyRepository.saveAll(policies);
        return savedPolicies.stream()
                .map(autoPolicyMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Strategy: Manual caching for complex filtered queries
     * - Complex key generation for diverse filter combinations
     * - Manual cache control for better flexibility with dynamic parameters
     * - Separate from Spring's cache abstraction due to complex key requirements
     */
    @Override
    public List<AutoPolicyDTO> getAllPolicies(LocalDate startDate, LocalDate endDate, PolicyStatus status,
                                              AutoPolicyType type, String vehicleMake, String firstName,
                                              String lastName, Double minPremium, Double maxPremium) {

        // Generate unique cache key based on all filter parameters
        String cacheKey = generateCacheKey(startDate, endDate, status, type, vehicleMake, firstName, lastName, minPremium, maxPremium);

        // Try to get from cache first
        List<AutoPolicyDTO> cachedResult = filteredPoliciesCache.getIfPresent(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        // Cache miss - execute query and cache result
        List<AutoPolicyDTO> result = executeFilteredQuery(startDate, endDate, status, type, vehicleMake, firstName, lastName, minPremium, maxPremium);
        filteredPoliciesCache.put(cacheKey, result);

        return result;
    }

    /**
     * Helper method to execute the actual filtered query
     */
    private List<AutoPolicyDTO> executeFilteredQuery(LocalDate startDate, LocalDate endDate, PolicyStatus status,
                                                     AutoPolicyType type, String vehicleMake, String firstName,
                                                     String lastName, Double minPremium, Double maxPremium) {
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
            predicates.add(cb.like(cb.lower(root.get("vehicleMake")), "%" + vehicleMake.toLowerCase() + "%"));
        }
        if (firstName != null && !firstName.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"));
        }
        if (lastName != null && !lastName.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
        }
        if (minPremium != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("premiumAmount"), minPremium));
        }
        if (maxPremium != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("premiumAmount"), maxPremium));
        }

        query.select(root).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList()
                .stream()
                .map(autoPolicyMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Generate unique cache key based on filter parameters
     * Handles null values appropriately
     */
    private String generateCacheKey(LocalDate startDate, LocalDate endDate, PolicyStatus status,
                                    AutoPolicyType type, String vehicleMake, String firstName,
                                    String lastName, Double minPremium, Double maxPremium) {
        return String.format("filter_%s_%s_%s_%s_%s_%s_%s_%s_%s",
                startDate != null ? startDate : "null",
                endDate != null ? endDate : "null",
                status != null ? status : "null",
                type != null ? type : "null",
                vehicleMake != null ? vehicleMake : "null",
                firstName != null ? firstName : "null",
                lastName != null ? lastName : "null",
                minPremium != null ? minPremium : "null",
                maxPremium != null ? maxPremium : "null");
    }

    /**
     * Optional: Method to clear specific filtered cache
     * Useful for targeted cache invalidation during testing or maintenance
     */
    public void clearFilteredCache() {
        filteredPoliciesCache.invalidateAll();
    }
}