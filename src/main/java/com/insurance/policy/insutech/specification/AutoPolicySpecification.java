package com.insurance.policy.insutech.specification;

import com.insurance.policy.insutech.model.AutoPolicy;
import com.insurance.policy.insutech.model.AutoPolicyType;
import com.insurance.policy.insutech.model.PolicyStatus;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public class AutoPolicySpecification {

    public static Specification<AutoPolicy> hasStartDate(LocalDate startDate) {
        return (root, query, criteriaBuilder) ->
                startDate == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate);
    }

    public static Specification<AutoPolicy> hasEndDate(LocalDate endDate) {
        return (root, query, criteriaBuilder) ->
                endDate == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), endDate);
    }

    public static Specification<AutoPolicy> hasStatus(PolicyStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<AutoPolicy> hasType(AutoPolicyType type) {
        return (root, query, criteriaBuilder) ->
                type == null ? null : criteriaBuilder.equal(root.get("policyType"), type);
    }



    public static Specification<AutoPolicy> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) ->
                firstName == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<AutoPolicy> hasLastName(String lastName) {
        return (root, query, criteriaBuilder) ->
                lastName == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }
    public static Specification<AutoPolicy> hasVehicleMake(String vehicleMake) {
        return (root, query, criteriaBuilder) ->
                vehicleMake == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("vehicleMake")), "%" + vehicleMake.toLowerCase() + "%");
    }

}
