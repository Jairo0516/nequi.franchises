package com.nequi.franchises.infrastructure.rest.controller;


import com.nequi.franchises.application.usecases.FranchiseUseCases;
import com.nequi.franchises.domain.model.Branch;
import com.nequi.franchises.domain.model.Franchise;
import com.nequi.franchises.domain.model.Product;
import com.nequi.franchises.infrastructure.rest.dto.BranchResponse;
import com.nequi.franchises.infrastructure.rest.dto.CreateBranchRequest;
import com.nequi.franchises.infrastructure.rest.dto.CreateFranchiseRequest;
import com.nequi.franchises.infrastructure.rest.dto.CreateProductRequest;
import com.nequi.franchises.infrastructure.rest.dto.FranchiseResponse;
import com.nequi.franchises.infrastructure.rest.dto.ProductResponse;
import com.nequi.franchises.infrastructure.rest.dto.UpdateNameRequest;
import com.nequi.franchises.infrastructure.rest.dto.UpdateStockRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FranchiseControllerTest {

    private FranchiseUseCases useCases;
    private FranchiseController controller;

    @BeforeEach
    void setUp() {
        useCases = mock(FranchiseUseCases.class);
        controller = new FranchiseController(useCases);
    }

    @Test
    void createFranchise_returnsCreatedFranchise() {
        CreateFranchiseRequest request = new CreateFranchiseRequest("Test Franchise");
        Franchise franchise = new Franchise(UUID.randomUUID(), "Test Franchise");
        when(useCases.createFranchise("Test Franchise")).thenReturn(franchise);

        ResponseEntity<FranchiseResponse> response = controller.createFranchise(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(franchise.id(), response.getBody().id());
        assertEquals(franchise.name(), response.getBody().name());
    }

    @Test
    void renameFranchise_returnsUpdatedFranchise() {
        UUID id = UUID.randomUUID();
        UpdateNameRequest request = new UpdateNameRequest("Nuevo Nombre");
        Franchise franchise = new Franchise(id, "Nuevo Nombre");
        when(useCases.renameFranchise(id, "Nuevo Nombre")).thenReturn(franchise);

        FranchiseResponse response = controller.renameFranchise(id, request);

        assertEquals(id, response.id());
        assertEquals("Nuevo Nombre", response.name());
    }

    @Test
    void addBranch_returnsCreatedBranch() {
        UUID franchiseId = UUID.randomUUID();
        CreateBranchRequest request = new CreateBranchRequest("Sucursal 1");
        Branch branch = new Branch(UUID.randomUUID(), franchiseId, "Sucursal 1");
        when(useCases.addBranch(franchiseId, "Sucursal 1")).thenReturn(branch);

        ResponseEntity<BranchResponse> response = controller.addBranch(franchiseId, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(branch.id(), response.getBody().id());
        assertEquals(branch.name(), response.getBody().name());
    }

    @Test
    void renameBranch_returnsUpdatedBranch() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        UpdateNameRequest request = new UpdateNameRequest("Sucursal Renombrada");
        Branch branch = new Branch(branchId, franchiseId, "Sucursal Renombrada");
        when(useCases.renameBranch(franchiseId, branchId, "Sucursal Renombrada")).thenReturn(branch);

        BranchResponse response = controller.renameBranch(franchiseId, branchId, request);

        assertEquals(branchId, response.id());
        assertEquals("Sucursal Renombrada", response.name());
    }

    @Test
    void addProduct_returnsCreatedProduct() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        CreateProductRequest request = new CreateProductRequest("Producto 1", 10);
        Product product = new Product(UUID.randomUUID(), branchId, "Producto 1", 10, null, false, null);
        when(useCases.addProduct(franchiseId, branchId, "Producto 1", 10)).thenReturn(product);

        ResponseEntity<ProductResponse> response = controller.addProduct(franchiseId, branchId, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(product.id(), response.getBody().id());
        assertEquals(product.name(), response.getBody().name());
        assertEquals(product.stock(), response.getBody().stock());
    }

    @Test
    void renameProduct_returnsUpdatedProduct() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UpdateNameRequest request = new UpdateNameRequest("Producto Renombrado");
        Product product = new Product(productId, branchId, "Producto Renombrado", 5, null, false, null);
        when(useCases.renameProduct(franchiseId, branchId, productId, "Producto Renombrado")).thenReturn(product);

        ProductResponse response = controller.renameProduct(franchiseId, branchId, productId, request);

        assertEquals(productId, response.id());
        assertEquals("Producto Renombrado", response.name());
    }

    @Test
    void removeProduct_returnsOk() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        ResponseEntity<Object> response = controller.removeProduct(franchiseId, branchId, productId);

        verify(useCases).removeProduct(franchiseId, branchId, productId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateStock_returnsUpdatedProduct() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UpdateStockRequest request = new UpdateStockRequest(20);
        Product product = new Product(productId, branchId, "Producto", 20, null, false, null);
        when(useCases.updateProductStock(franchiseId, branchId, productId, 20)).thenReturn(product);

        ProductResponse response = controller.updateStock(franchiseId, branchId, productId, request);

        assertEquals(20, response.stock());
    }

}