package com.nequi.franchises.domain.ports;

import com.nequi.franchises.domain.model.Branch;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchRepositoryPort {
    Branch save(Branch branch);

    Optional<Branch> findById(UUID id);

    List<Branch> findByFranchiseId(UUID franchiseId);

    boolean existsByFranchiseIdAndNameIgnoreCase(UUID franchiseId, String name);

    Branch updateName(UUID id, String newName);
}
