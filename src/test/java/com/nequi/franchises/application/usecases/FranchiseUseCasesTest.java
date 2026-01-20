package com.nequi.franchises.application.usecases;


import com.nequi.franchises.domain.exceptions.BusinessRuleException;
import com.nequi.franchises.domain.exceptions.ConflictException;
import com.nequi.franchises.domain.model.Branch;
import com.nequi.franchises.domain.model.Franchise;
import com.nequi.franchises.domain.model.Product;
import com.nequi.franchises.domain.ports.BranchRepositoryPort;
import com.nequi.franchises.domain.ports.FranchiseRepositoryPort;
import com.nequi.franchises.domain.ports.ProductRepositoryPort;
import com.nequi.franchises.infrastructure.rest.dto.TopProductByBranchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FranchiseUseCasesTest {

    private FranchiseRepositoryPort franchisePort;
    private BranchRepositoryPort branchPort;
    private ProductRepositoryPort productPort;
    private FranchiseUseCases useCases;

    @BeforeEach
    void setUp() {
        franchisePort = mock(FranchiseRepositoryPort.class);
        branchPort = mock(BranchRepositoryPort.class);
        productPort = mock(ProductRepositoryPort.class);
        useCases = new FranchiseUseCases(franchisePort, branchPort, productPort);
    }

    @Test
    void createFranchise_success() {
        String name = "Test";
        when(franchisePort.existsByNameIgnoreCase("Test")).thenReturn(false);
        Franchise saved = new Franchise(UUID.randomUUID(), "Test");
        when(franchisePort.save(any())).thenReturn(saved);

        Franchise result = useCases.createFranchise(name);

        assertEquals("Test", result.name());
        verify(franchisePort).save(any());
    }

    @Test
    void createFranchise_conflict() {
        when(franchisePort.existsByNameIgnoreCase("Test")).thenReturn(true);
        assertThrows(ConflictException.class, () -> useCases.createFranchise("Test"));
    }

    @Test
    void renameFranchise_success() {
        UUID id = UUID.randomUUID();
        Franchise existing = new Franchise(id, "Old");
        when(franchisePort.findById(id)).thenReturn(Optional.of(existing));
        when(franchisePort.existsByNameIgnoreCase("New")).thenReturn(false);
        Franchise updated = new Franchise(id, "New");
        when(franchisePort.updateName(id, "New")).thenReturn(updated);

        Franchise result = useCases.renameFranchise(id, "New");

        assertEquals("New", result.name());
    }

    @Test
    void addBranch_success() {
        UUID franchiseId = UUID.randomUUID();
        when(franchisePort.findById(franchiseId)).thenReturn(Optional.of(new Franchise(franchiseId, "F")));
        when(branchPort.existsByFranchiseIdAndNameIgnoreCase(franchiseId, "Branch")).thenReturn(false);
        Branch saved = new Branch(UUID.randomUUID(), franchiseId, "Branch");
        when(branchPort.save(any())).thenReturn(saved);

        Branch result = useCases.addBranch(franchiseId, "Branch");

        assertEquals("Branch", result.name());
    }

    @Test
    void addProduct_restoreDeleted() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        when(franchisePort.findById(franchiseId)).thenReturn(Optional.of(new Franchise(franchiseId, "F")));
        Branch branch = new Branch(branchId, franchiseId, "B");
        when(branchPort.findById(branchId)).thenReturn(Optional.of(branch));
        Product deleted = new Product(UUID.randomUUID(), branchId, "Product", 0, null, true, null);
        when(productPort.findByBranchIdAndNameIgnoreCase(branchId, "Product")).thenReturn(Optional.of(deleted));
        when(productPort.save(any())).thenReturn(deleted);

        Product result = useCases.addProduct(franchiseId, branchId, "Product", 10);

        assertEquals("Product", result.name());
        assertTrue(result.deleted());
    }

    @Test
    void updateProductStock_negativeStock() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        when(franchisePort.findById(franchiseId)).thenReturn(Optional.of(new Franchise(franchiseId, "F")));
        Branch branch = new Branch(branchId, franchiseId, "B");
        when(branchPort.findById(branchId)).thenReturn(Optional.of(branch));
        Product product = new Product(productId, branchId, "P", 1, null, false, null);
        when(productPort.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(BusinessRuleException.class, () -> useCases.updateProductStock(franchiseId, branchId, productId, -1));
    }

    @Test
    void getTopProductsByBranch_returnsList() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        when(franchisePort.findById(franchiseId)).thenReturn(Optional.of(new Franchise(franchiseId, "F")));
        Branch branch = new Branch(branchId, franchiseId, "B");
        when(branchPort.findByFranchiseId(franchiseId)).thenReturn(List.of(branch));
        Product product = new Product(UUID.randomUUID(), branchId, "P", 5, null, false, null);
        when(productPort.findProductsOrderedForTopByBranches(List.of(branchId))).thenReturn(List.of(product));

        List<TopProductByBranchResponse> result = useCases.getTopProductsByBranch(franchiseId);

        assertEquals(1, result.size());
        assertEquals("P", result.get(0).productName());
    }
}