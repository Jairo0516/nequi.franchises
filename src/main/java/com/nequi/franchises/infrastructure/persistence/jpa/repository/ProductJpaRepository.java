package com.nequi.franchises.infrastructure.persistence.jpa.repository;

import com.nequi.franchises.infrastructure.persistence.jpa.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    Optional<ProductEntity> findByIdAndDeletedFalse(UUID id);

    Optional<ProductEntity> findByBranchIdAndNameIgnoreCase(UUID branchId, String name);

    boolean existsByBranchIdAndNameIgnoreCase(UUID branchId, String name);

    List<ProductEntity> findByBranchIdInAndDeletedFalseOrderByBranchIdAscStockDesc(List<UUID> branchIds);
}
