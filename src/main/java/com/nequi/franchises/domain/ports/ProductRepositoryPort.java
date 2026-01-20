package com.nequi.franchises.domain.ports;

import com.nequi.franchises.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {
    Product save(Product product);

    Optional<Product> findById(UUID id);

    void deleteById(UUID id);

    /**
     * Finds by branch name case-insensitive, including soft-deleted (for restore).
     */
    Optional<Product> findByBranchIdAndNameIgnoreCase(UUID branchId, String name);

    boolean existsByBranchIdAndNameIgnoreCase(UUID branchId, String name);

    Product updateName(UUID id, String newName);

    Product updateStock(UUID id, int newStock);

    /**
     * Returns a list of products for the given branch IDs, ordered by branchId then stock desc.
     * top product by branch
     */
    List<Product> findProductsOrderedForTopByBranches(List<UUID> branchIds);
}
