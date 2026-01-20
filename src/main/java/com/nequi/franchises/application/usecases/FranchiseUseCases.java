package com.nequi.franchises.application.usecases;

import com.nequi.franchises.application.dto.TopProductByBranch;
import com.nequi.franchises.domain.exceptions.BusinessRuleException;
import com.nequi.franchises.domain.exceptions.ConflictException;
import com.nequi.franchises.domain.exceptions.NotFoundException;
import com.nequi.franchises.domain.model.Branch;
import com.nequi.franchises.domain.model.Franchise;
import com.nequi.franchises.domain.model.Product;
import com.nequi.franchises.domain.ports.BranchRepositoryPort;
import com.nequi.franchises.domain.ports.FranchiseRepositoryPort;
import com.nequi.franchises.domain.ports.ProductRepositoryPort;
import com.nequi.franchises.infrastructure.rest.dto.TopProductByBranchResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FranchiseUseCases {

    private final FranchiseRepositoryPort franchisePort;
    private final BranchRepositoryPort branchPort;
    private final ProductRepositoryPort productPort;

    public FranchiseUseCases(
            FranchiseRepositoryPort franchisePort,
            BranchRepositoryPort branchPort,
            ProductRepositoryPort productPort
    ) {
        this.franchisePort = franchisePort;
        this.branchPort = branchPort;
        this.productPort = productPort;
    }

    @Transactional
    public Franchise createFranchise(String name) {
        String normalized = normalizeName(name);
        if (franchisePort.existsByNameIgnoreCase(normalized)) {
            throw new ConflictException("Franchise name already exists: " + normalized);
        }
        Franchise franchise = new Franchise(UUID.randomUUID(), normalized);
        return franchisePort.save(franchise);
    }

    @Transactional
    public Franchise renameFranchise(UUID franchiseId, String newName) {
        Franchise existing = requireFranchise(franchiseId);
        String normalized = normalizeName(newName);
        if (!existing.name().equalsIgnoreCase(normalized) && franchisePort.existsByNameIgnoreCase(normalized)) {
            throw new ConflictException("Franchise name already exists: " + normalized);
        }
        return franchisePort.updateName(franchiseId, normalized);
    }

    @Transactional
    public Branch addBranch(UUID franchiseId, String name) {
        requireFranchise(franchiseId);
        String normalized = normalizeName(name);
        if (branchPort.existsByFranchiseIdAndNameIgnoreCase(franchiseId, normalized)) {
            throw new ConflictException("Branch name already exists for this franchise: " + normalized);
        }
        Branch branch = new Branch(UUID.randomUUID(), franchiseId, normalized);
        return branchPort.save(branch);
    }

    @Transactional
    public Branch renameBranch(UUID franchiseId, UUID branchId, String newName) {
        requireFranchise(franchiseId);
        Branch branch = requireBranch(branchId);
        assertBelongsToFranchise(branch, franchiseId);

        String normalized = normalizeName(newName);
        if (!branch.name().equalsIgnoreCase(normalized)
                && branchPort.existsByFranchiseIdAndNameIgnoreCase(franchiseId, normalized)) {
            throw new ConflictException("Branch name already exists for this franchise: " + normalized);
        }

        return branchPort.updateName(branchId, normalized);
    }

    @Transactional
    public Product addProduct(UUID franchiseId, UUID branchId, String name, int stock) {
        requireFranchise(franchiseId);
        Branch branch = requireBranch(branchId);
        assertBelongsToFranchise(branch, franchiseId);

        if (stock < 0) {
            throw new BusinessRuleException("Stock cannot be negative");
        }

        String normalized = normalizeName(name);

        var existingOpt = productPort.findByBranchIdAndNameIgnoreCase(branchId, normalized);
        if (existingOpt.isPresent()) {
            Product existing = existingOpt.get();
            if (!existing.deleted()) {
                throw new ConflictException("Product name already exists for this branch: " + normalized);
            }
            // Restore (undelete) keeping the same product id.
            Product restored = new Product(
                    existing.id(),
                    branchId,
                    normalized,
                    stock,
                    existing.version(),
                    false,
                    null
            );
            return productPort.save(restored);
        }

        Product product = new Product(UUID.randomUUID(), branchId, normalized, stock, null, false, null);
        return productPort.save(product);
    }

    @Transactional
    public Product renameProduct(UUID franchiseId, UUID branchId, UUID productId, String newName) {
        requireFranchise(franchiseId);
        Branch branch = requireBranch(branchId);
        assertBelongsToFranchise(branch, franchiseId);

        Product product = requireProduct(productId);
        assertBelongsToBranch(product, branchId);

        String normalized = normalizeName(newName);
        if (!product.name().equalsIgnoreCase(normalized)
                && productPort.existsByBranchIdAndNameIgnoreCase(branchId, normalized)) {
            throw new ConflictException("Product name already exists for this branch: " + normalized);
        }

        return productPort.updateName(productId, normalized);
    }

    @Transactional
    public void removeProduct(UUID franchiseId, UUID branchId, UUID productId) {
        requireFranchise(franchiseId);
        Branch branch = requireBranch(branchId);
        assertBelongsToFranchise(branch, franchiseId);

        Product product = requireProduct(productId);
        assertBelongsToBranch(product, branchId);

        productPort.deleteById(productId);
    }

    @Transactional
    public Product updateProductStock(UUID franchiseId, UUID branchId, UUID productId, int stock) {
        requireFranchise(franchiseId);
        Branch branch = requireBranch(branchId);
        assertBelongsToFranchise(branch, franchiseId);

        Product product = requireProduct(productId);
        assertBelongsToBranch(product, branchId);

        if (stock < 0) {
            throw new BusinessRuleException("Stock cannot be negative");
        }

        return productPort.updateStock(productId, stock);
    }

    @Transactional(readOnly = true)
    public List<TopProductByBranchResponse> getTopProductsByBranch(UUID franchiseId) {
        // Validar franquicia existe (regla de negocio)
        if (franchisePort.findById(franchiseId).isEmpty()) {
            throw new NotFoundException("Franchise not found: " + franchiseId);
        }

        // Traer sucursales de la franquicia
        List<Branch> branches = branchPort.findByFranchiseId(franchiseId);
        if (branches.isEmpty()) return List.of();

        List<UUID> branchIds = branches.stream().map(Branch::id).toList();

        // Traer productos de esas sucursales ordenados (branchId asc, stock desc) y excluyendo deleted
        List<Product> ordered = productPort.findProductsOrderedForTopByBranches(branchIds);

        // Elegir el top 1 por sucursal la lista ya viene ordenada por stock desc
        Map<UUID, Product> topByBranch = new LinkedHashMap<>();
        for (Product p : ordered) {
            topByBranch.putIfAbsent(p.branchId(), p);
        }

        // Construir respuesta por cada sucursal que tenga al menos un producto
        Map<UUID, String> branchNameById = branches.stream()
                .collect(Collectors.toMap(Branch::id, Branch::name));

        List<TopProductByBranchResponse> result = new ArrayList<>();
        for (var e : topByBranch.entrySet()) {
            UUID bId = e.getKey();
            Product p = e.getValue();
            result.add(new TopProductByBranchResponse(
                    bId,
                    branchNameById.getOrDefault(bId, ""),
                    p.id(),
                    p.name(),
                    p.stock()
            ));
        }

        return result;
    }

    private Franchise requireFranchise(UUID franchiseId) {
        return franchisePort.findById(franchiseId)
                .orElseThrow(() -> new NotFoundException("Franchise not found: " + franchiseId));
    }

    private Branch requireBranch(UUID branchId) {
        return branchPort.findById(branchId)
                .orElseThrow(() -> new NotFoundException("Branch not found: " + branchId));
    }

    private Product requireProduct(UUID productId) {
        return productPort.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productId));
    }

    private void assertBelongsToFranchise(Branch branch, UUID franchiseId) {
        if (!branch.franchiseId().equals(franchiseId)) {
            throw new NotFoundException("Branch " + branch.id() + " does not belong to franchise " + franchiseId);
        }
    }

    private void assertBelongsToBranch(Product product, UUID branchId) {
        if (!product.branchId().equals(branchId)) {
            throw new NotFoundException("Product " + product.id() + " does not belong to branch " + branchId);
        }
    }

    private String normalizeName(String name) {
        if (name == null) return null;
        return name.trim();
    }
}
