package com.nequi.franchises.infrastructure.persistence.jpa.adapter;

import com.nequi.franchises.domain.exceptions.NotFoundException;
import com.nequi.franchises.domain.model.Franchise;
import com.nequi.franchises.domain.ports.FranchiseRepositoryPort;
import com.nequi.franchises.infrastructure.persistence.jpa.entity.FranchiseEntity;
import com.nequi.franchises.infrastructure.persistence.jpa.mapper.FranchiseJpaMapper;
import com.nequi.franchises.infrastructure.persistence.jpa.repository.FranchiseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class FranchiseRepositoryAdapter implements FranchiseRepositoryPort {

    private final FranchiseJpaRepository repo;

    public FranchiseRepositoryAdapter(FranchiseJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public Franchise save(Franchise franchise) {
        FranchiseEntity saved = repo.save(FranchiseJpaMapper.toEntity(franchise));
        return FranchiseJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Franchise> findById(UUID id) {
        return repo.findById(id).map(FranchiseJpaMapper::toDomain);
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return repo.existsByNameIgnoreCase(name);
    }

    @Override
    public Franchise updateName(UUID id, String newName) {
        FranchiseEntity entity = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Franchise not found: " + id));
        entity.setName(newName);
        FranchiseEntity saved = repo.save(entity);
        return FranchiseJpaMapper.toDomain(saved);
    }
}
