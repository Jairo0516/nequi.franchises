package com.nequi.franchises.domain.ports;

import com.nequi.franchises.domain.model.Franchise;

import java.util.Optional;
import java.util.UUID;

public interface FranchiseRepositoryPort {
    Franchise save(Franchise franchise);

    Optional<Franchise> findById(UUID id);

    boolean existsByNameIgnoreCase(String name);

    Franchise updateName(UUID id, String newName);
}
