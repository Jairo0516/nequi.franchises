package com.nequi.franchises.infrastructure.persistence.jpa.repository;

import com.nequi.franchises.infrastructure.persistence.jpa.entity.FranchiseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FranchiseJpaRepository extends JpaRepository<FranchiseEntity, UUID> {
    boolean existsByNameIgnoreCase(String name);
}
