package com.nequi.franchises.infrastructure.persistence.jpa.adapter;

import com.nequi.franchises.domain.exceptions.NotFoundException;
import com.nequi.franchises.domain.model.Product;
import com.nequi.franchises.domain.ports.ProductRepositoryPort;
import com.nequi.franchises.infrastructure.persistence.jpa.entity.ProductEntity;
import com.nequi.franchises.infrastructure.persistence.jpa.mapper.ProductJpaMapper;
import com.nequi.franchises.infrastructure.persistence.jpa.repository.ProductJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository repo;

    public ProductRepositoryAdapter(ProductJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public Product save(Product product) {
        ProductEntity saved = repo.save(ProductJpaMapper.toEntity(product));
        return ProductJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return repo.findByIdAndDeletedFalse(id).map(ProductJpaMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        ProductEntity entity = repo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
        entity.setDeleted(true);
        entity.setDeletedAt(Instant.now());
        repo.save(entity);
    }

    @Override
    public Optional<Product> findByBranchIdAndNameIgnoreCase(UUID branchId, String name) {
        return repo.findByBranchIdAndNameIgnoreCase(branchId, name).map(ProductJpaMapper::toDomain);
    }

    @Override
    public Product updateName(UUID id, String newName) {
        ProductEntity entity = repo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
        entity.setName(newName);
        return ProductJpaMapper.toDomain(repo.save(entity));
    }

    @Override
    public Product updateStock(UUID id, int newStock) {
        ProductEntity entity = repo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
        entity.setStock(newStock);
        return ProductJpaMapper.toDomain(repo.save(entity));
    }

    @Override
    public List<Product> findProductsOrderedForTopByBranches(List<UUID> branchIds) {
        if (branchIds == null || branchIds.isEmpty()) return List.of();
        return repo.findByBranchIdInAndDeletedFalseOrderByBranchIdAscStockDesc(branchIds).stream()
                .map(ProductJpaMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByBranchIdAndNameIgnoreCase(UUID branchId, String name) {
        return repo.existsByBranchIdAndNameIgnoreCase(branchId, name);
    }


}
