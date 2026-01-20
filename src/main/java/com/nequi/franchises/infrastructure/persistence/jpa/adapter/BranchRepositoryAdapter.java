package com.nequi.franchises.infrastructure.persistence.jpa.adapter;

import com.nequi.franchises.domain.exceptions.NotFoundException;
import com.nequi.franchises.domain.model.Branch;
import com.nequi.franchises.domain.ports.BranchRepositoryPort;
import com.nequi.franchises.infrastructure.persistence.jpa.entity.BranchEntity;
import com.nequi.franchises.infrastructure.persistence.jpa.mapper.BranchJpaMapper;
import com.nequi.franchises.infrastructure.persistence.jpa.repository.BranchJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BranchRepositoryAdapter implements BranchRepositoryPort {

    private final BranchJpaRepository repo;

    public BranchRepositoryAdapter(BranchJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public Branch save(Branch branch) {
        BranchEntity saved = repo.save(BranchJpaMapper.toEntity(branch));
        return BranchJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Branch> findById(UUID id) {
        return repo.findById(id).map(BranchJpaMapper::toDomain);
    }

    @Override
    public List<Branch> findByFranchiseId(UUID franchiseId) {
        return repo.findByFranchiseId(franchiseId).stream()
                .map(BranchJpaMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByFranchiseIdAndNameIgnoreCase(UUID franchiseId, String name) {
        return repo.existsByFranchiseIdAndNameIgnoreCase(franchiseId, name);
    }

    @Override
    public Branch updateName(UUID id, String newName) {
        BranchEntity entity = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Branch not found: " + id));
        entity.setName(newName);
        BranchEntity saved = repo.save(entity);
        return BranchJpaMapper.toDomain(saved);
    }
}
