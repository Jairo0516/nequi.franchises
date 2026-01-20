package com.nequi.franchises.infrastructure.persistence.jpa.mapper;

import com.nequi.franchises.domain.model.Franchise;
import com.nequi.franchises.infrastructure.persistence.jpa.entity.FranchiseEntity;

public final class FranchiseJpaMapper {

    private FranchiseJpaMapper() {
    }

    public static FranchiseEntity toEntity(Franchise d) {
        return new FranchiseEntity(d.id(), d.name());
    }

    public static Franchise toDomain(FranchiseEntity e) {
        return new Franchise(e.getId(), e.getName());
    }
}
