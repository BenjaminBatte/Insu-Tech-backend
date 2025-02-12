package com.insurance.policy.insutech.repository;

import com.insurance.policy.insutech.model.AutoPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoPolicyRepository extends JpaRepository<AutoPolicy, Long>, JpaSpecificationExecutor<AutoPolicy> {
}