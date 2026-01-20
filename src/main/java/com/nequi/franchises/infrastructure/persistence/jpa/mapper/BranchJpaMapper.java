package com.nequi.franchises.infrastructure.persistence.jpa.mapper;

import com.nequi.franchises.domain.model.Branch;
import com.nequi.franchises.infrastructure.persistence.jpa.entity.BranchEntity;

public final class BranchJpaMapper {

    private BranchJpaMapper() {
    }

    public static BranchEntity toEntity(Branch d) {
        return new BranchEntity(d.id(), d.franchiseId(), d.name());
    }

    public static Branch toDomain(BranchEntity e) {
        return new Branch(e.getId(), e.getFranchiseId(), e.getName());
    }
}
