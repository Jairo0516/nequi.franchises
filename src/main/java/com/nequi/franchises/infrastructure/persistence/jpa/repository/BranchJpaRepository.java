package com.nequi.franchises.infrastructure.persistence.jpa.repository;

import com.nequi.franchises.infrastructure.persistence.jpa.entity.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BranchJpaRepository extends JpaRepository<BranchEntity, UUID> {
    List<BranchEntity> findByFranchiseId(UUID franchiseId);

    boolean existsByFranchiseIdAndNameIgnoreCase(UUID franchiseId, String name);
}
